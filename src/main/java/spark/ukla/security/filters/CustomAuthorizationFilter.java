package spark.ukla.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import spark.ukla.repositories.TokenRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomAuthorizationFilter extends OncePerRequestFilter {

	final TokenRepository tokenRepository;

	public CustomAuthorizationFilter(TokenRepository tokenRepository) {


		this.tokenRepository= tokenRepository;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
			throws ServletException, IOException {

		if (request.getServletPath().equals("/login") || request.getServletPath().equals("/registration/refreshtoken")) {
			filterChain.doFilter(request, response);
		} else {
			String authorizationHeader = request.getHeader(AUTHORIZATION);
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ) {
				String token = authorizationHeader.substring("Bearer ".length());
				var isTokenValid = tokenRepository.findByToken(token)
						.map(t -> !t.isExpired() && !t.isRevoked())
						.orElse(false);

				if (isTokenValid) {
					try {
						Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
						JWTVerifier verifier = JWT.require(algorithm).build();
						DecodedJWT decodedJWT = verifier.verify(token);
						String username = decodedJWT.getSubject();
						String[] roles = decodedJWT.getClaim("role").asArray(String.class);
						Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
						stream(roles).forEach(role -> {
							authorities.add(new SimpleGrantedAuthority(role));
						});

						UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
								username, null, authorities);
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
						filterChain.doFilter(request, response);
					} catch (TokenExpiredException expiredException) {
						// Token is expired, return 401 Unauthorized
						log.error("Token expired: {}", expiredException.getMessage());
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.getWriter().write("Token expired");
					} catch (Exception exception) {
						log.error("Error logging in : {}", exception.getMessage());
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.getWriter().write("Unauthorized");
					}
				} else {
					// Token is not valid, return 410 Gone
					response.setStatus(HttpServletResponse.SC_GONE);
					response.getWriter().write("Invalid token");
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}
	}



}
