package py.gov.mitic.htv.security;

import java.io.IOException;
import java.util.Objects;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import py.gov.mitic.htv.dto.auth.UsuarioSession;
import py.gov.mitic.htv.repository.TokenRepository;
import py.gov.mitic.htv.service.UsuarioService;
/**
 * @autor: Luis Cardozo
 **/
@Component
public class SecurityFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	private final TokenManager tokenComponent;
	private final TokenRepository tokenRepository;
	private final UsuarioService usuarioService;

	public SecurityFilter(@NonNull TokenManager tokenComponent, @NonNull TokenRepository tokenRepository, @NonNull UsuarioService usuarioService) {
		this.tokenComponent = tokenComponent;
		this.tokenRepository = tokenRepository;
		this.usuarioService = usuarioService;
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain chain) throws ServletException, IOException {
		try {
			final String reqHeader = request.getHeader(AUTHORIZATION_HEADER);
			if (Objects.isNull(reqHeader) || !reqHeader.startsWith(TOKEN_PREFIX)) {
				chain.doFilter(request, response);
				return;
			}

			final UsuarioSession currentUser = tokenComponent.validateUserToken(reqHeader);

			if (Objects.nonNull(currentUser) && Objects.nonNull(currentUser.getUsername()) && !currentUser.getUsername().isEmpty()) {
				final UsuarioSession sessionCache = this.tokenRepository.get(reqHeader.split("\\s")[1]);

				if(Objects.nonNull(sessionCache)) {
					UserDetails userDetails = this.usuarioService.loadUserByUsername(currentUser.getUsername());

					if (tokenComponent.validateToken(currentUser.getToken(), userDetails.getUsername())) {
						UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities()
						);

						userPassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(userPassAuthToken);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: " + e.getMessage());
		}

		chain.doFilter(request, response);
	}
}