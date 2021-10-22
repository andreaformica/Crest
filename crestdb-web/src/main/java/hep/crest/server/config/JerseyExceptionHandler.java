package hep.crest.server.config;

import hep.crest.data.exceptions.CdbBadRequestException;
import hep.crest.data.exceptions.CdbServiceException;
import hep.crest.server.caching.CachingPolicyService;
import hep.crest.server.swagger.api.ApiResponseMessage;
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
    private CacheControl cc;

    /**
     * Post construct.
     */
    @PostConstruct
    private void postConstruct() {
        cc = cachesvc.getGroupsCacheControl(0L);
    }

    @Override
    public Response toResponse(Exception exception) {
        log.warn("Handling exception: {} of type {}", exception.getMessage(), exception.getClass());

        if (exception instanceof WebApplicationException) {
            log.debug("Instance of WebApplicationException...get Response from there.");
            // Jersey exceptions: return their standard response
            WebApplicationException e = (WebApplicationException) exception;
            return e.getResponse();
        }

        if (exception instanceof CdbServiceException) {
            log.debug("Instance of CdbServiceException...generate HTTPResponse");
            // Exceptions thrown by the align-mon code
            CdbServiceException e = (CdbServiceException) exception;
            HTTPResponse resp = new HTTPResponse().timestamp(OffsetDateTime.now())
                    .code(e.getResponseStatus().getStatusCode())
                    .error(e.getResponseStatus().getReasonPhrase())
                    .message(e.getMessage());
            return Response.status(e.getResponseStatus()).cacheControl(cc).entity(resp).build();
        }

        if (exception instanceof DataIntegrityViolationException) {
            log.debug("Instance of DataIntegrityViolationException...generate HTTPResponse creating a CdbSQLException");
            // Exceptions thrown by the align-mon code
            CdbBadRequestException e = new CdbBadRequestException(exception);
            HTTPResponse resp = new HTTPResponse().timestamp(OffsetDateTime.now())
                    .code(e.getResponseStatus().getStatusCode())
                    .error(e.getResponseStatus().getReasonPhrase())
                    .message(e.getMessage());
            return Response.status(e.getResponseStatus()).cacheControl(cc).entity(resp).build();
        }

        log.error("Unhandled exception of type {}: internal server error: {}", exception.getClass(),
                exception.getMessage());
        ApiResponseMessage resp = new ApiResponseMessage(ApiResponseMessage.ERROR, exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).cacheControl(cc).entity(resp).build();
    }
}
