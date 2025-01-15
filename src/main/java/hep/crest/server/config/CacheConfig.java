package hep.crest.server.config;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * The cache manager.
     */
    @Autowired
    private CacheManager cacheManager;

    /**
     * Clear all caches on startup.
     */
    @PostConstruct
    public void clearCachesOnStartup() {
        log.info("Clearing all caches on startup...");
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cache '{}' cleared.", cacheName);
            }
        });
    }
}
