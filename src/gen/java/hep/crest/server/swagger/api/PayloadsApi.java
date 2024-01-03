package hep.crest.server.swagger.api;

import hep.crest.server.swagger.model.*;
import hep.crest.server.swagger.api.PayloadsApiService;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.impl.JAXRSContext;

import java.math.BigDecimal;
import java.io.File;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.HTTPResponse;
import java.util.Map;
import hep.crest.server.swagger.model.PayloadDto;
import hep.crest.server.swagger.model.PayloadSetDto;
import hep.crest.server.swagger.model.StoreSetDto;

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

@Path("/payloads")


@io.swagger.annotations.Api(description = "the payloads API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class PayloadsApi  {
   @Autowired
   private PayloadsApiService delegate;
   @Context
   protected Request request;
   @Context
   protected HttpHeaders headers;
   @Autowired
   protected JAXRSContext context;

    @GET
    @Path("/{hash}")
    
    @Produces({ "application/octet-stream", "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a payload resource associated to the hash.", notes = "This method retrieves a payload resource. Arguments: hash=<hash> the hash of the payload Depending on the header, this method will either retrieve the data, the metadata of the payload  or the streamerInfo alone. ", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response getPayload(@ApiParam(value = "hash:  the hash of the payload", required = true) @PathParam("hash") @NotNull  String hash,@ApiParam(value = "The format of the output data.  It can be : BLOB (default), META (meta data) or STREAMER (streamerInfo). ", required = true, allowableValues="BLOB, META, STREAMER", defaultValue = "BLOB") @DefaultValue("BLOB") @Pattern(regexp="BLOB|META|STREAMER") @QueryParam("format") @NotNull  String format,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.getPayload(hash, format, securityContext, info);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds Payloads metadata.", notes = "This method allows to perform search and sorting. Arguments: hash=<the payload hash>, minsize=<min size>, objectType=<the type> page={ipage}, size={isize}, sort=<sortpattern>. ", response = PayloadSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response listPayloads(@ApiParam(value = "the hash to search {none}") @QueryParam("hash")  String hash,@ApiParam(value = "the objectType to search") @QueryParam("objectType")  String objectType,@ApiParam(value = "the minimum size to search") @QueryParam("minsize")  Integer minsize,@ApiParam(value = "page: the page number {0}", defaultValue = "0") @DefaultValue("0") @QueryParam("page")  Integer page,@ApiParam(value = "size: the page size {1000}", defaultValue = "1000") @DefaultValue("1000") @QueryParam("size")  Integer size,@ApiParam(value = "sort: the sort pattern {insertionTime:DESC}", defaultValue = "insertionTime:DESC") @DefaultValue("insertionTime:DESC") @QueryParam("sort")  String sort,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.listPayloads(hash, objectType, minsize, page, size, sort, securityContext, info);
    }
    @POST
    
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Create Payloads in the database, associated to a given iov since list and tag name.", notes = "This method allows to insert list of Payloads and IOVs. Payload can be contained in the HASH of the IOV (in case it is a small JSON) or as a reference to external file (FILE). In the first case, the files list can be null. Arguments: tag,version,endtime,objectType,compressionType The header parameter X-Crest-PayloadFormat can be FILE or JSON ", response = StoreSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = StoreSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response storePayloadBatch(@ApiParam(value = "The tag name", required=true)@FormDataParam("tag")  String tag,@ApiParam(value = "the string representing a StoreSetDto in json", required=true)@FormDataParam("storeset")  String storeset,@ApiParam(value = "The format of the input data. StoreSetDto entries will have either the content inline (JSON) or stored via external files (FILE). " , allowableValues="FILE, JSON", defaultValue="FILE")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,
 @FormDataParam("files") List<FormDataBodyPart> filesBodypart ,@ApiParam(value = "The object type")@FormDataParam("objectType")  String objectType,@ApiParam(value = "The compression type")@FormDataParam("compressionType")  String compressionType,@ApiParam(value = "The version")@FormDataParam("version")  String version,@ApiParam(value = "The end time, shall be set at tag level.")@FormDataParam("endtime")  BigDecimal endtime,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.storePayloadBatch(tag, storeset, xCrestPayloadFormat, filesBodypart, objectType, compressionType, version, endtime, securityContext, info);
    }
    @PUT
    @Path("/{hash}")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Update a streamerInfo in a payload", notes = "This method will update the streamerInfo. This is provided via a generic map in the request body containing the key 'streamerInfo' ", response = PayloadDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response updatePayload(@ApiParam(value = "hash:  the hash of the payload", required = true) @PathParam("hash") @NotNull  String hash,@ApiParam(value = "") @Valid  Map<String, String> requestBody,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.updatePayload(hash, requestBody, securityContext, info);
    }
    @PUT
    
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Upload and process large JSON data.", notes = "", response = StoreSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = StoreSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response uploadJson(@ApiParam(value = "The tag name", required=true)@FormDataParam("tag")  String tag,
 @FormDataParam("storeset") FormDataBodyPart storesetBodypart ,@ApiParam(value = "The object type")@FormDataParam("objectType")  String objectType,@ApiParam(value = "The compression type")@FormDataParam("compressionType")  String compressionType,@ApiParam(value = "The version")@FormDataParam("version")  String version,@ApiParam(value = "The end time, shall be set at tag level.")@FormDataParam("endtime")  BigDecimal endtime,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.uploadJson(tag, storesetBodypart, objectType, compressionType, version, endtime, securityContext, info);
    }
}
