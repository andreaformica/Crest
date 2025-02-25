package hep.crest.server.services;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Class to implement a payload buffer for Redis.
 */
public class RedisPayloadBuffer implements IPayloadBuffer {

    /**
     * The RedisTemplate.
     */
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * The constructor.
     *
     * @param redisTemplate
     */
    public RedisPayloadBuffer(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addToBuffer(String hash, String tagName) {
        // Use a Redis Set to associate the hash with the tag name
        String redisKey = "tag:" + tagName;
        redisTemplate.opsForSet().add(redisKey, hash);
    }

    @Override
    public Set<String> getHashesByTagName(String tagName) {
        String redisKey = "tag:" + tagName;
        return redisTemplate.opsForSet().members(redisKey);
    }


    @Override
    public void removeFromBuffer(String hash, String tagName) {
        String redisKey = "tag:" + tagName;
        redisTemplate.opsForSet().remove(redisKey, hash);
    }

    @Override
    public Stream<String> streamHashesByTagName(String tagName) {
        Set<String> hashes = getHashesByTagName(tagName);
        return hashes != null ? hashes.stream() : Stream.empty();
    }
}
