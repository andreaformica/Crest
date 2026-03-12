package hep.crest.server.services;

import com.sun.net.httpserver.HttpServer;
import hep.crest.server.caching.CacheInvalidationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for CacheInvalidationService.
 * Uses an embedded HTTP server to mock Varnish responses.
 */
@Slf4j
public class TestCacheValidationService {

    private CacheInvalidationService service;
    private HttpServer mockVarnish;
    private int mockPort;
    private List<String> receivedRequests;

    @BeforeEach
    public void setUp() throws IOException {
        receivedRequests = Collections.synchronizedList(new ArrayList<>());

        // Create a mock HTTP server to simulate Varnish
        mockVarnish = HttpServer.create(new InetSocketAddress(0), 0);
        mockPort = mockVarnish.getAddress().getPort();

        // Handler for PURGE requests
        mockVarnish.createContext("/", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String banTag = exchange.getRequestHeaders().getFirst("X-Ban-Tag");

            String requestInfo = method + " " + path;
            if (banTag != null) {
                requestInfo += " X-Ban-Tag=" + banTag;
            }
            receivedRequests.add(requestInfo);
            log.info("Mock Varnish received: {}", requestInfo);

            // Respond with 200 OK for PURGE and BAN
            if ("PURGE".equals(method) || "BAN".equals(method)) {
                exchange.sendResponseHeaders(200, -1);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        });

        mockVarnish.start();
        log.info("Mock Varnish started on port {}", mockPort);

        // Create and configure the service
        service = new CacheInvalidationService();
        ReflectionTestUtils.setField(service, "enabled", true);
        ReflectionTestUtils.setField(service, "varnishEndpoints", "localhost:" + mockPort);
        ReflectionTestUtils.setField(service, "headlessService", "");
        ReflectionTestUtils.setField(service, "varnishPort", mockPort);
        ReflectionTestUtils.setField(service, "timeoutMs", 5000);

        // Initialize the service
        service.init();
    }

    @AfterEach
    public void tearDown() {
        if (mockVarnish != null) {
            mockVarnish.stop(0);
            log.info("Mock Varnish stopped");
        }
    }

    /**
     * Test that invalidateTag sends PURGE requests to the correct URLs.
     */
    @Test
    public void testInvalidateTagSendsPurgeRequests() throws InterruptedException {
        log.info("=======> testInvalidateTagSendsPurgeRequests");

        String tagName = "TestTag-" + System.currentTimeMillis();

        // Call the service
        service.invalidateTag(tagName);

        // Wait for async requests to complete
        TimeUnit.MILLISECONDS.sleep(500);

        // Verify PURGE requests were sent
        assertThat(receivedRequests).hasSize(2);
        assertThat(receivedRequests).anyMatch(r -> r.equals("PURGE /api-v6.0/tags/" + tagName));
        assertThat(receivedRequests).anyMatch(r -> r.equals("PURGE /api-v6.0/tags/" + tagName + "/meta"));

        log.info("Successfully verified PURGE requests for tag: {}", tagName);
    }

    /**
     * Test that invalidateIovsForTag sends BAN requests with correct header.
     */
    @Test
    public void testInvalidateIovsForTagSendsBanRequest() throws InterruptedException {
        log.info("=======> testInvalidateIovsForTagSendsBanRequest");

        String tagName = "TestIovTag-" + System.currentTimeMillis();

        // Call the service
        service.invalidateIovsForTag(tagName);

        // Wait for async requests to complete
        TimeUnit.MILLISECONDS.sleep(500);

        // Verify BAN request was sent with correct header
        assertThat(receivedRequests).hasSize(1);
        assertThat(receivedRequests.get(0)).isEqualTo("BAN / X-Ban-Tag=" + tagName);

        log.info("Successfully verified BAN request for tag: {}", tagName);
    }

    /**
     * Test that service does nothing when disabled.
     */
    @Test
    public void testServiceDisabledDoesNothing() throws InterruptedException {
        log.info("=======> testServiceDisabledDoesNothing");

        // Create a disabled service
        CacheInvalidationService disabledService = new CacheInvalidationService();
        ReflectionTestUtils.setField(disabledService, "enabled", false);
        ReflectionTestUtils.setField(disabledService, "varnishEndpoints", "localhost:" + mockPort);
        ReflectionTestUtils.setField(disabledService, "headlessService", "");
        ReflectionTestUtils.setField(disabledService, "varnishPort", mockPort);
        ReflectionTestUtils.setField(disabledService, "timeoutMs", 5000);
        disabledService.init();

        // Clear any previous requests
        receivedRequests.clear();

        // Call the service methods
        disabledService.invalidateTag("SomeTag");
        disabledService.invalidateIovsForTag("SomeTag");

        // Wait a bit
        TimeUnit.MILLISECONDS.sleep(300);

        // Verify no requests were sent
        assertThat(receivedRequests).isEmpty();

        log.info("Successfully verified disabled service sends no requests");
    }

    /**
     * Test that service handles multiple endpoints.
     */
    @Test
    public void testMultipleEndpoints() throws Exception {
        log.info("=======> testMultipleEndpoints");

        // Create a second mock Varnish
        HttpServer mockVarnish2 = HttpServer.create(new InetSocketAddress(0), 0);
        int mockPort2 = mockVarnish2.getAddress().getPort();

        mockVarnish2.createContext("/", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            receivedRequests.add("VARNISH2: " + method + " " + path);
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        mockVarnish2.start();

        try {
            // Configure service with multiple endpoints
            CacheInvalidationService multiService = new CacheInvalidationService();
            ReflectionTestUtils.setField(multiService, "enabled", true);
            ReflectionTestUtils.setField(multiService, "varnishEndpoints",
                    "localhost:" + mockPort + ",localhost:" + mockPort2);
            ReflectionTestUtils.setField(multiService, "headlessService", "");
            ReflectionTestUtils.setField(multiService, "varnishPort", mockPort);
            ReflectionTestUtils.setField(multiService, "timeoutMs", 5000);
            multiService.init();

            // Clear previous requests
            receivedRequests.clear();

            String tagName = "MultiEndpointTag";
            multiService.invalidateTag(tagName);

            // Wait for async requests
            TimeUnit.MILLISECONDS.sleep(500);

            // Should have 4 requests: 2 URLs x 2 endpoints
            assertThat(receivedRequests).hasSize(4);

            // Verify requests went to both endpoints
            long varnish1Requests = receivedRequests.stream()
                    .filter(r -> r.startsWith("PURGE")).count();
            long varnish2Requests = receivedRequests.stream()
                    .filter(r -> r.startsWith("VARNISH2")).count();

            assertThat(varnish1Requests).isEqualTo(2);
            assertThat(varnish2Requests).isEqualTo(2);

            log.info("Successfully verified requests sent to multiple endpoints");
        } finally {
            mockVarnish2.stop(0);
        }
    }

    /**
     * Test that service handles connection failures gracefully.
     */
    @Test
    public void testHandlesConnectionFailure() throws InterruptedException {
        log.info("=======> testHandlesConnectionFailure");

        // Configure service with non-existent endpoint
        CacheInvalidationService failingService = new CacheInvalidationService();
        ReflectionTestUtils.setField(failingService, "enabled", true);
        ReflectionTestUtils.setField(failingService, "varnishEndpoints", "localhost:99999");
        ReflectionTestUtils.setField(failingService, "headlessService", "");
        ReflectionTestUtils.setField(failingService, "varnishPort", 99999);
        ReflectionTestUtils.setField(failingService, "timeoutMs", 1000);
        failingService.init();

        // This should not throw an exception, just log a warning
        failingService.invalidateTag("FailingTag");
        failingService.invalidateIovsForTag("FailingTag");

        // Wait for async completion
        TimeUnit.MILLISECONDS.sleep(500);

        log.info("Successfully verified service handles connection failures gracefully");
    }

    /**
     * Test that service handles non-200 responses.
     */
    @Test
    public void testHandlesNon200Response() throws Exception {
        log.info("=======> testHandlesNon200Response");

        // Create a mock that returns 405
        HttpServer failingMock = HttpServer.create(new InetSocketAddress(0), 0);
        int failingPort = failingMock.getAddress().getPort();

        failingMock.createContext("/", exchange -> {
            receivedRequests.add("FAILING: " + exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        });
        failingMock.start();

        try {
            CacheInvalidationService testService = new CacheInvalidationService();
            ReflectionTestUtils.setField(testService, "enabled", true);
            ReflectionTestUtils.setField(testService, "varnishEndpoints", "localhost:" + failingPort);
            ReflectionTestUtils.setField(testService, "headlessService", "");
            ReflectionTestUtils.setField(testService, "varnishPort", failingPort);
            ReflectionTestUtils.setField(testService, "timeoutMs", 5000);
            testService.init();

            receivedRequests.clear();

            // Should not throw, just log warning
            testService.invalidateTag("TestTag");

            TimeUnit.MILLISECONDS.sleep(500);

            // Requests should still have been sent
            assertThat(receivedRequests).hasSize(2);

            log.info("Successfully verified service handles non-200 responses");
        } finally {
            failingMock.stop(0);
        }
    }

    /**
     * Test service with empty endpoints configuration.
     */
    @Test
    public void testEmptyEndpointsDoesNothing() throws InterruptedException {
        log.info("=======> testEmptyEndpointsDoesNothing");

        CacheInvalidationService emptyService = new CacheInvalidationService();
        ReflectionTestUtils.setField(emptyService, "enabled", true);
        ReflectionTestUtils.setField(emptyService, "varnishEndpoints", "");
        ReflectionTestUtils.setField(emptyService, "headlessService", "");
        ReflectionTestUtils.setField(emptyService, "varnishPort", 9090);
        ReflectionTestUtils.setField(emptyService, "timeoutMs", 5000);
        emptyService.init();

        receivedRequests.clear();

        // Should not throw, just skip
        emptyService.invalidateTag("TestTag");
        emptyService.invalidateIovsForTag("TestTag");

        TimeUnit.MILLISECONDS.sleep(300);

        // No requests should be sent
        assertThat(receivedRequests).isEmpty();

        log.info("Successfully verified empty endpoints configuration");
    }
}
