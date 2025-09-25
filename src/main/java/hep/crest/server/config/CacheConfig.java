package hep.crest.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import hep.crest.server.services.CachePayloadBuffer;
import hep.crest.server.services.IPayloadBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {


    // Centralized cache name constants
    /**
     * Cache name for tag payloads.
     */
    public static final String TAG_PAYLOAD_CACHE = "tagPayloadCache";
    /**
     * Cache name for tags.
     */
    public static final String TAG_CACHE = "tagCache";
    /**
     * Cache name for tag metadata.
     */
    public static final String TAG_META_CACHE = "tagMetaCache";
    // Add other cache names as needed

    /**
     * Cache manager using Caffeine with TTL and max size.
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                TAG_PAYLOAD_CACHE, TAG_CACHE, TAG_META_CACHE
                // Add other cache names here
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000));
        clearCachesOnStartup(cacheManager);
        return cacheManager;
    }

    /**
     * Clear all caches on startup.
     * @param cacheManager
     */
    public void clearCachesOnStartup(CacheManager cacheManager) {
        log.info("Clearing all caches on startup...");
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cache '{}' cleared.", cacheName);
            }
        });
    }

    /**
     * Create a cache manager for the payload buffer.
     * @param cacheManager
     * @return IPayloadBuffer
     */
    @Bean
    @Profile("!redis")
    public IPayloadBuffer cacheManagerPayloadBuffer(CacheManager cacheManager) {
        return new CachePayloadBuffer(cacheManager);
    }

}
