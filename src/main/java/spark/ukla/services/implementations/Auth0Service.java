package spark.ukla.services.implementations;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spark.ukla.entities.Token;
import spark.ukla.entities.User;
import spark.ukla.entities.enums.AuthentificationProvider;
import spark.ukla.entities.enums.Role;
import spark.ukla.entities.enums.TokenType;
import spark.ukla.repositories.TokenRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.IAuth0Service;
import spark.ukla.utils.SignInObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class Auth0Service implements IAuth0Service {

    private  final UserRepository userRepository ;
    private final    RegistrationService registrationService;
    private final TokenRepository tokenRepository;
    JsonFactory jsonFactory = new JacksonFactory();
    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    HttpTransport httpTransport = new NetHttpTransport();

    public Auth0Service(UserRepository userRepository, RegistrationService registrationService, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
        this.tokenRepository = tokenRepository;
    }


    @Override
    public ResponseEntity checkGoogleIdTokenIntegrityThenSigninOrSignup(String idtoken) throws GeneralSecurityException, IOException {


        GoogleIdToken googleIdToken = verifyIntegrity(idtoken) ;


        if (googleIdToken == null) {
            // The ID token not valid

            return new ResponseEntity<>("there is a problem with google signin ", HttpStatus.NOT_ACCEPTABLE);

        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        String subjectId = payload.getSubject();
        String email = payload.getEmail();
        boolean existsByEmail = userRepository.existsByEmail(email);
        User userExist = userRepository.findBySubjectId(subjectId) ;
        if (userExist==null) {
            if(existsByEmail){
                User userExistByEmail = userRepository.findByEmail(email);
                if(userExistByEmail.getProvider() == AuthentificationProvider.USERNAME_PASSWORD){
                    return new ResponseEntity<>("link account", HttpStatus.TEMPORARY_REDIRECT);
                }
            }
            return new ResponseEntity<>(" get user info (username + birthdate + birth gender + idtoken )   ", HttpStatus.ACCEPTED);

        }

        return sendAuthenticationRequest(userExist) ;


    }

    @Override
    public ResponseEntity createAccount(SignInObject signInObject) throws GeneralSecurityException, IOException {

        GoogleIdToken googleIdToken = verifyIntegrity(signInObject.getIdtoken()) ;


        if (googleIdToken == null) {
            // The ID token not valid
            return new ResponseEntity<>("there is a problem with google signin ", HttpStatus.NOT_ACCEPTABLE);
        }
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        String subjectId = payload.getSubject();
        String firstName = payload.get("given_name").toString();
        String lastName = payload.get("family_name").toString();


        User userExist = userRepository.findBySubjectId(subjectId) ;
        if (userExist!=null) {
            return new ResponseEntity<>(" account already exists   ", HttpStatus.IM_USED);
        }
        // todo check for username usage right here.

        User user = new User() ;
        user.setRole(Role.USER);
        user.setSubjectId(subjectId);
        user.setEmail(payload.getEmail());
        user.setProvider(AuthentificationProvider.GOOGLE);
        user.setGender(signInObject.getGender());
        user.setUsername(signInObject.getUsername());
        user.setBirthdate(signInObject.getBirthdate());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("dummypassword**147");


        //  now when creating a Google account emails need to be verified this is only for old account,
        //  so i don't have a way of testing it but theoretically it will work since it's calling the same method as email signin
        if(!payload.getEmailVerified()){

            registrationService.register(user) ;
            return new ResponseEntity<>("user saved, verify email", HttpStatus.CREATED);

        }
        User savedUser = registrationService.registerWithOutEmailVerification(user) ;

        return sendAuthenticationRequest(savedUser) ;

    }

    @Override
    public ResponseEntity linkAccount(String idtoken, String password) throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = verifyIntegrity(idtoken) ;
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String subjectId = payload.getSubject();
        String email = payload.getEmail();
        User user = userRepository.findByEmail(email);
        if (googleIdToken == null) {
            return new ResponseEntity<>("there is a problem with google signin ", HttpStatus.NOT_ACCEPTABLE);
        }
        boolean isPasswordMatch = passwordEncoder.matches(password, user.getPassword());
        if (!isPasswordMatch) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
        }
        user.setProvider(AuthentificationProvider.GOOGLE);
        user.setSubjectId(subjectId);
        userRepository.save(user);
        return sendAuthenticationRequest(user);
    }


    public GoogleIdToken verifyIntegrity(String idtoken) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                //  the CLIENT_ID of the app (web )
                .setAudience(Collections.singletonList("176674262123-tplcpaa73ghvfp32n55da3hc66ivfnui.apps.googleusercontent.com"))
                .build();

        return verifier.verify(idtoken);
    }

    public
    ResponseEntity sendAuthenticationRequest(User user) throws IOException {

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 1000))
                .withClaim("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .sign(algorithm);
        deleteOldValidTokens(user);
        var token = Token.builder()
                .user(user)
                .token(access_token)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);


        Map<String , String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);



        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    private void deleteOldValidTokens(spark.ukla.entities.User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        tokenRepository.deleteAllInBatch(validUserTokens);
    }

}
