package hep.crest.server.config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * The cache manager.
     */
    @Autowired
    private org.springframework.cache.CacheManager cacheManager;

    /**
     * Clear all caches on startup.
     */
    @PostConstruct
    public void clearCachesOnStartup() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }
}
