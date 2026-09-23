package py.gov.mitic.htv.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import py.gov.mitic.htv.dto.auth.UsuarioSession;

import java.util.Objects;

@Repository
public class TokenRepository {

    private static String KEY = "minna";
    @SuppressWarnings("unused")
    private RedisTemplate<String, UsuarioSession> redisTemplate;
    private HashOperations<String, String, UsuarioSession> hashOperations;
    
    private Log logger = LogFactory.getLog(TokenRepository.class);

    public TokenRepository(RedisTemplate<String, UsuarioSession> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    public UsuarioSession get(String token) {
        return (UsuarioSession) hashOperations.get(Objects.requireNonNull(KEY), Objects.requireNonNull(token));
    }

    public void create(UsuarioSession item) {
        hashOperations.put(Objects.requireNonNull(KEY), Objects.requireNonNull(item.getToken()), item);
    }

    public void update(UsuarioSession item) {
        create(item);
    }

    public void delete(String token) {
        System.out.println("token antes de eliminar: "+get(token));
        hashOperations.delete(Objects.requireNonNull(KEY), Objects.requireNonNull(token));
        if (get(token) == null) {
        	logger.info("Token eliminado.");
        }
    }

}
