package hep.crest.server.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Class to implement a payload buffer for Redis.
 */
@Service
public class RedisPayloadBuffer {

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

    /**
     * Add a hash to the buffer with optional tag_name metadata.
     *
     * @param hash
     * @param tagName
     */
    public void addToBuffer(String hash, String tagName) {
        redisTemplate.opsForValue().set(hash, tagName);
    }

    /**
     * Retrieve a Set with all HASHes.
     *
     * @return Set of String
     */
    public Set<String> getAllHashes() {
        return redisTemplate.keys("*"); // Retrieve all keys (hashes)
    }

    /**
     * Remove a KEY from Redis.
     *
     * @param hash
     */
    public void removeHash(String hash) {
        redisTemplate.delete(hash);
    }

    /**
     * Chexk if a given key exists.
     *
     * @param hash
     * @return boolean
     */
    public boolean hashExists(String hash) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(hash));
    }
}
