package hep.crest.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Properties for Redis.
 */
@Configuration
@lombok.Getter
public class RedisProperties {
    /**
     * @return the redisPort
     */
    private int redisPort;
    /**
     * @return the redisHost
     */
    private String redisHost;

    /**
     * Ctor.
     * @param redisPort
     * @param redisHost
     */
    public RedisProperties(
            @Value("${spring.data.redis.port}") int redisPort,
            @Value("${spring.data.redis.host}") String redisHost) {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
    }
}
