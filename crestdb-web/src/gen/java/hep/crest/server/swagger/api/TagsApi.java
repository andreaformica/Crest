package hep.crest.server.swagger.api;

import hep.crest.server.swagger.model.*;
import hep.crest.server.swagger.api.TagsApiService;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.impl.JAXRSContext;

import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.HTTPResponse;
import java.util.Map;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagMetaDto;
import hep.crest.server.swagger.model.TagMetaSetDto;
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

@Path("/tags")


@io.swagger.annotations.Api(description = "the tags API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class TagsApi  {
   @Autowired
   private TagsApiService delegate;
   @Context
   protected Request request;
   @Context
   protected HttpHeaders headers;
   @Autowired
   protected JAXRSContext context;

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a Tag in the database.", notes = "This method allows to insert a Tag.Arguments: TagDto should be provided in the body as a JSON file.", response = TagDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "tags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response createTag(@ApiParam(value = "") @Valid  TagDto tagDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.createTag(tagDto, securityContext, info);
    }
    @POST
    @Path("/{name}/meta")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a TagMeta in the database.", notes = "This method allows to insert a TagMeta.Arguments: TagMetaDto should be provided in the body as a JSON file.", response = TagMetaDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "tags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagMetaDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response createTagMeta(@ApiParam(value = "name: the tag name", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "") @Valid  TagMetaDto tagMetaDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.createTagMeta(name, tagMetaDto, securityContext, info);
    }
    @GET
    @Path("/{name}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a TagDto by name", notes = "This method will search for a tag with the given name. Only one tag should be returned.", response = TagSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "tags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response findTag(@ApiParam(value = "name: the tag name", required = true) @PathParam("name") @NotNull  String name,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.findTag(name, securityContext, info);
    }
    @GET
    @Path("/{name}/meta")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a TagMetaDto by name", notes = "This method will search for a tag metadata with the given name. Only one tag should be returned.", response = TagMetaSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "tags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagMetaSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response findTagMeta(@ApiParam(value = "name: the tag name", required = true) @PathParam("name") @NotNull  String name,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.findTagMeta(name, securityContext, info);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a TagDtos lists.", notes = "This method allows to perform search and sorting. Arguments: name=<pattern>, objectType, timeType, description page={ipage}, size={isize}, sort=<sortpattern>. ", response = TagSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "tags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response listTags(@ApiParam(value = "the tag name search pattern {all}", defaultValue = "all") @DefaultValue("all") @QueryParam("name")  String name,@ApiParam(value = "the tag timeType {none}") @QueryParam("timeType")  String timeType,@ApiParam(value = "the tag objectType search pattern {none}") @QueryParam("objectType")  String objectType,@ApiParam(value = "the global tag description search pattern {none}") @QueryParam("description")  String description,@ApiParam(value = "page: the page number {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("page")  Integer page,@ApiParam(value = "size: the page size {1000}", defaultValue = "1000") @DefaultValue("1000") @QueryParam("size")  Integer size,@ApiParam(value = "sort: the sort pattern {name:ASC}", defaultValue = "name:ASC") @DefaultValue("name:ASC") @QueryParam("sort")  String sort,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.listTags(name, timeType, objectType, description, page, size, sort, securityContext, info);
    }
    @PUT
    @Path("/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update a TagDto by name", notes = "This method will search for a tag with the given name, and update its content for the provided body fields. Only the following fields can be updated: description, timeType, objectTime, endOfValidity, lastValidatedTime.", response = TagDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "tags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response updateTag(@ApiParam(value = "name: the tag name", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "") @Valid  Map<String, String> requestBody,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.updateTag(name, requestBody, securityContext, info);
    }
    @PUT
    @Path("/{name}/meta")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Update a TagMetaDto by name", notes = "This method will search for a tag with the given name, and update its content for the provided body fields. Only the following fields can be updated: description, timeType, objectTime, endOfValidity, lastValidatedTime.", response = TagMetaDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "tags", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagMetaDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response updateTagMeta(@ApiParam(value = "name: the tag name", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "") @Valid  Map<String, String> requestBody,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.updateTagMeta(name, requestBody, securityContext, info);
    }
}
