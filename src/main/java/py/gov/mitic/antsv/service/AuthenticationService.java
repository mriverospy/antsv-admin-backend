package py.gov.mitic.htv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import py.gov.mitic.htv.dto.auth.IdentidadPersonaDTO;
import py.gov.mitic.htv.dto.auth.LoginIdentidadElectronicaDTO;
import py.gov.mitic.htv.dto.OrganizacionDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.exceptions.UnauthorizedException;
import py.gov.mitic.htv.model.Organizacion;
import py.gov.mitic.htv.model.Rol;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.repository.UsuarioOrganizacionRepository;
import py.gov.mitic.htv.repository.UsuarioRepository;
import py.gov.mitic.htv.security.TokenManager;
import py.gov.mitic.htv.util.MapperUtil;
import org.modelmapper.ModelMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
/**
 * @autor: Luis Cardozo
 **/
@Service
public class AuthenticationService {

    private static final Log logger = LogFactory.getLog(AuthenticationService.class);

    @Autowired
    @Qualifier("tokenConfig")
    private TokenManager tokenComponent;

    @Autowired
    private UsuarioOrganizacionRepository usuarioOrganizacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    ArchivoService archivoService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * Autentica con el servicio de Identidad Electrónica usando el código de autorización
     * @param code Código de autorización recibido del callback de Identidad Electrónica
     * @return Token de acceso como String (JSON)
     */
    private String authenticationUserIE(String code) {
        logger.info("Iniciando autenticación con Identidad Electrónica para code: " + (code != null ? code.substring(0, Math.min(10, code.length())) + "..." : "null"));
        
        try {
            if (code == null || code.trim().isEmpty()) {
                logger.warn("El código de autorización es nulo o vacío");
                throw new BadRequestException("El código de autorización es requerido");
            }

            String url = tokenComponent.getUrlAuthentication() +
                    "/authentication" +
                    "?client_id=" +
                    tokenComponent.getClientId() +
                    "&client_secret=" +
                    tokenComponent.getClientSecret() +
                    "&grant_type=authorization_code" +
                    "&code=" +
                    code;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Objects.requireNonNull(List.of(MediaType.APPLICATION_JSON)));
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> response = restTemplate.exchange(
                url, Objects.requireNonNull(HttpMethod.POST), new HttpEntity<>(headers), String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Autenticación exitosa con Identidad Electrónica");
                return response.getBody();
            } else {
                logger.warn("Error en la autenticación con Identidad Electrónica. Status: " + response.getStatusCode());
                return null;
            }

        } catch (BadRequestException e) {
            logger.error("Error de validación en authenticationUserIE: " + e.getMessage());
            throw e;
        } catch (Exception ex) {
            logger.error("Error inesperado durante la autenticación con Identidad Electrónica: " + ex.getMessage(), ex);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Valida el acceso usando Identidad Electrónica y retorna los datos de la persona
     * @param dto DTO con el código de autorización
     * @return ResponseDTO con IdentidadPersonaDTO en el data
     */
    public ResponseDTO validatorAccess(LoginIdentidadElectronicaDTO dto) {
        try {
            if (dto == null || dto.getCode() == null || dto.getCode().trim().isEmpty()) {
                logger.warn("Intento de validación sin código de autorización");
                throw new BadRequestException("El código de autorización es requerido");
            }

            // Paso 1: Autenticar con el servicio de IE y obtener el token
            String tokenResponse = authenticationUserIE(dto.getCode());
            if (tokenResponse == null || tokenResponse.trim().isEmpty()) {
                logger.error("No se pudo obtener el token de autenticación de Identidad Electrónica");
                throw new UnauthorizedException("Error al autenticar con Identidad Electrónica");
            }

            // Paso 2: Extraer el access_token de la respuesta
            String accessToken = null;
            String trimmedResponse = tokenResponse.trim();
            
            // Detectar si la respuesta es directamente un token JWT 
            if (trimmedResponse.startsWith("eyJ")) {
                logger.info("token JWT encontrado");
                accessToken = trimmedResponse;
                
            } else {
                // Intentar parsear como JSON
                try {
                    Map<String, Object> tokenMap = objectMapper.readValue(tokenResponse, new TypeReference<Map<String, Object>>() {});
                    if (tokenMap != null) {
                        accessToken = (String) tokenMap.get("access_token");
                        if (accessToken == null) {
                            // Intentar con diferentes nombres de campo comunes
                            accessToken = (String) tokenMap.get("accessToken");
                            if (accessToken == null) {
                                accessToken = (String) tokenMap.get("token");
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    logger.warn("Error al parsear la respuesta como JSON: " + e.getMessage() + ". Intentando como token directo...");
                    // Si falla el parseo JSON, intentar usar la respuesta directamente como token
                    // (puede que el servicio devuelva el token sin comillas o con algún formato especial)
                    if (trimmedResponse.length() > 50) { // Los tokens JWT suelen ser largos
                        accessToken = trimmedResponse.replaceAll("^[\"']+|[\"']+$", ""); // Remover comillas si las hay
                    }
                }
            }

            if (accessToken == null || accessToken.trim().isEmpty()) {
                logger.error("No se pudo extraer el access_token de la respuesta. Respuesta recibida: " + 
                    (tokenResponse != null && tokenResponse.length() > 100 ? tokenResponse.substring(0, 100) + "..." : tokenResponse));
                throw new UnauthorizedException("No se pudo obtener el token de acceso de Identidad Electrónica");
            }
            
            logger.info("Token JWT extraído exitosamente (longitud: " + accessToken.length() + ")");

            // Paso 3: Decodificar el token JWT para obtener los datos de la persona
            IdentidadPersonaDTO identidadPersonaDTO;
            try {
                identidadPersonaDTO = tokenComponent.getAuthenticationData(accessToken);
            } catch (JsonProcessingException e) {
                logger.error("Error al decodificar el token de Identidad Electrónica: " + e.getMessage(), e);
                throw new BadRequestException("Error al decodificar el token de Identidad Electrónica");
            }

            if (identidadPersonaDTO == null) {
                logger.error("No se pudieron obtener los datos de la persona del token");
                throw new UnauthorizedException("No se pudieron obtener los datos de identidad de la persona");
            }

            logger.info("Validación exitosa para persona: " + identidadPersonaDTO.getSub());
            
            return new ResponseDTO("Autenticación exitosa", HttpStatus.OK, identidadPersonaDTO);

        } catch (BadRequestException | UnauthorizedException e) {
            logger.error("Error en validatorAccess: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en validatorAccess: " + e.getMessage(), e);
            e.printStackTrace();
            throw new BadRequestException("Error inesperado al validar el acceso: " + e.getMessage());
        }
    }

    /**
     * Obtiene la organización del usuario y la convierte a DTO
     * @param usuario Usuario del cual se obtendrá la organización
     * @return OrganizacionDTO o null si el usuario no tiene organización asociada
     */
    public OrganizacionDTO getOrganizacionDTOByUsuario(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        Organizacion organizacion = usuarioOrganizacionRepository.findFirstOrganizacionByUsuario(usuario);
        if (organizacion != null) {
            OrganizacionDTO organizacionDTO = MapperUtil.map(organizacion, OrganizacionDTO.class, modelMapper);
            cargarNombreArchivoDTO(organizacionDTO);
            return organizacionDTO;
        }
        return null;
    }


    private void cargarNombreArchivoDTO(OrganizacionDTO dto) {
        if (dto.getIdDeclaracionJurada() != null && !dto.getIdDeclaracionJurada().isEmpty()) {
            try {
                String nombreArchivo = archivoService.obtenerNombreArchivo(dto.getIdDeclaracionJurada());
                if (nombreArchivo != null) {
                    dto.setDeclaracionJuradaNombre(nombreArchivo);
                }
            } catch (Exception e) {
                logger.warn("No se pudo obtener el nombre del archivo: " + dto.getIdDeclaracionJurada(), e);
            }
        }
        if (Objects.nonNull(dto.getReferenciaImagen()) && !dto.getReferenciaImagen().isEmpty()) {
            try {
                String nombreArchivo = archivoService.obtenerNombreArchivo(dto.getReferenciaImagen());
                if (nombreArchivo != null) {
                    dto.setReferenciaImagenNombre(nombreArchivo);
                }
            } catch (Exception e) {
                logger.warn("No se pudo obtener el nombre del archivo: " + dto.getIdDeclaracionJurada(), e);
            }
        }
    }

    /**
     * Carga los roles del usuario sin los permisos asociados
     * @param usuario Usuario del cual se cargarán los roles
     * @return Lista de roles sin permisos, o null si el usuario no existe
     */
    public List<Rol> cargarRolesSinPermisos(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            return null;
        }
        
        Optional<Usuario> usuarioCompleto = usuarioRepository.findById(Objects.requireNonNull(usuario.getIdUsuario()));
        if (usuarioCompleto.isPresent()) {
            List<Rol> roles = new ArrayList<>();
            for (Rol rol : usuarioCompleto.get().getRoles()) {
                rol.setPermisos(null);
                roles.add(rol);
            }
            return roles;
        }
        return null;
    }

    /**
     * Obtiene la URL de autorización de Identidad Electrónica
     * @return Map con la clave "IE" y la URL de autorización como valor
     */
    public Map<String, String> getUrlIE() {
        Map<String, String> resp = new HashMap<>();
        resp.put("IE", tokenComponent.getUrlAuthorization());
        return resp;
    }

}
