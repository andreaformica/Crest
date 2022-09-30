package hep.crest.server.swagger.api;

import hep.crest.server.swagger.model.*;
import hep.crest.server.swagger.api.PayloadsApiService;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.api.impl.JAXRSContext;

import java.math.BigDecimal;
import java.io.File;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.HTTPResponse;
import hep.crest.server.swagger.model.IovSetDto;
import java.util.Map;
import hep.crest.server.swagger.model.PayloadDto;
import hep.crest.server.swagger.model.PayloadSetDto;

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

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a Payload in the database.", notes = "This method allows to insert a Payload.Arguments: PayloadDto should be provided in the body as a JSON file.", response = PayloadDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response createPayload(@ApiParam(value = "") @Valid  PayloadDto payloadDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.createPayload(payloadDto, securityContext, info);
    }
    @POST
    @Path("/upload")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a Payload in the database.", notes = "This method allows to insert a Payload.Arguments: PayloadDto should be provided in the body as a JSON file.", response = PayloadDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response createPayloadMultiForm(
 @FormDataParam("file") FormDataBodyPart _fileBodypart ,@ApiParam(value = "", required=true)@FormDataParam("payload")  PayloadDto payload,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.createPayloadMultiForm(_fileBodypart, payload, securityContext, info);
    }
    @GET
    @Path("/{hash}")
    
    @Produces({ "application/_*", "text/plain", "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a payload resource associated to the hash.", notes = "This method retrieves a payload resource.Arguments: hash=<hash> the hash of the payload", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response getPayload(@ApiParam(value = "hash:  the hash of the payload", required = true) @PathParam("hash") @NotNull  String hash,@ApiParam(value = "The format of the output data. The header parameter X-Crest-PayloadFormat can be : BLOB (default) or DTO (in JSON format)." , allowableValues="BLOB, DTO", defaultValue="BLOB")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.getPayload(hash, xCrestPayloadFormat, securityContext, info);
    }
    @GET
    @Path("/{hash}/meta")
    
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Finds a payload resource associated to the hash.", notes = "This method retrieves metadata of the payload resource.Arguments: hash=<hash> the hash of the payload", response = PayloadSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response getPayloadMetaInfo(@ApiParam(value = "hash:  the hash of the payload", required = true) @PathParam("hash") @NotNull  String hash,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.getPayloadMetaInfo(hash, securityContext, info);
    }
    @POST
    @Path("/batch")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Create many Payloads in the database, associated to a given iov since list and tag name.", notes = "This method allows to insert list of Payloads and IOVs. Payload can be contained in the HASH of the IOV (JSON) or as a reference to external file (FILE). In the first case, the files list can be null. Arguments: tag,streamerInfo,endtime. The header parameter X-Crest-PayloadFormat can be FILE or JSON ", response = IovSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = IovSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response storeBatch(
 @FormDataParam("files") List<FormDataBodyPart> filesBodypart ,@ApiParam(value = "The tag name", required=true)@FormDataParam("tag")  String tag,@ApiParam(value = "", required=true)@FormDataParam("iovsetupload")  IovSetDto iovsetupload,@ApiParam(value = "The format of the input data" , allowableValues="FILE, JSON", defaultValue="FILE")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,@ApiParam(value = "The object type")@FormDataParam("objectType")  String objectType,@ApiParam(value = "The version")@FormDataParam("version")  String version,@ApiParam(value = "The end time")@FormDataParam("endtime")  BigDecimal endtime,@ApiParam(value = "The streamerInfo CLOB as a string")@FormDataParam("streamerInfo")  String streamerInfo,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.storeBatch(filesBodypart, tag, iovsetupload, xCrestPayloadFormat, objectType, version, endtime, streamerInfo, securityContext, info);
    }
    @POST
    @Path("/store")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Create a Payload in the database, associated to a given iov since and tag name.", notes = "This method allows to insert a Payload and an IOV. Arguments: since, tag, streamerInfo, endtime. ", response = HTTPResponse.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response storePayloadWithIovMultiForm(
 @FormDataParam("file") FormDataBodyPart _fileBodypart ,@ApiParam(value = "The tag name", required=true)@FormDataParam("tag")  String tag,@ApiParam(value = "The since time", required=true)@FormDataParam("since")  BigDecimal since,@ApiParam(value = "The format of the input data" , allowableValues="JSON, TXT, BLOB, BIN", defaultValue="JSON")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,@ApiParam(value = "The object type")@FormDataParam("objectType")  String objectType,@ApiParam(value = "The version")@FormDataParam("version")  String version,@ApiParam(value = "The end time")@FormDataParam("endtime")  BigDecimal endtime,@ApiParam(value = "The streamerInfo CLOB as a string")@FormDataParam("streamerInfo")  String streamerInfo,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.storePayloadWithIovMultiForm(_fileBodypart, tag, since, xCrestPayloadFormat, objectType, version, endtime, streamerInfo, securityContext, info);
    }
    @PUT
    @Path("/{hash}/meta")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Update a streamerInfo in a payload", notes = "This method will get a payload from its hash, and update streamer info content for the provided body fields: streamerInfo.", response = PayloadSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response updatePayload(@ApiParam(value = "hash:  the hash of the payload", required = true) @PathParam("hash") @NotNull  String hash,@ApiParam(value = "") @Valid  Map<String, String> requestBody,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.updatePayload(hash, requestBody, securityContext, info);
    }
}
