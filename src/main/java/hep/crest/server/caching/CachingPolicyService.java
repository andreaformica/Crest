package hep.crest.server.caching;

import hep.crest.server.config.CrestProperties;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.swagger.impl.JAXRSContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A class to get the cache control.
 *
 * @author formica
 *
 */
@Component
@Slf4j
@Getter
public class CachingPolicyService {
    /**
     * Properties.
     */
    private CachingProperties cprops;

    /**
     * Ctor for injection.
     * @param cprops
     */
    @Autowired
    public CachingPolicyService(CachingProperties cprops) {
        this.cprops = cprops;
    }

    /**
     * @param snapshot
     *            the Long
     * @return CacheControl
     */
    public CacheControl getGroupsCacheControl(Long snapshot) {
        Integer maxage = CachingProperties.DEFAULT_CACHE_TIME;
        if (snapshot != 0L) {
            maxage = cprops.getIovsgroupsSnapshotMaxage();
        }
        final CacheControl cc = new CacheControl();
        cc.setMaxAge(maxage);
        return cc;
    }

    /**
     * Get the default cache control.
     * @return CacheControl
     */
    public CacheControl getDefaultsCacheControl() {
        final CacheControl cc = new CacheControl();
        cc.setMaxAge(CachingProperties.DEFAULT_CACHE_TIME);
        return cc;
    }

    /**
     * @return CacheControl
     */
    public CacheControl getPayloadCacheControl() {
        final CacheControl cc = new CacheControl();
        cc.setMaxAge(cprops.getPayloadsMaxage());
        return cc;
    }


    /**
     * @param snapshot
     *            the Long
     * @param until
     *            the BigDecimal
     * @return CacheControl
     */
    public CacheControl getIovsCacheControlForUntil(Long snapshot, BigDecimal until) {
        // FOR HLT test we need to set the DEFAULT_CACHE_TIME to 0 for the IOVs.
        // This is because the HLT test will use a fixed until time INF.
        //
        // cache time = 60 s will be enough to avoid caching the response.
        // Default cache time for IOV is 600s.
        Integer maxage = cprops.getIovsMaxage();
        if (!until.equals(CrestProperties.INFINITY)) {
            if (snapshot != 0L) {
                maxage = cprops.getIovsSnapshotMaxage();
            }
            else {
                maxage = cprops.getIovsMaxage();
            }
        }
        final CacheControl cc = new CacheControl();
        cc.setMaxAge(maxage);
        return cc;
    }

    /**
     * @param request
     *            the Request
     * @param tagentity
     *            the Tag
     * @return ResponseBuilder
     */
    public ResponseBuilder verifyLastModified(Request request, Tag tagentity) {
        final Date lastModified = tagentity.getModificationTime();
        log.debug("Use tag modification time {} and request {}", lastModified, request);
        final ResponseBuilder builder = request.evaluatePreconditions(lastModified);
        if (builder != null) {
            final CacheControl cc = new CacheControl();
            builder.cacheControl(cc).header("Last-Modified", lastModified); // add
                                                                            // metadata
        }
        return builder;
    }

    /**
     * Check if the tag was modified since last request, and if not, return a 304 response.
     * @param tagentity
     * @param context
     * @return Response if the tag was not modified, null otherwise.
     */
    public Response checkLastModified(Tag tagentity, JAXRSContext context) {
        HttpHeaders headers = context.getHttpHeaders();
        Request request = context.getRequest();
        log.info("Check if tag {} was modified since last request", tagentity.getName());
        log.info("Received If-Modified-Since header: {}",
                headers.getHeaderString("If-Modified-Since"));
        // Use cache service to detect if a tag was modified.
        final ResponseBuilder builder = this.verifyLastModified(request, tagentity);
        if (builder != null) {
            // Get request headers: this is just to dump the If-Modified-Since
            final String ifmodsince = headers.getHeaderString("If-Modified-Since");
            log.debug("The output data are not modified since " + ifmodsince);
            return builder.build();
        }
        return null;
    }

}
