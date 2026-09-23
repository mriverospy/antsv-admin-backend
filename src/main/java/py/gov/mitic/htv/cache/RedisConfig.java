package py.gov.mitic.htv.cache;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import py.gov.mitic.htv.dto.auth.UsuarioSession;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    @NonNull
    String host;

    @Value("${spring.redis.port}")
    @NonNull
    Integer port;

    @Value("${spring.redis.timeout}")
    @NonNull
    Integer durationTimeout;

    @Value("${spring.redis.password:}")
    String password;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(Objects.requireNonNull(host));
        redisStandaloneConfiguration.setPort(Objects.requireNonNull(port));
        
        if (password != null && !password.isEmpty()) {
            redisStandaloneConfiguration.setPassword(password);
        }

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Objects.requireNonNull(Duration.ofSeconds(durationTimeout)));
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
    }

    @Bean
    public RedisTemplate<String, UsuarioSession> redisTemplate() {
        RedisTemplate<String, UsuarioSession> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

}
