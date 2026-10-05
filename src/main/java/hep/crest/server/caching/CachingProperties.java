package hep.crest.server.caching;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The caching properties.
 *
 * @author formica
 *
 */
@Component
@ConfigurationProperties("caching")
@Data
@Slf4j
public class CachingProperties {
    /**
     * The default cache in seconds.
     */
    public static final Integer DEFAULT_CACHE_TIME = 60; // number of seconds

    /**
     * The iov group max age.
     */
    private Integer iovsgroupsMaxage = 120; // number of seconds

    /**
     * The iov group snapshot max age.
     */
    private Integer iovsgroupsSnapshotMaxage = 600; // number of seconds

    /**
     * The iov max age.
     */
    private Integer iovsMaxage = 600; // number of seconds

    /**
     * The iov snapshot max age.
     */
    private Integer iovsSnapshotMaxage = 1200;

    /**
     * The payload max age.
     */
    private Integer payloadsMaxage = 1200;

    /**
     * The group size for time based folders.
     */
    private Integer timetypeGroupsize = 1000;

    /**
     * The group size for run based folders.
     */
    private Integer runtypeGroupsize = 10;
    /**
     * The caching for GlobalTag map trace.
     */
    private Integer tagmap = 300;
    /**
     * The caching for Tag.
     */
    private Integer tag = 3600;

    /**
     * Log the loaded caching properties at startup.
     */
    @PostConstruct
    public void logProperties() {
        log.info("Caching properties loaded: iovsgroupsMaxage={}, iovsgroupsSnapshotMaxage={}, "
                + "iovsMaxage={}, iovsSnapshotMaxage={}, payloadsMaxage={}, "
                + "timetypeGroupsize={}, runtypeGroupsize={}",
                iovsgroupsMaxage, iovsgroupsSnapshotMaxage,
                iovsMaxage, iovsSnapshotMaxage, payloadsMaxage,
                timetypeGroupsize, runtypeGroupsize);
    }

}
