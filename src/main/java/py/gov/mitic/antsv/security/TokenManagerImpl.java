package py.gov.mitic.htv.security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import py.gov.mitic.htv.dto.auth.IdentidadPersonaDTO;
import py.gov.mitic.htv.dto.auth.Oauth2DTO;
import py.gov.mitic.htv.dto.auth.UsuarioSession;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component("tokenConfig")
public class TokenManagerImpl implements TokenManager {

    private Log logger = LogFactory.getLog(TokenManagerImpl.class);

    @Autowired

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    @Value("${ie.url.authorization}")
    String urlAuthorization;

    @Value("${ie.url.authentication}")
    String urlAuthentication;

    @Value("${ie.client.id}")
    String ieClientId;

    @Value("${ie.secret}")
    String ieSecret;

    @Value("${ie.issuer}")
    String ieIssuer;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    @Override
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(UserDetails userDetails, Long idUsuario, Long idOrganizacion) {
        Map<String, Object> claims = new HashMap<>();
        if (idUsuario != null) {
            claims.put("idUsuario", idUsuario);
        }
        if (idOrganizacion != null) {
            claims.put("idOrganizacion", idOrganizacion);
        }
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate, (expiration * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public Long getUsuarioIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        Object idUsuario = claims.get("idUsuario");
        if (idUsuario instanceof Integer) {
            return ((Integer) idUsuario).longValue();
        } else if (idUsuario instanceof Long) {
            return (Long) idUsuario;
        }
        return null;
    }

    @Override
    public Long getOrganizacionIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        Object idOrganizacion = claims.get("idOrganizacion");
        if (idOrganizacion instanceof Integer) {
            return ((Integer) idOrganizacion).longValue();
        } else if (idOrganizacion instanceof Long) {
            return (Long) idOrganizacion;
        }
        return null;
    }

    @Override
    public String createRefreshToken(String token) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate, (refreshExpiration * 1000));
        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String getUrlAuthorization() {
        return urlAuthorization;
    }

    @Override
    public String getUrlAuthentication() {
        return urlAuthentication;
    }

    @Override
    public String getClientId() {
        return ieClientId;
    }

    @Override
    public String getClientSecret() {
        return ieSecret;
    }

    public Oauth2DTO getAuthorizationData(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
            return new Oauth2DTO(header, payload);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public IdentidadPersonaDTO getAuthenticationData(String token) throws JsonProcessingException {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        return new IdentidadPersonaDTO(payload);
    }

    @Override
    public Boolean validateToken(String token, String username) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

    @Override
    public UsuarioSession validateUserToken(String reqHeader) {
        try {
            String token = reqHeader.substring(7);
            String username = getUsernameFromToken(token);
            return new UsuarioSession(username, token);
        } catch (ExpiredJwtException e) {
            logger.error("Token expirado");
        } catch (IllegalArgumentException | UnsupportedJwtException | MalformedJwtException e) {
            logger.error("No se pudo obtener el token");
        } catch (Exception e) {
            logger.error("Permiso denegado");
        }
        return null;
    }

    private Date calculateExpirationDate(Date currentDate, Long expirationToken) {
        return new Date(currentDate.getTime() + expirationToken);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}