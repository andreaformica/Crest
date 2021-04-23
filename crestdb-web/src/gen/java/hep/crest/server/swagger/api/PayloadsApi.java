package hep.crest.server.swagger.api;

import hep.crest.swagger.model.*;
import hep.crest.server.swagger.api.PayloadsApiService;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import java.math.BigDecimal;
import java.io.File;
import hep.crest.swagger.model.HTTPResponse;
import hep.crest.swagger.model.IovSetDto;
import hep.crest.swagger.model.PayloadDto;
import hep.crest.swagger.model.PayloadSetDto;

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

@Path("/payloads")


@io.swagger.annotations.Api(description = "the payloads API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class PayloadsApi  {
   @Autowired
   private PayloadsApiService delegate;

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a Payload in the database.", notes = "This method allows to insert a Payload.Arguments: PayloadDto should be provided in the body as a JSON file.", response = PayloadDto.class, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadDto.class)
    })
    public Response createPayload(@ApiParam(value = "") @Valid  PayloadDto payloadDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.createPayload(payloadDto, securityContext, info);
    }
    @POST
    @Path("/upload")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a Payload in the database.", notes = "This method allows to insert a Payload.Arguments: PayloadDto should be provided in the body as a JSON file.", response = PayloadDto.class, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadDto.class)
    })
    public Response createPayloadMultiForm(
 @FormDataParam("file") FormDataBodyPart fileBodypart ,@ApiParam(value = "", required=true)@FormDataParam("payload")  String payload,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.createPayloadMultiForm(fileBodypart, payload, securityContext, info);
    }
    @GET
    @Path("/{hash}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Finds a payload resource associated to the hash.", notes = "This method retrieves a payload resource.Arguments: hash=<hash> the hash of the payload", response = String.class, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class)
    })
    public Response getPayload(@ApiParam(value = "hash:  the hash of the payload", required = true) @PathParam("hash") @NotNull  String hash,@ApiParam(value = "The format of the output data. The header parameter X-Crest-PayloadFormat can be : BLOB (default) or DTO (in JSON format)." , defaultValue="BLOB")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.getPayload(hash, xCrestPayloadFormat, securityContext, info);
    }
    @GET
    @Path("/{hash}/meta")
    
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Finds a payload resource associated to the hash.", notes = "This method retrieves metadata of the payload resource.Arguments: hash=<hash> the hash of the payload", response = PayloadSetDto.class, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadSetDto.class)
    })
    public Response getPayloadMetaInfo(@ApiParam(value = "hash:  the hash of the payload", required = true) @PathParam("hash") @NotNull  String hash,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.getPayloadMetaInfo(hash, securityContext, info);
    }
    @POST
    @Path("/storebatch")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Create many Payloads in the database, associated to a given iov since list and tag name.", notes = "This method allows to insert a Payload and an IOV. Arguments: tagname,stream,end time. The header parameter X-Crest-PayloadFormat can be : JSON (default) or TXT or BLOB", response = IovSetDto.class, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = IovSetDto.class)
    })
    public Response storePayloadBatchWithIovMultiForm(@ApiParam(value = "The tag name", required=true)@FormDataParam("tag")  String tag,@ApiParam(value = "The list of iovs as a set", required=true)@FormDataParam("iovsetupload")  String iovsetupload,@ApiParam(value = "The format of the input data" , defaultValue="FILE")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,@ApiParam(value = "The object type")@FormDataParam("objectType")  String objectType,@ApiParam(value = "The version")@FormDataParam("version")  String version,@ApiParam(value = "The end time")@FormDataParam("endtime")  BigDecimal endtime,@ApiParam(value = "The streamerInfo CLOB as a string")@FormDataParam("streamerInfo")  String streamerInfo,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.storePayloadBatchWithIovMultiForm(tag, iovsetupload, xCrestPayloadFormat, objectType, version, endtime, streamerInfo, securityContext, info);
    }
    @POST
    @Path("/store")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Create a Payload in the database, associated to a given iov since and tag name.", notes = "This method allows to insert a Payload and an IOV. Arguments: since,tagname,stream,end time. The header parameter X-Crest-PayloadFormat can be : JSON (default) or TXT or BLOB", response = HTTPResponse.class, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = HTTPResponse.class)
    })
    public Response storePayloadWithIovMultiForm(
 @FormDataParam("file") FormDataBodyPart fileBodypart ,@ApiParam(value = "The tag name", required=true)@FormDataParam("tag")  String tag,@ApiParam(value = "The since time", required=true)@FormDataParam("since")  BigDecimal since,@ApiParam(value = "The format of the input data" , defaultValue="JSON")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,@ApiParam(value = "The object type")@FormDataParam("objectType")  String objectType,@ApiParam(value = "The version")@FormDataParam("version")  String version,@ApiParam(value = "The end time")@FormDataParam("endtime")  BigDecimal endtime,@ApiParam(value = "The streamerInfo CLOB as a string")@FormDataParam("streamerInfo")  String streamerInfo,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.storePayloadWithIovMultiForm(fileBodypart, tag, since, xCrestPayloadFormat, objectType, version, endtime, streamerInfo, securityContext, info);
    }
    @POST
    @Path("/uploadbatch")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json", "application/xml" })
    @io.swagger.annotations.ApiOperation(value = "Create many Payloads in the database, associated to a given iov since list and tag name.", notes = "This method allows to insert a Payload and an IOV. Arguments: tagname,stream,end time. The header parameter X-Crest-PayloadFormat can be : JSON (default) or TXT or BLOB", response = IovSetDto.class, tags={ "payloads", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = IovSetDto.class)
    })
    public Response uploadPayloadBatchWithIovMultiForm(
 @FormDataParam("files") List<FormDataBodyPart> filesBodypart ,@ApiParam(value = "The tag name", required=true)@FormDataParam("tag")  String tag,@ApiParam(value = "The list of iovs", required=true)@FormDataParam("iovsetupload")  String iovsetupload,@ApiParam(value = "The format of the input data" , defaultValue="FILE")@HeaderParam("X-Crest-PayloadFormat") String xCrestPayloadFormat,@ApiParam(value = "The object type")@FormDataParam("objectType")  String objectType,@ApiParam(value = "The version")@FormDataParam("version")  String version,@ApiParam(value = "The end time")@FormDataParam("endtime")  BigDecimal endtime,@ApiParam(value = "The streamerInfo CLOB as a string")@FormDataParam("streamerInfo")  String streamerInfo,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.uploadPayloadBatchWithIovMultiForm(filesBodypart, tag, iovsetupload, xCrestPayloadFormat, objectType, version, endtime, streamerInfo, securityContext, info);
    }
}
