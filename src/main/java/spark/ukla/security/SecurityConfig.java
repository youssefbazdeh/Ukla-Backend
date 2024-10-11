package spark.ukla.security;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import spark.ukla.repositories.TokenRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.security.filters.CustomAuthenticationEntryPoint;
import spark.ukla.security.filters.CustomAuthenticationFIlter;
import spark.ukla.security.filters.CustomAuthorizationFilter;
import spark.ukla.security.oauth2.OAuthUserService;
import spark.ukla.services.implementations.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final UserDetailsService userDetailsService;
	@Autowired
	 UserRepository userRepository;
	@Autowired
	TokenRepository tokenRepository;
	@Autowired
	CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final UserService userService ;
	 final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private final LogoutHandler logoutHandler;

	private final OAuthUserService oAuthUserService ;

	public SecurityConfig(UserDetailsService userDetailsService, UserService userService, LogoutHandler logoutHandler, OAuthUserService oAuthUserService) {
		this.userDetailsService = userDetailsService;
		this.userService = userService;
		this.logoutHandler = logoutHandler;
		this.oAuthUserService = oAuthUserService;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.cors();



		http.authorizeRequests().antMatchers("/", "/login","/registration/**" ,"/auth0/**", "/oauth/**","/registration/refreshtoken/**","/wait/**","/forgetPassword/**","/creator/add","/registration/usernameexists").permitAll()
				.antMatchers("/Allergies/add","/Allergies/updateImage/{allergyId}","/Recipe/forceDeleteRecipe/{id}","/Allergies/delete/{id}","/ingredient/add","/ingredient/update","/ingredient/updateimage/{ingredientId}","/ingredient/delete/{id}","/ingredient/import","/tag/add","/user/retrieveAll","/user/setRole/{id}/{role}","/user/unlockUser/{id}").hasAnyRole("ADMIN","SUPERADMIN")
				.antMatchers("/Recipe/addv2","/Recipe/update/{id}","/Recipe/delete/{id}","/Recipe/deleteStep/{idRecipe}/{idStep}").hasAnyRole("CREATOR","ADMIN","SUPERADMIN")
				.antMatchers("/**").authenticated()


				.anyRequest().authenticated()
		.and().oauth2Login().userInfoEndpoint()
		.userService(oAuthUserService).and()

		.successHandler(new AuthenticationSuccessHandler() {

						@Override
						public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
															Authentication authentication) throws IOException {


							OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

							String mail = oauthUser.getAttribute("email") ;

							System.out.println(	mail);
							userService.processOAuthPostLogin(oauthUser.getAttribute("email")); // to rework

							response.sendRedirect("/ukla/list");
						}
				})
		;


		http.addFilter(new CustomAuthenticationFIlter(authenticationManagerBean(), userRepository,tokenRepository));
		http.addFilterBefore(new CustomAuthorizationFilter(tokenRepository), UsernamePasswordAuthenticationFilter.class)
		  .logout()
				.logoutUrl("/logout")
				.addLogoutHandler(logoutHandler)
				.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
		;

	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}



}
