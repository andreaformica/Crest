package hep.crest.server.services;

import hep.crest.server.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Class to implement a payload buffer for Redis.
 */
@Slf4j
public class CachePayloadBuffer implements IPayloadBuffer {

    /**
     * The memory cache.
     */
    private CacheManager cacheManager;

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
        Cache cache = cacheManager.getCache(CacheConfig.TAG_PAYLOAD_CACHE);
        if (cache != null) {
            log.debug("Add hash {} to buffer for tag {}", hash, tagName);
            Set<String> hashes = getHashesByTagName(tagName);
            if (hashes == null) {
                hashes = new HashSet<>();
            }
            hashes.add(hash);
            cache.put(tagName, hashes);
        }
    }

    @Override
    public Set<String> getHashesByTagName(String tagName) {
        Cache cache = cacheManager.getCache(CacheConfig.TAG_PAYLOAD_CACHE);
        if (cache != null) {
            return cache.get(tagName, Set.class);
        }
        return new HashSet<>();
    }

    @Override
    public void removeFromBuffer(String hash, String tagName) {
        Cache cache = cacheManager.getCache(CacheConfig.TAG_PAYLOAD_CACHE);
        if (cache != null) {
            Set<String> hashes = getHashesByTagName(tagName);
            if (hashes != null) {
                log.debug("Remove hash {} from buffer for tag {}", hash, tagName);
                hashes.remove(hash);
                cache.put(tagName, hashes);
                if (hashes.isEmpty()) {
                    cache.evict(tagName);
                }
            }
        }
    }

    @Override
    public Stream<String> streamHashesByTagName(String tagName) {
        Set<String> hashes = getHashesByTagName(tagName);
        return hashes != null ? hashes.stream() : Stream.empty();
    }
}
