package spark.ukla.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import spark.ukla.DTO.UserDTO;
import spark.ukla.entities.Token;
import spark.ukla.entities.User;
import spark.ukla.entities.enums.TokenType;
import spark.ukla.repositories.TokenRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.security.filters.CustomAuthenticationFIlter;
import spark.ukla.services.implementations.RegistrationService;
import spark.ukla.services.implementations.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/registration")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationController {

	private final RegistrationService registrationService;
	private final UserService usersService;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;

	public RegistrationController(RegistrationService registrationService, UserService usersService, AuthenticationManager authenticationManager, UserRepository userRepository, TokenRepository tokenRepository) {
		this.registrationService = registrationService;
		this.usersService = usersService;
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
	}

	@PostMapping("/user")
	public ResponseEntity add(@Valid @RequestBody User user) {

		return registrationService.register(user);

	}

	@GetMapping("/confirm")
	public ResponseEntity<String> confirm(@RequestParam("token") String token) {
		if( registrationService.confirmToken(token)=="confirmed"){
			return new ResponseEntity<>("confirmed", HttpStatus.OK);
		}
		if( registrationService.confirmToken(token)=="token not found"){
			return new ResponseEntity<>("token not found", HttpStatus.NOT_FOUND);
		}
		else if(registrationService.confirmToken(token)=="email already confirmed" ){
			return new ResponseEntity<>("email already confirmed", HttpStatus.FOUND);
		}
		else {
			return new ResponseEntity<>("token expired", HttpStatus.NOT_ACCEPTABLE);
		}
	}


	@PostMapping("/resend/{email}")
	@ResponseBody
	public void resendActivationCode(@PathVariable(value = "email") String email) {registrationService.resendActivationCode(email);}

	@PostMapping("/Login2")
	public ResponseEntity<String> authenticateUser(@RequestBody User user) {
		if (usersService.retrieveByUsername(user.getUsername())==null) {
			return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
		}
		else 		{
			CustomAuthenticationFIlter authFilter=new CustomAuthenticationFIlter(authenticationManager,userRepository,tokenRepository);
			return new ResponseEntity<>(authFilter.getUsernameParameter(), HttpStatus.OK);
		}
	}
	@GetMapping("/refreshtoken")
	public void RefreshToken(HttpServletRequest request, HttpServletResponse response) {
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ) {
				try {
					String refreshtoken = authorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(refreshtoken);
					String username = decodedJWT.getSubject();
					User user = usersService.findByUsername(username);


					String access_token = JWT.create()
							.withSubject(user.getUsername())
							.withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
							.withIssuer(request.getRequestURI().toString())
							.withClaim("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
							.sign(algorithm);


					var token= Token.builder()
							.user(userRepository.findByUsername(user.getUsername()))
							.token(access_token)
							.tokenType(TokenType.BEARER)
							.expired(false)
							.revoked(false)
							.build();
					tokenRepository.save(token);

					Map<String , String> tokens = new HashMap<>();
					tokens.put("access_token", access_token);
					tokens.put("refresh_token", refreshtoken);
					response.setContentType("application/json");
					new ObjectMapper().writeValue(response.getOutputStream(), tokens);

				} catch (Exception exception) {
					response.setHeader("Error", exception.getMessage());

				}

		}else
		{
			throw  new RuntimeException("refresh token is missing !!");
		}
	}
	@PostMapping("/LoginTest")
	public ResponseEntity<String> retrieveByUsernameL(@RequestBody UserDTO userDTO) {

		UserDTO userRetreived = usersService.retrieveByUsername(userDTO.getUsername());
		if ((userRetreived != null)&&(!usersService.checkIfPasswordIsCorrect(userDTO.getUsername(),userDTO.getPassword()))){


			return new ResponseEntity<>("wrong password", HttpStatus.NOT_ACCEPTABLE);
		}

		else if (userRetreived == null)
			return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<>("OK", HttpStatus.OK);

	}
	@PostMapping("/usernameexists")
	public ResponseEntity<String> checkUsernameExists(@RequestBody String username) {
		if (usersService.checkUsernameExists(username)){
			return new ResponseEntity<>( HttpStatus.NOT_ACCEPTABLE);
		}
		else
			return new ResponseEntity<>( HttpStatus.OK);
	}
}


