package hep.crest.server.swagger.api;

import hep.crest.server.swagger.model.*;
import hep.crest.server.swagger.api.GlobaltagsApiService;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.api.impl.JAXRSContext;

import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.GlobalTagSetDto;
import hep.crest.server.swagger.model.HTTPResponse;
import hep.crest.server.swagger.model.TagSetDto;

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

@Path("/globaltags")


@io.swagger.annotations.Api(description = "the globaltags API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class GlobaltagsApi  {
   @Autowired
   private GlobaltagsApiService delegate;
   @Context
   protected Request request;
   @Context
   protected HttpHeaders headers;
   @Autowired
   protected JAXRSContext context;

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a GlobalTag in the database.", notes = "This method allows to insert a GlobalTag.Arguments: GlobalTagDto should be provided in the body as a JSON file.", response = GlobalTagDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "globaltags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = GlobalTagDto.class)
    })
    public Response createGlobalTag(@ApiParam(value = "force: tell the server if it should use or not the insertion time provided {default: false}", defaultValue = "false") @DefaultValue("false") @QueryParam("force")  String force,@ApiParam(value = "") @Valid  GlobalTagDto globalTagDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.createGlobalTag(force, globalTagDto, securityContext, info);
    }
    @GET
    @Path("/{name}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a GlobalTagDto by name", notes = "This method will search for a global tag with the given name. Only one global tag should be returned.", response = GlobalTagSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "globaltags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GlobalTagSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "resource not found", response = HTTPResponse.class)
    })
    public Response findGlobalTag(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.findGlobalTag(name, securityContext, info);
    }
    @GET
    @Path("/{name}/tags")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a TagDtos lists associated to the global tag name in input.", notes = "This method allows to trace a global tag.Arguments: record=<record> filter output by record, label=<label> filter output by label", response = TagSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "globaltags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagSetDto.class)
    })
    public Response findGlobalTagFetchTags(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "record:  the record string {}", defaultValue = "none") @DefaultValue("none") @QueryParam("record")  String record,@ApiParam(value = "label:  the label string {}", defaultValue = "none") @DefaultValue("none") @QueryParam("label")  String label,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.findGlobalTagFetchTags(name, record, label, securityContext, info);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a GlobalTagDtos lists.", notes = "This method allows to perform search and sorting. Arguments: name=<pattern>, workflow, scenario, release, validity, description page={ipage}, size={isize}, sort=<sortpattern>. ", response = GlobalTagSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "globaltags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GlobalTagSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "resource not found", response = HTTPResponse.class)
    })
    public Response listGlobalTags(@ApiParam(value = "the global tag name search pattern {none}", defaultValue = "all") @DefaultValue("all") @QueryParam("name")  String name,@ApiParam(value = "the global tag workflow search pattern {none}") @QueryParam("workflow")  String workflow,@ApiParam(value = "the global tag scenario search pattern {none}") @QueryParam("scenario")  String scenario,@ApiParam(value = "the global tag release search pattern {none}") @QueryParam("release")  String release,@ApiParam(value = "the global tag validity low limit {x>=validity}") @QueryParam("validity")  Long validity,@ApiParam(value = "the global tag description search pattern {none}") @QueryParam("description")  String description,@ApiParam(value = "page: the page number {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("page")  Integer page,@ApiParam(value = "size: the page size {1000}", defaultValue = "1000") @DefaultValue("1000") @QueryParam("size")  Integer size,@ApiParam(value = "sort: the sort pattern {name:ASC}", defaultValue = "name:ASC") @DefaultValue("name:ASC") @QueryParam("sort")  String sort,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.listGlobalTags(name, workflow, scenario, release, validity, description, page, size, sort, securityContext, info);
    }
}
