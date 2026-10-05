package hep.crest.server.caching;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CacheInvalidationService {

    /**
     * Comma-separated list of Varnish endpoints (host:port).
     * For K8s with StatefulSet: "varnish-0.varnish-headless:9090,varnish-1.varnish-headless:9090"
     * For single instance: "localhost:9090"
     * Leave empty to use dynamic discovery via headless service.
     */
    @Value("${varnish.endpoints:}")
    private String varnishEndpoints;

    /**
     * Headless service name for dynamic pod discovery (K8s Deployment).
     * When set, will resolve all pod IPs from this DNS name.
     * Example: "varnish-headless"
     */
    @Value("${varnish.headless.service:}")
    private String headlessService;

    /**
     * Port for Varnish when using dynamic discovery.
     */
    @Value("${varnish.port:9090}")
    private int varnishPort;

    /**
     * Timeout for cache invalidation requests.
     */
    @Value("${varnish.timeout:5000}")
    private int timeoutMs;

    /**
     * Whether cache invalidation is enabled.
     */
    @Value("${varnish.enabled:false}")
    private boolean enabled;

    /**
     * The Host header to use for PURGE/BAN requests.
     * This must match the Host header used by clients accessing the cache.
     * Example: "atlas-crest-dev.cern.ch"
     */
    @Value("${varnish.host.header:}")
    private String hostHeader;

    /**
     * Resolved list of Varnish endpoints. Populated at startup from
     * configuration or dynamic discovery.
     */
    private List<String> staticEndpoints;
    /**
     * HTTP client for sending PURGE and BAN requests to Varnish instances.
     */
    private HttpClient httpClient;

    /**
     * Initialize the service after properties are set.
     * Parses static endpoints and sets up HTTP client.
     */
    @PostConstruct
    public void init() {
        if (!enabled) {
            log.info("CacheInvalidationService is disabled (varnish.enabled=false)");
            return;
        }

        staticEndpoints = new ArrayList<>();
        if (varnishEndpoints != null && !varnishEndpoints.isBlank()) {
            for (String endpoint : varnishEndpoints.split(",")) {
                staticEndpoints.add(endpoint.trim());
            }
        }

        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();

        if (!staticEndpoints.isEmpty()) {
            log.info("CacheInvalidationService initialized with {} static endpoint(s): {}",
                    staticEndpoints.size(), staticEndpoints);
        }
        else if (headlessService != null && !headlessService.isBlank()) {
            log.info("CacheInvalidationService initialized with dynamic discovery via headless "
                            + "service: {}",
                    headlessService);
        }
        else {
            log.warn("CacheInvalidationService: No endpoints configured. Set varnish.endpoints or"
                    + " varnish.headless.service");
        }
    }

    /**
     * Resolve current Varnish endpoints.
     * Uses static endpoints if configured, otherwise resolves from headless service.
     * @return list of endpoints in host:port format
     */
    private List<String> resolveEndpoints() {
        if (!staticEndpoints.isEmpty()) {
            return staticEndpoints;
        }

        if (headlessService != null && !headlessService.isBlank()) {
            return resolveHeadlessService();
        }

        return List.of();
    }

    /**
     * Resolve all pod IPs from a Kubernetes headless service.
     * DNS lookup on headless service returns all pod IPs.
     * @return list of endpoints in host:port format
     */
    private List<String> resolveHeadlessService() {
        List<String> endpoints = new ArrayList<>();
        try {
            InetAddress[] addresses = InetAddress.getAllByName(headlessService);
            for (InetAddress addr : addresses) {
                endpoints.add(addr.getHostAddress() + ":" + varnishPort);
            }
            log.info("Resolved {} endpoints from headless service {}: {}",
                    endpoints.size(), headlessService, endpoints);
        }
        catch (Exception e) {
            log.warn("Failed to resolve headless service {}: {}", headlessService, e.getMessage());
        }
        return endpoints;
    }

    /**
     * Invalidate the cache for a given tag by sending PURGE requests to all Varnish instances.
     *
     * @param tagName the tag name to invalidate
     */
    public void invalidateTag(String tagName) {
        if (!enabled) {
            return;
        }

        List<String> endpoints = resolveEndpoints();
        if (endpoints.isEmpty()) {
            log.debug("No Varnish endpoints available, skipping cache invalidation for tag: {}",
                    tagName);
            return;
        }

        log.info("Invalidating cache for tag: {} on {} Varnish instance(s)", tagName,
                endpoints.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String endpoint : endpoints) {
            // Purge the tag URL
            futures.add(sendPurgeAsync(endpoint, "/api-v6.0/tags/" + tagName));
            // Purge the tag meta URL
            futures.add(sendPurgeAsync(endpoint, "/api-v6.0/tags/" + tagName + "/meta"));
        }

        // Wait for all purge requests to complete (non-blocking overall)
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("Cache invalidated for tag: {} on all Varnish instances",
                        tagName))
                .exceptionally(e -> {
                    log.warn("Some cache invalidation requests failed for tag: {}", tagName, e);
                    return null;
                });
    }

    /**
     * Invalidate all IOV-related cache entries for a given tag using BAN.
     * This is useful when IOVs are added/modified for a tag.
     *
     * @param tagName the tag name to ban IOV entries for
     */
    public void invalidateIovsForTag(String tagName) {
        if (!enabled) {
            return;
        }

        List<String> endpoints = resolveEndpoints();
        if (endpoints.isEmpty()) {
            log.warn("No Varnish endpoints available, skipping BAN for tag: {}", tagName);
            return;
        }

        log.debug("Banning IOV cache for tag: {} on {} Varnish instance(s)", tagName,
                endpoints.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String endpoint : endpoints) {
            futures.add(sendBanAsync(endpoint, tagName));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("IOV cache banned for tag: {} on all Varnish instances",
                        tagName))
                .exceptionally(e -> {
                    log.warn("Some BAN requests failed for tag: {}", tagName, e);
                    return null;
                });
    }

    /**
     * Send async PURGE request to a specific Varnish endpoint.
     * @param endpoint the Varnish endpoint (host:port)
     * @param path the path to purge (e.g. "/api-v6.0/tags/{tagName}")
     * @return CompletableFuture that completes when the request is done
     */
    private CompletableFuture<Void> sendPurgeAsync(String endpoint, String path) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://" + endpoint + path))
                .method("PURGE", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofMillis(timeoutMs));

        // Add Host header if configured (required for Varnish to match cached objects)
        if (hostHeader != null && !hostHeader.isBlank()) {
            requestBuilder.header("Host", hostHeader);
        }

        HttpRequest request = requestBuilder.build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        log.info("PURGE successful for {} on {}", path, endpoint);
                    }
                    else {
                        log.warn("PURGE returned {} for {} on {}", response.statusCode(), path,
                                endpoint);
                    }
                })
                .exceptionally(e -> {
                    log.warn("PURGE failed for {} on {}: {}", path, endpoint, e.getMessage());
                    return null;
                });
    }

    /**
     * Send async BAN request to a specific Varnish endpoint.
     * @param endpoint the Varnish endpoint (host:port)
     * @param tagName the tag name to ban (sent in X-Ban-Tag header)
     * @return CompletableFuture that completes when the request is done
     */
    private CompletableFuture<Void> sendBanAsync(String endpoint, String tagName) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://" + endpoint + "/"))
                .method("BAN", HttpRequest.BodyPublishers.noBody())
                .header("X-Ban-Tag", tagName)
                .timeout(Duration.ofMillis(timeoutMs));

        // Add Host header if configured (required for Varnish to match cached objects)
        if (hostHeader != null && !hostHeader.isBlank()) {
            requestBuilder.header("Host", hostHeader);
        }

        HttpRequest request = requestBuilder.build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        log.info("BAN successful for tag {} on {}", tagName, endpoint);
                    }
                    else {
                        log.warn("BAN returned {} for tag {} on {}", response.statusCode(),
                                tagName, endpoint);
                    }
                })
                .exceptionally(e -> {
                    log.warn("BAN failed for tag {} on {}: {}", tagName, endpoint, e.getMessage());
                    return null;
                });
    }
}
