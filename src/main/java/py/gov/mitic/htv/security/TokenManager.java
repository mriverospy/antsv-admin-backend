package py.gov.mitic.htv.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import py.gov.mitic.htv.dto.auth.IdentidadPersonaDTO;
import py.gov.mitic.htv.dto.auth.Oauth2DTO;
import py.gov.mitic.htv.dto.auth.UsuarioSession;

import java.util.Date;
import java.util.function.Function;

public interface TokenManager {

    String getUsernameFromToken(String token);

    Date getExpirationDateFromToken(String token);

    <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetails userDetails, Long idUsuario, Long idOrganizacion);

    Long getUsuarioIdFromToken(String token);

    Long getOrganizacionIdFromToken(String token);

    Boolean validateToken(String token, String username);

    UsuarioSession validateUserToken(String reqHeader);

    String createRefreshToken(String token);

    String getUrlAuthorization();

    String getUrlAuthentication();

    String getClientId();

    String getClientSecret();

    Oauth2DTO getAuthorizationData(String token) throws JsonProcessingException;

    IdentidadPersonaDTO getAuthenticationData(String token) throws JsonProcessingException;

}
