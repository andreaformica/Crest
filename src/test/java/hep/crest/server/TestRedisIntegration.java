package hep.crest.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("testredis") // Use a specific profile if needed
@IfProfileValue(name = "spring.profiles.active", value = "testredis")
@SpringBootTest
public class TestRedisIntegration {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //@Test
    void testRedisFunctionality() {
        redisTemplate.opsForValue().set("key", "value");
        String value = redisTemplate.opsForValue().get("key");
        Assertions.assertEquals("value", value);
    }
}
