package py.gov.mitic.htv.controller;

import jakarta.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import py.gov.mitic.htv.dto.auth.*;
import py.gov.mitic.htv.dto.OrganizacionDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.token.TokenRefreshRequest;
import py.gov.mitic.htv.dto.token.TokenRefreshResponse;
import py.gov.mitic.htv.util.Util;
import py.gov.mitic.htv.repository.TokenRepository;
import py.gov.mitic.htv.security.TokenManager;
import py.gov.mitic.htv.exceptions.UnauthorizedException;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.service.ArchivoService;
import py.gov.mitic.htv.service.AuthService;
import py.gov.mitic.htv.service.AuthenticationService;
import py.gov.mitic.htv.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthenticationController {

	private Log logger = LogFactory.getLog(AuthenticationController.class);
	private final TokenManager tokenComponent;
	private final AuthenticationManager authManager;
	private final UsuarioService usuarioService;

	@Autowired(required = true)
	TokenRepository tokenRepository;

	@Autowired
	AuthService authService;

	@Autowired
	private ArchivoService archivoService;

	@Autowired
	private AuthenticationService authenticationService;

	public AuthenticationController(
			@Qualifier("tokenConfig") TokenManager tokenComponent,
			AuthenticationManager authManager,
			UsuarioService usuarioService) {
		this.tokenComponent = tokenComponent;
		this.authManager = authManager;
		this.usuarioService = usuarioService;
	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authReq)
			throws UsernameNotFoundException, JsonProcessingException {
		try {
			/**
			 * validacion de credenciales de acceso
			 */
			Usuario usuario = usuarioService.getUserSession(authReq.getUsername());
			if (usuario == null) {
				throw new UnauthorizedException("Usuario no encontrado");
			}

			authManager.authenticate(
					new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword()));
			// Se valida y se inserta en auditoría
			UserDetails userDetails = usuarioService.loadUserByUsernameForAudit(usuario.getUsername());

			// Obtener la organización del usuario
			OrganizacionDTO organizacionDTO = authenticationService.getOrganizacionDTOByUsuario(usuario);
			Long idOrganizacion = (organizacionDTO != null) ? organizacionDTO.getIdOrganizacion() : null;

			/**
			 * creacion de token de acceso y de actualizacion
			 */
			String accessToken = tokenComponent.generateToken(userDetails, usuario.getIdUsuario(), idOrganizacion);
			String refreshToken = tokenComponent.createRefreshToken(accessToken);

			/**
			 * consulta datos del usuario
			 */
			tokenRepository.create(new UsuarioSession(usuario.getUsername(), accessToken));

			usuario.setRoles(authenticationService.cargarRolesSinPermisos(usuario));

			return ResponseEntity.ok(new AuthenticationResponse(
					accessToken, usuario, organizacionDTO, userDetails.getAuthorities(), refreshToken));

		} catch (BadCredentialsException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
			throw new UnauthorizedException("Usuario y/o contraseña inválidos");
		}
	}

	@PostMapping(value = "/refreshToken", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
		try {
			String requestAccessToken = tokenRefreshRequest.getAccessToken();
			String requestRefreshToken = tokenRefreshRequest.getRefreshToken();

			final UsuarioSession sessionCache = this.tokenRepository.get(requestAccessToken);
			if (Objects.isNull(sessionCache)) {
				throw new UnauthorizedException("Token de actualización expirado");
			}

			/**
			 * verificamos que el token sea valido y que no haya expirado
			 */
			String usernameFromToken = tokenComponent.getUsernameFromToken(requestRefreshToken);
			UserDetails userDetails = usuarioService.loadUserByUsername(usernameFromToken);

			/**
			 * verificamos la session del usuario
			 */
			Usuario usuario = usuarioService.getUserSession(userDetails.getUsername());

			// Obtener la organización del usuario
			OrganizacionDTO organizacionDTO = authenticationService.getOrganizacionDTOByUsuario(usuario);
			Long idOrganizacion = (organizacionDTO != null) ? organizacionDTO.getIdOrganizacion() : null;

			/**
			 * accessToken: generamos un nuevo token de acceso
			 * refreshToken: generamos un refresh token
			 */
			String accessToken = tokenComponent.generateToken(userDetails, usuario.getIdUsuario(), idOrganizacion);
			String refreshToken = tokenComponent.createRefreshToken(requestRefreshToken);

			/**
			 * limpiamos la sesion actual
			 */
			tokenRepository.delete(requestAccessToken);

			/**
			 * actualizamos la nueva session
			 */
			tokenRepository.create(new UsuarioSession(usuario.getUsername(), accessToken));

			return ResponseEntity.ok(new TokenRefreshResponse(
					accessToken, refreshToken));
		} catch (BadCredentialsException e) {
			e.printStackTrace();
			throw new UnauthorizedException("Usuario y/o contraseña inválidos");
		}
	}

	@PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response,
			@Valid @RequestBody LogoutRequest req) {
		try {
			// Validar que el token no sea nulo o vacío
			if (req == null || req.getToken() == null || req.getToken().trim().isEmpty()) {
				logger.warn("Intento de logout con token nulo o vacío");
				throw new BadRequestException("El token es requerido");
			}

			String token = req.getToken().trim();

			// Verificar que el token exista antes de intentar eliminarlo
			UsuarioSession sessionCache = tokenRepository.get(token);
			if (sessionCache == null) {
				logger.warn("Intento de logout con token inexistente o ya eliminado: "
						+ token.substring(0, Math.min(10, token.length())) + "...");
				// Aunque el token no exista, consideramos el logout como exitoso por seguridad
				// para evitar que un atacante descubra qué tokens son válidos
				return ResponseEntity.ok(new ResponseDTO("Sesión cerrada exitosamente", HttpStatus.OK));
			}

			// Eliminar el token de la sesión
			tokenRepository.delete(token);

			logger.info("Logout exitoso para usuario: " + sessionCache.getUsername());

			return ResponseEntity.ok(new ResponseDTO("Sesión cerrada exitosamente", HttpStatus.OK));

		} catch (BadRequestException e) {
			logger.error("Error de validación en logout: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Error inesperado durante el logout: " + e.getMessage(), e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO("Ocurrió un error al cerrar la sesión", HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/urlIE")
	public ResponseEntity<?> getUrlIE() {
		return ResponseEntity.ok(authenticationService.getUrlIE());
	}

	@PostMapping(value = "/validateUserIE", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> doValidate(HttpServletRequest request, @RequestBody LoginIdentidadElectronicaDTO dto) {
		try {
			ResponseDTO response = authenticationService.validatorAccess(dto);
			if (response.getStatus() != HttpStatus.OK)
				throw new UnauthorizedException(response.getMessage());

			// cast Identity Person
			IdentidadPersonaDTO ieDTO = (IdentidadPersonaDTO) response.getData();
			ieDTO.setCode(dto.getCode());

			// verify document number
			if (ieDTO.getSub() == null || ieDTO.getSub().isEmpty() || !Util.isNumeric(ieDTO.getSub()))
				throw new BadRequestException("No se pudo identificar al ciudadano");

			Usuario usuario = usuarioService.getUserNroDocumentoSession(ieDTO.getSub());
			if (usuario == null) {
				throw new BadRequestException("Usuario no encontrado");
			}

			if (usuario.getPassword() == null) {
				throw new BadRequestException("Contraseña no disponible.");
			}

			if (!usuario.getEstado()) {
				throw new BadRequestException("El usuario está inactivo.");
			}

			Date date = new Date();
			if (usuario.getFechaExpiracion() != null && usuario.getFechaExpiracion().before(date)) {
				throw new BadRequestException("Usuario inactivo por fecha de expiración.");
			}

			// detalles del usuario
			UserDetails userDetails = usuarioService.loadUserByUsername(usuario.getUsername());
			if (userDetails.getAuthorities().isEmpty()) {
				throw new BadRequestException(
						"El usuario ".concat(usuario.getUsername()).concat(" no cuenta con roles asociados"));
			}

			// Obtener la organización del usuario
			OrganizacionDTO organizacionDTO = authenticationService.getOrganizacionDTOByUsuario(usuario);
			Long idOrganizacion = (organizacionDTO != null) ? organizacionDTO.getIdOrganizacion() : null;

			String accessToken = tokenComponent.generateToken(userDetails, usuario.getIdUsuario(), idOrganizacion);
			String refreshToken = tokenComponent.createRefreshToken(accessToken);

			tokenRepository.create(new UsuarioSession(usuario.getUsername(), accessToken));

			return ResponseEntity.ok(new AuthenticationResponse(
					accessToken,
					usuario,
					organizacionDTO,
					userDetails.getAuthorities(),
					refreshToken));

		} catch (BadCredentialsException e) {
			e.printStackTrace();
			throw new UnauthorizedException("Usuario y/o contraseña inválidos");
		}
	}

	@PostMapping("/set-password")
	public ResponseEntity<?> setPassword(@Valid @RequestBody SetPasswordRequestDTO request) {
		try {
			authService.setPassword(request.getToken(), request.getNewPassword());
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestParam("email") String email) {
		try {
			authService.resetPassword(email);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
		}
	}

	@GetMapping(value = "file-validations", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getFileValidations() {
		return archivoService.getFileValidation();
	}

}