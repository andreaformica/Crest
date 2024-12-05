package hep.crest.server;

import com.redis.testcontainers.RedisServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Profile({"test-local"})
public class TestEmbeddedRedisConfig {


    /**
     * The redis server.
     */
    private RedisServer redisServer;
    private static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.2")).withExposedPorts(6379);

    @PostConstruct
    public void startRedis() {
        redis.start();
    }

    @PreDestroy
    public void stopRedis() {
        redis.stop();
    }

}
