package hep.crest.server;

import com.redis.testcontainers.RedisServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.IfProfileValue;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@TestConfiguration
@Profile({"testredis"})
@IfProfileValue(name = "spring.profiles.active", value = "testredis")
public class TestEmbeddedRedisConfig {


    /**
     * The redis server.
     */
    private RedisServer redisServer;
    /**
     * The redis container.
     */
    private static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6380);

    @PostConstruct
    public void startRedis() {
        log.info("Starting redis server...");
        redis.start();
    }

    @PreDestroy
    public void stopRedis() {
        log.info("Stopping redis server...");
        redis.stop();
    }
}
