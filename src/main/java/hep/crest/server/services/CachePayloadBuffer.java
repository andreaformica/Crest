package hep.crest.server.services;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Class to implement a payload buffer for Redis.
 */
public class CachePayloadBuffer implements IPayloadBuffer {

    /**
     * The memory cache.
     */
    private CacheManager cacheManager;
    /**
     * The cache name.
     */
    private static final String CACHE_NAME = "tagPayloadCache"; // Unique cache name

    /**
     * The constructor.
     *
     * @param cacheManager
     */
    public CachePayloadBuffer(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void addToBuffer(String hash, String tagName) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(tagName, hash);
        }
    }

    @Override
    public Set<String> getHashesByTagName(String tagName) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            return cache.get(tagName, Set.class);
        }
        return new HashSet<>();
    }

    @Override
    public void removeFromBuffer(String hash, String tagName) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.evict(tagName);
        }
    }

    @Override
    public Stream<String> streamHashesByTagName(String tagName) {
        Set<String> hashes = getHashesByTagName(tagName);
        return hashes != null ? hashes.stream() : Stream.empty();
    }
}
