package hep.crest.server.swagger.api;

import hep.crest.server.swagger.model.*;
import hep.crest.server.swagger.api.FoldersApiService;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.impl.JAXRSContext;

import hep.crest.server.swagger.model.FolderDto;
import hep.crest.server.swagger.model.FolderSetDto;

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

@Path("/folders")


@io.swagger.annotations.Api(description = "the folders API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class FoldersApi  {
   @Autowired
   private FoldersApiService delegate;
   @Context
   protected Request request;
   @Context
   protected HttpHeaders headers;
   @Autowired
   protected JAXRSContext context;

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create an entry for folder information.", notes = "Folder informations go into a dedicated table.", response = FolderDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "folders", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = FolderDto.class)
    })
    public Response createFolder(@ApiParam(value = "") @Valid  FolderDto folderDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.createFolder(folderDto, securityContext, info);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a FolderDto list.", notes = "This method allows to perform search and sorting.Arguments: by=<pattern>, sort=<sortpattern>. The pattern <pattern> is in the form <param-name><operation><param-value>       <param-name> is the name of one of the fields in the dto       <operation> can be [< : >] ; for string use only [:]        <param-value> depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for <pattern>.      The pattern <sortpattern> is <field>:[DESC|ASC]", response = FolderSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "folders", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = FolderSetDto.class)
    })
    public Response listFolders(@ApiParam(value = "the schema pattern {none}", defaultValue = "none") @DefaultValue("none") @QueryParam("schema")  String schema,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.listFolders(schema, securityContext, info);
    }
}
