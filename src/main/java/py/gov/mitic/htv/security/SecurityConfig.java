package py.gov.mitic.htv.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import py.gov.mitic.htv.exceptions.JwtAuthenticationEntryPoint;
import py.gov.mitic.htv.exceptions.JwtAccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Autowired
	private UserDetailsService userDetailService;

	@Autowired
	private SecurityFilter securityFilter;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authProvider);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/set-password/**").permitAll()
						.requestMatchers("/archivo/imagen/**").permitAll() // Endpoint público para servir imágenes
						.requestMatchers("/api/auth/file-validations/**")
						.permitAll() // Endpoint público para validaciones
						// de archivos
						.requestMatchers("/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/webjars/**",
								"/api/v3/api-docs/**",
								"/api/swagger-ui/**",
								"/api/swagger-ui.html",
								"/api/webjars/**")
						.permitAll()
						.requestMatchers("/api/usuario/**").hasAuthority("usuarios:ver")
						.requestMatchers("/api/rol/**").hasAuthority("roles:ver")
						.requestMatchers("/api/permiso/**").hasAuthority("permisos:ver")
						.anyRequest().authenticated())
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(jwtAuthenticationEntryPoint)
						.accessDeniedHandler(jwtAccessDeniedHandler))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public DefaultWebSecurityExpressionHandler customWebSecurityExpressionHandler() {
		DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
		handler.setPermissionEvaluator(new PermissionChecker());
		return handler;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
				.requestMatchers(
						new AntPathRequestMatcher("/auth/login"),
						new AntPathRequestMatcher("/auth/set-password"),
						new AntPathRequestMatcher("/auth/refreshToken"),
						new AntPathRequestMatcher("/auth/reset-password"),
						new AntPathRequestMatcher("/auth/logout"),
						new AntPathRequestMatcher("/v3/api-docs/**"),
						new AntPathRequestMatcher("/swagger-ui/**"),
						new AntPathRequestMatcher("/swagger-ui.html"),
						new AntPathRequestMatcher("/webjars/**"),
						new AntPathRequestMatcher("/api/v3/api-docs/**"),
						new AntPathRequestMatcher("/api/swagger-ui/**"),
						new AntPathRequestMatcher("/api/swagger-ui.html"),
						new AntPathRequestMatcher("/api/webjars/**"),
						new AntPathRequestMatcher("/auth/validateUserIE"),
						new AntPathRequestMatcher("/auth/file-validations"),
						new AntPathRequestMatcher("/auth/urlIE"))
				.requestMatchers(HttpMethod.OPTIONS, "/**");
	}
}