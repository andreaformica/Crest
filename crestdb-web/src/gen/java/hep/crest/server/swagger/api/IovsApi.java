package hep.crest.server.swagger.api;

import hep.crest.server.swagger.model.*;
import hep.crest.server.swagger.api.IovsApiService;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.api.impl.JAXRSContext;

import hep.crest.server.swagger.model.HTTPResponse;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovPayloadSetDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.TagSummarySetDto;

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

@Path("/iovs")


@io.swagger.annotations.Api(description = "the iovs API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class IovsApi  {
   @Autowired
   private IovsApiService delegate;
   @Context
   protected Request request;
   @Context
   protected HttpHeaders headers;
   @Autowired
   protected JAXRSContext context;

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a IovDtos lists.", notes = "Retrieves IOVs, with parameterizable method and arguments ", response = IovSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "iovs", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = IovSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response findAllIovs(@ApiParam(value = "the method used will determine which query is executed IOVS, RANGE and AT is a standard IOV query requiring a precise tag name GROUPS is a group query type ", required = true, allowableValues="IOVS, GROUPS, MONITOR", defaultValue = "IOVS") @DefaultValue("IOVS") @Pattern(regexp="IOVS|GROUPS|MONITOR") @QueryParam("method") @NotNull  String method,@ApiParam(value = "the tag name", defaultValue = "none") @DefaultValue("none") @QueryParam("tagname")  String tagname,@ApiParam(value = "snapshot: the snapshot time {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("snapshot")  Long snapshot,@ApiParam(value = "the since time as a string {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("since")  String since,@ApiParam(value = "the until time as a string {INF}", defaultValue = "INF") @DefaultValue("INF") @QueryParam("until")  String until,@ApiParam(value = "the format for since and until {number | ms | iso | run-lumi | custom (yyyyMMdd'T'HHmmssX)} If timeformat is equal number, we just parse the argument as a long. ", allowableValues="NUMBER, MS, ISO, RUN, RUN_LUMI, CUSTOM", defaultValue = "NUMBER") @DefaultValue("NUMBER") @Pattern(regexp="NUMBER|MS|ISO|RUN|RUN_LUMI|CUSTOM") @QueryParam("timeformat")  String timeformat,@ApiParam(value = "The group size represent the pagination type provided for GROUPS query method. ") @QueryParam("groupsize")  Long groupsize,@ApiParam(value = "the hash for searching specific IOV list for a given hash. ") @QueryParam("hash")  String hash,@ApiParam(value = "the page number {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("page")  Integer page,@ApiParam(value = "the page size {10000}", defaultValue = "10000") @DefaultValue("10000") @QueryParam("size")  Integer size,@ApiParam(value = "the sort pattern {id.since:ASC}", defaultValue = "id.since:ASC") @DefaultValue("id.since:ASC") @QueryParam("sort")  String sort,@ApiParam(value = "The query type. The header parameter X-Crest-Query can be : iovs, ranges, at. The iovs represents an exclusive interval, while ranges and at include previous since. This has an impact on how the since and until ranges are applied. " , allowableValues="IOVS, RANGES, AT", defaultValue="IOVS")@HeaderParam("X-Crest-Query") String xCrestQuery,@ApiParam(value = "The since type required in the query. It can be : ms, cool. Since and until will be transformed in these units. It differs from timeformat which indicates how to interpret the since and until strings in input. " , allowableValues="MS, COOL, NUMBER", defaultValue="NUMBER")@HeaderParam("X-Crest-Since") String xCrestSince,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.findAllIovs(method, tagname, snapshot, since, until, timeformat, groupsize, hash, page, size, sort, xCrestQuery, xCrestSince, securityContext, info);
    }
    @GET
    @Path("/size")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get the number o iovs for tags matching pattern.", notes = "This method allows to retrieve the number of iovs in a tag (or pattern). ", response = TagSummarySetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "iovs", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagSummarySetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response getSizeByTag(@ApiParam(value = "the tag name, can be a pattern like MDT%", required = true, defaultValue = "none") @DefaultValue("none") @QueryParam("tagname") @NotNull  String tagname,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.getSizeByTag(tagname, securityContext, info);
    }
    @GET
    @Path("/infos")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Select iovs and payload meta info for a given tagname and in a given range.", notes = "Retrieve a list of iovs with payload metadata associated. The arguments are: tagname={a tag name}, since={since time as string}, until={until time as string}, snapshot={snapshot time as long}' and timeformat={format of since/until}. ", response = IovPayloadSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "iovs", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = IovPayloadSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response selectIovPayloads(@ApiParam(value = "the tag name", required = true, defaultValue = "none") @DefaultValue("none") @QueryParam("tagname") @NotNull  String tagname,@ApiParam(value = "the since time as a string {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("since")  String since,@ApiParam(value = "the until time as a string {INF}", defaultValue = "INF") @DefaultValue("INF") @QueryParam("until")  String until,@ApiParam(value = "the format for since and until {number | ms | iso | custom (yyyyMMdd'T'HHmmssX)} If timeformat is equal number, we just parse the argument as a long. ", defaultValue = "number") @DefaultValue("number") @QueryParam("timeformat")  String timeformat,@ApiParam(value = "the page number {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("page")  Integer page,@ApiParam(value = "the page size {10000}", defaultValue = "10000") @DefaultValue("10000") @QueryParam("size")  Integer size,@ApiParam(value = "the sort pattern {id.since:ASC}", defaultValue = "id.since:ASC") @DefaultValue("id.since:ASC") @QueryParam("sort")  String sort,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.selectIovPayloads(tagname, since, until, timeformat, page, size, sort, securityContext, info);
    }
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create IOVs in the database, associated to a tag name.", notes = "Insert a list of Iovs using an IovSetDto in the request body. It is mandatory to provide an existing tag in input. The referenced payloads should already exists in the DB. ", response = IovSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "iovs", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = IovSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response storeIovBatch(@ApiParam(value = "") @Valid  IovSetDto iovSetDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.storeIovBatch(iovSetDto, securityContext, info);
    }
    @PUT
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create IOV in the database, associated to a tag name.", notes = "Insert an Iov using an IovDto in the request body. It is mandatory to provide an existing tag in input. The referenced payloads should already exists in the DB. ", response = IovSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "iovs", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = IovSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response storeIovOne(@ApiParam(value = "") @Valid  IovDto iovDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.storeIovOne(iovDto, securityContext, info);
    }
}
