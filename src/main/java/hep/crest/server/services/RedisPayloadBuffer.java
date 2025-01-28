package hep.crest.server.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Stream;

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
     * @param hash    Unique hash to store.
     * @param tagName Tag name associated with the hash.
     */
    public void addToBuffer(String hash, String tagName) {
        // Use a Redis Set to associate the hash with the tag name
        String redisKey = "tag:" + tagName;
        redisTemplate.opsForSet().add(redisKey, hash);
    }

    /**
     * Retrieve a set of ALL HASHES for a given tag_name from the buffer.
     *
     * @param tagName Tag name to look up.
     * @return A set of hashes associated with the given tag name.
     */
    public Set<String> getHashesByTagName(String tagName) {
        String redisKey = "tag:" + tagName;
        return redisTemplate.opsForSet().members(redisKey);
    }


    /**
     * Remove a hash from the buffer by its tag name.
     *
     * @param hash    The hash to remove.
     * @param tagName The tag name to which the hash belongs.
     */
    public void removeFromBuffer(String hash, String tagName) {
        String redisKey = "tag:" + tagName;
        redisTemplate.opsForSet().remove(redisKey, hash);
    }

    /**
     * Stream hashes for a given tag name, useful for processing each hash one by one.
     *
     * @param tagName Tag name to fetch hashes for streaming.
     * @return A Stream of hashes associated with the tag name.
     */
    public Stream<String> streamHashesByTagName(String tagName) {
        Set<String> hashes = getHashesByTagName(tagName);
        return hashes != null ? hashes.stream() : Stream.empty();
    }
}
