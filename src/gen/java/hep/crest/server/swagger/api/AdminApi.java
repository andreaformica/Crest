package hep.crest.server.swagger.api;

import hep.crest.server.swagger.model.*;
import hep.crest.server.swagger.api.AdminApiService;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.impl.JAXRSContext;

import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.HTTPResponse;

import java.util.Map;
import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.*;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/admin")


@io.swagger.annotations.Api(description = "the admin API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class AdminApi  {
   @Autowired
   private AdminApiService delegate;
   @Context
   protected Request request;
   @Context
   protected HttpHeaders headers;
   @Autowired
   protected JAXRSContext context;

    @DELETE
    @Path("/globaltags/{name}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Remove a GlobalTag from the database.", notes = "This method allows to remove a GlobalTag.Arguments: the name has to uniquely identify a global tag.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "admin", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Void.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response removeGlobalTag(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.removeGlobalTag(name, securityContext, info);
    }
    @DELETE
    @Path("/tags/{name}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Remove a Tag from the database.", notes = "This method allows to remove a Tag.Arguments: the name has to uniquely identify a tag.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "admin", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Void.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response removeTag(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.removeTag(name, securityContext, info);
    }
    @PUT
    @Path("/globaltags/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update a GlobalTag in the database.", notes = "This method allows to update a GlobalTag.Arguments: the name has to uniquely identify a global tag.", response = GlobalTagDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "admin", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GlobalTagDto.class)
    })
    public Response updateGlobalTag(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "") @Valid  GlobalTagDto globalTagDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.updateGlobalTag(name, globalTagDto, securityContext, info);
    }
}
