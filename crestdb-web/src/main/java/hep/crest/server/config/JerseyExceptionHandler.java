package hep.crest.server.config;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbBadRequestException;
import hep.crest.server.caching.CachingPolicyService;
import hep.crest.swagger.model.HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.OffsetDateTime;

/**
 * Exception handler.
 * This handler will catch all exceptions thrown and provide a dedicated output response
 * in case they are thrown by server code.
 */
@Provider
public class JerseyExceptionHandler implements ExceptionMapper<Exception> {
    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(JerseyExceptionHandler.class);

    /**
     * Service.
     */
    @Autowired
    private CachingPolicyService cachesvc;

    /**
     * A cache control for errors.
     */
    private CacheControl cc = null;

    /**
     * Default ctor.
     */
    public JerseyExceptionHandler() {
        // Empty constructor. All initialization in postConstruct().
    }

    /**
     * Post construct.
     * Set the default cache to 0.
     */
    @PostConstruct
    private void postConstruct() {
        // CacheControl for exceptions is set to maxAge=0.
        // In this way we never cache exceptions.
        cc = cachesvc.getGroupsCacheControl(0L);
    }

    @Override
    public Response toResponse(Exception exception) {
        log.warn("Handling exception: {} of type {}", exception.getMessage(), exception.getClass());
        // If exception is a webapplication exception
        if (exception instanceof WebApplicationException) {
            log.debug("Instance of WebApplicationException...get Response from there.");
            // Jersey exceptions: return their standard response
            WebApplicationException e = (WebApplicationException) exception;
            return e.getResponse();
        }
        // If exception is a AbstractCdbServiceException exception
        if (exception instanceof AbstractCdbServiceException) {
            log.debug("Instance of CdbServiceException...generate HTTPResponse");
            // Exceptions thrown by the crest server code
            AbstractCdbServiceException e = (AbstractCdbServiceException) exception;
            HTTPResponse resp = new HTTPResponse().timestamp(OffsetDateTime.now())
                    .code(e.getResponseStatus().getStatusCode())
                    .error(e.getResponseStatus().getReasonPhrase())
                    .type(e.getType())
                    .message(e.getMessage());
            // Set the response and the cachecontrol.
            return Response.status(e.getResponseStatus()).cacheControl(cc).entity(resp).build();
        }
        // If exception is a DataIntegrityViolationException exception
        if (exception instanceof DataIntegrityViolationException) {
            log.debug("Instance of DataIntegrityViolationException...generate HTTPResponse creating a CdbSQLException");
            // Exceptions thrown by the SQL code
            CdbBadRequestException e = new CdbBadRequestException(exception);
            HTTPResponse resp = new HTTPResponse().timestamp(OffsetDateTime.now())
                    .code(e.getResponseStatus().getStatusCode())
                    .error(e.getResponseStatus().getReasonPhrase())
                    .type(e.getType())
                    .message(e.getMessage());
            // Set the response and the cachecontrol.
            return Response.status(e.getResponseStatus()).cacheControl(cc).entity(resp).build();
        }
        // The exception is unhandled, so use INTERNAL_SERVER_ERROR as output code.
        log.error("Unhandled exception of type {}: internal server error: {}", exception.getClass(),
                exception.getMessage());
        HTTPResponse resp = new HTTPResponse().timestamp(OffsetDateTime.now())
                .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .error(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .type("SERVER_UNHANDLED_ERROR")
                .message(exception.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).cacheControl(cc).entity(resp).build();
    }
}
