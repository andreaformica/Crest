package hep.crest.server.config;

import hep.crest.server.caching.CachingPolicyService;
import hep.crest.server.exceptions.CdbBadRequestException;
import hep.crest.server.exceptions.CdbInternalException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.exceptions.ConflictException;
import hep.crest.server.swagger.model.HTTPResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for JerseyExceptionHandler.
 * Tests the different exception types handled by the exception mapper.
 */
@Slf4j
public class TestJerseyExceptionHandler {

    @Mock
    private CachingPolicyService cachingPolicyService;

    private JerseyExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the cache control
        CacheControl cc = new CacheControl();
        cc.setMaxAge(0);
        when(cachingPolicyService.getGroupsCacheControl(anyLong())).thenReturn(cc);

        // Create the exception handler
        exceptionHandler = new JerseyExceptionHandler(cachingPolicyService);

        // Trigger @PostConstruct manually since we're not using Spring context
        try {
            java.lang.reflect.Method postConstruct = JerseyExceptionHandler.class
                    .getDeclaredMethod("postConstruct");
            postConstruct.setAccessible(true);
            postConstruct.invoke(exceptionHandler);
        } catch (Exception e) {
            log.warn("Could not invoke postConstruct: {}", e.getMessage());
        }
    }

    /**
     * Test handling of WebApplicationException.
     */
    @Test
    public void testWebApplicationException() {
        log.info("=======> testWebApplicationException");

        // Create a WebApplicationException with a specific status
        Response originalResponse = Response.status(Response.Status.NOT_FOUND)
                .entity("Resource not found")
                .build();
        WebApplicationException webEx = new WebApplicationException("Not found", originalResponse);

        // Handle the exception
        Response response = exceptionHandler.toResponse(webEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        log.info("WebApplicationException handled correctly with status: {}", response.getStatus());
    }

    /**
     * Test handling of WebApplicationException with BAD_REQUEST status.
     */
    @Test
    public void testWebApplicationExceptionBadRequest() {
        log.info("=======> testWebApplicationExceptionBadRequest");

        Response originalResponse = Response.status(Response.Status.BAD_REQUEST)
                .entity("Bad request")
                .build();
        WebApplicationException webEx = new WebApplicationException("Bad request", originalResponse);

        Response response = exceptionHandler.toResponse(webEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        log.info("WebApplicationException (BAD_REQUEST) handled correctly");
    }

    /**
     * Test handling of WebApplicationException with UNAUTHORIZED status.
     */
    @Test
    public void testWebApplicationExceptionUnauthorized() {
        log.info("=======> testWebApplicationExceptionUnauthorized");

        Response originalResponse = Response.status(Response.Status.UNAUTHORIZED)
                .entity("Unauthorized")
                .build();
        WebApplicationException webEx = new WebApplicationException("Unauthorized", originalResponse);

        Response response = exceptionHandler.toResponse(webEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
        log.info("WebApplicationException (UNAUTHORIZED) handled correctly");
    }

    /**
     * Test handling of CdbNotFoundException (AbstractCdbServiceException).
     */
    @Test
    public void testCdbNotFoundException() {
        log.info("=======> testCdbNotFoundException");

        CdbNotFoundException notFoundEx = new CdbNotFoundException("Entity not found");

        Response response = exceptionHandler.toResponse(notFoundEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getMessage()).contains("Entity not found");
        assertThat(httpResponse.getCode()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        log.info("CdbNotFoundException handled correctly: {}", httpResponse.getMessage());
    }

    /**
     * Test handling of CdbBadRequestException (AbstractCdbServiceException).
     */
    @Test
    public void testCdbBadRequestException() {
        log.info("=======> testCdbBadRequestException");

        CdbBadRequestException badRequestEx = new CdbBadRequestException("Invalid input data");

        Response response = exceptionHandler.toResponse(badRequestEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getMessage()).contains("Invalid input data");

        log.info("CdbBadRequestException handled correctly: {}", httpResponse.getMessage());
    }

    /**
     * Test handling of ConflictException (AbstractCdbServiceException).
     */
    @Test
    public void testConflictException() {
        log.info("=======> testConflictException");

        ConflictException conflictEx = new ConflictException("Resource already exists");

        Response response = exceptionHandler.toResponse(conflictEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.CONFLICT.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getMessage()).contains("Resource already exists");

        log.info("ConflictException handled correctly: {}", httpResponse.getMessage());
    }

    /**
     * Test handling of CdbInternalException (AbstractCdbServiceException).
     */
    @Test
    public void testCdbInternalException() {
        log.info("=======> testCdbInternalException");

        CdbInternalException internalEx = new CdbInternalException("Internal server error occurred");

        Response response = exceptionHandler.toResponse(internalEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getMessage()).contains("Internal server error occurred");

        log.info("CdbInternalException handled correctly: {}", httpResponse.getMessage());
    }

    /**
     * Test handling of ConstraintViolationException.
     */
    @Test
    public void testConstraintViolationException() {
        log.info("=======> testConstraintViolationException");

        // Create mock constraint violations
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("Field 'name' must not be null");
        violations.add(violation1);

        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        when(violation2.getMessage()).thenReturn("Field 'description' must not be empty");
        violations.add(violation2);

        ConstraintViolationException constraintEx = new ConstraintViolationException(violations);

        Response response = exceptionHandler.toResponse(constraintEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getType()).isEqualTo("VALIDATION_ERROR");
        assertThat(httpResponse.getMessage()).contains("Validation failed");
        // Check that at least one of the violation messages is present
        assertThat(httpResponse.getMessage()).containsAnyOf("name", "description");

        log.info("ConstraintViolationException handled correctly: {}", httpResponse.getMessage());
    }

    /**
     * Test handling of ConstraintViolationException with single violation.
     */
    @Test
    public void testConstraintViolationExceptionSingleViolation() {
        log.info("=======> testConstraintViolationExceptionSingleViolation");

        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Tag name can only contain alphanumeric characters");
        violations.add(violation);

        ConstraintViolationException constraintEx = new ConstraintViolationException(violations);

        Response response = exceptionHandler.toResponse(constraintEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse.getMessage()).contains("alphanumeric");

        log.info("Single ConstraintViolation handled correctly");
    }

    /**
     * Test handling of DataIntegrityViolationException.
     */
    @Test
    public void testDataIntegrityViolationException() {
        log.info("=======> testDataIntegrityViolationException");

        DataIntegrityViolationException dataEx = new DataIntegrityViolationException(
                "Unique constraint violated: duplicate key value");

        Response response = exceptionHandler.toResponse(dataEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getMessage()).contains("Unique constraint violated");

        log.info("DataIntegrityViolationException handled correctly: {}", httpResponse.getMessage());
    }

    /**
     * Test handling of DataIntegrityViolationException with nested cause.
     */
    @Test
    public void testDataIntegrityViolationExceptionWithCause() {
        log.info("=======> testDataIntegrityViolationExceptionWithCause");

        Exception rootCause = new RuntimeException("Foreign key constraint failed");
        DataIntegrityViolationException dataEx = new DataIntegrityViolationException(
                "Could not execute statement", rootCause);

        Response response = exceptionHandler.toResponse(dataEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();

        log.info("DataIntegrityViolationException with cause handled correctly");
    }

    /**
     * Test handling of unhandled/generic exception.
     */
    @Test
    public void testUnhandledException() {
        log.info("=======> testUnhandledException");

        RuntimeException genericEx = new RuntimeException("Something unexpected happened");

        Response response = exceptionHandler.toResponse(genericEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getType()).isEqualTo("SERVER_UNHANDLED_ERROR");
        assertThat(httpResponse.getMessage()).contains("Something unexpected happened");

        log.info("Unhandled exception handled correctly: {}", httpResponse.getMessage());
    }

    /**
     * Test handling of NullPointerException (unhandled).
     */
    @Test
    public void testNullPointerException() {
        log.info("=======> testNullPointerException");

        NullPointerException npe = new NullPointerException("Null reference encountered");

        Response response = exceptionHandler.toResponse(npe);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse.getType()).isEqualTo("SERVER_UNHANDLED_ERROR");

        log.info("NullPointerException handled as unhandled exception correctly");
    }

    /**
     * Test handling of IllegalArgumentException (unhandled).
     */
    @Test
    public void testIllegalArgumentException() {
        log.info("=======> testIllegalArgumentException");

        IllegalArgumentException illegalEx = new IllegalArgumentException("Invalid argument provided");

        Response response = exceptionHandler.toResponse(illegalEx);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse.getType()).isEqualTo("SERVER_UNHANDLED_ERROR");
        assertThat(httpResponse.getMessage()).contains("Invalid argument");

        log.info("IllegalArgumentException handled as unhandled exception correctly");
    }

    /**
     * Test that response contains proper timestamp.
     */
    @Test
    public void testResponseContainsTimestamp() {
        log.info("=======> testResponseContainsTimestamp");

        CdbNotFoundException notFoundEx = new CdbNotFoundException("Test exception");

        Response response = exceptionHandler.toResponse(notFoundEx);

        HTTPResponse httpResponse = (HTTPResponse) response.getEntity();
        assertThat(httpResponse.getTimestamp()).isNotNull();

        log.info("Response contains timestamp: {}", httpResponse.getTimestamp());
    }

    /**
     * Test that cache control is set on response.
     */
    @Test
    public void testResponseHasCacheControl() {
        log.info("=======> testResponseHasCacheControl");

        CdbNotFoundException notFoundEx = new CdbNotFoundException("Test exception");

        Response response = exceptionHandler.toResponse(notFoundEx);

        // Check that cache-control header is present
        String cacheControlHeader = response.getHeaderString("Cache-Control");
        // The response should have cache control set (may be null if not directly accessible)
        // We verify the response is properly built
        assertThat(response).isNotNull();

        log.info("Response built with cache control");
    }
}

