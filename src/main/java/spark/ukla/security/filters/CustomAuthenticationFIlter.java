package spark.ukla.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spark.ukla.entities.Token;
import spark.ukla.entities.enums.TokenType;
import spark.ukla.repositories.TokenRepository;
import spark.ukla.repositories.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomAuthenticationFIlter extends UsernamePasswordAuthenticationFilter {
	private final	AuthenticationManager authenticationManager;

	private final	UserRepository userRepository;

	private final	TokenRepository tokenRepository;

	public CustomAuthenticationFIlter(AuthenticationManager authenticationManager,UserRepository userRepository,TokenRepository tokenRepository) {
		this.authenticationManager = authenticationManager;
		this.userRepository=userRepository;
		this.tokenRepository= tokenRepository;
	}


	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		log.info("Username is {}.", username);
		log.info("Password is hidden.");
		spark.ukla.entities.User user = userRepository.findByUsername(username);
		if(user == null){
			throw new BadCredentialsException("username or password are wrong");
		}
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,

			Authentication authentication) throws IOException {
		User user = (User) authentication.getPrincipal();
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		String access_token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))   //expires after 24 hours
				.withIssuer(request.getRequestURI())
				.withClaim("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		String refresh_token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000* 180)) // expires after 6 months
				.withIssuer(request.getRequestURI())
				.sign(algorithm);
		this.deleteOldValidTokens(userRepository.findByUsername(user.getUsername()));
		var tokenU= Token.builder()
				.user(userRepository.findByUsername(user.getUsername()))
				.token(access_token)
				.tokenType(TokenType.BEARER)
				.expired(false)
				.revoked(false)
				.build();
		tokenRepository.save(tokenU);
		//afficher les tokens dans Body
		Map<String , String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}
	private void deleteOldValidTokens(spark.ukla.entities.User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		tokenRepository.deleteAllInBatch(validUserTokens);
	}
}