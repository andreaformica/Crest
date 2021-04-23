package hep.crest.server.swagger.api;

import hep.crest.swagger.model.*;
import hep.crest.server.swagger.api.AdminApiService;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import hep.crest.swagger.model.GlobalTagDto;

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

    @DELETE
    @Path("/globaltags/{name}")
    
    
    @io.swagger.annotations.ApiOperation(value = "Remove a GlobalTag from the database.", notes = "This method allows to remove a GlobalTag.Arguments: the name has to uniquely identify a global tag.", response = Void.class, tags={ "admin", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Void.class)
    })
    public Response removeGlobalTag(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.removeGlobalTag(name, securityContext, info);
    }
    @DELETE
    @Path("/tags/{name}")
    
    
    @io.swagger.annotations.ApiOperation(value = "Remove a Tag from the database.", notes = "This method allows to remove a Tag.Arguments: the name has to uniquely identify a tag.", response = Void.class, tags={ "admin", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Void.class)
    })
    public Response removeTag(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.removeTag(name, securityContext, info);
    }
    @PUT
    @Path("/globaltags/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update a GlobalTag in the database.", notes = "This method allows to update a GlobalTag.Arguments: the name has to uniquely identify a global tag.", response = GlobalTagDto.class, tags={ "admin", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GlobalTagDto.class)
    })
    public Response updateGlobalTag(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "") @Valid  GlobalTagDto globalTagDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.updateGlobalTag(name, globalTagDto, securityContext, info);
    }
}
