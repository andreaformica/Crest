package hep.crest.server.config;

import hep.crest.server.services.IPayloadBuffer;
import hep.crest.server.services.RedisPayloadBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis configuration.
 *
 * @author formica
 */
@Configuration
@ComponentScan("hep.crest.server")
@Profile("redis")
@Slf4j
public class RedisConfig {


    /**
     * Create a RedisTemplate, when Redis is present.
     * @param connectionFactory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * Create a RedisTemplate Cache for Payloads removal.
     * @param redisTemplate
     * @return IPayloadBuffer
     */
    @Bean
    public IPayloadBuffer redisPayloadBuffer(RedisTemplate<String, String> redisTemplate) {
        return new RedisPayloadBuffer(redisTemplate);
    }

}
