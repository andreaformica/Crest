package hep.crest.server.config;

import hep.crest.server.services.CachePayloadBuffer;
import hep.crest.server.services.IPayloadBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {


    /**
     * Cache manager.
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CacheManager cacheManager = new ConcurrentMapCacheManager();
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
