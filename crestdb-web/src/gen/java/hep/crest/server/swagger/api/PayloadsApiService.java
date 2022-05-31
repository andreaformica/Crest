package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.server.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.math.BigDecimal;
import java.io.File;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.HTTPResponse;
import hep.crest.server.swagger.model.IovSetDto;
import java.util.Map;
import hep.crest.server.swagger.model.PayloadDto;
import hep.crest.server.swagger.model.PayloadSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class PayloadsApiService {
    public abstract Response createPayload(PayloadDto payloadDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response createPayloadMultiForm(FormDataBodyPart _fileBodypart,String payload,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response getPayload(String hash,String xCrestPayloadFormat,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response getPayloadMetaInfo(String hash,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response storePayloadBatchWithIovMultiForm(String tag,String iovsetupload,String xCrestPayloadFormat,String objectType,String version,BigDecimal endtime,String streamerInfo,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response storePayloadWithIovMultiForm(FormDataBodyPart _fileBodypart,String tag,BigDecimal since,String xCrestPayloadFormat,String objectType,String version,BigDecimal endtime,String streamerInfo,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updatePayload(String hash,Map<String, String> requestBody,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response uploadPayloadBatchWithIovMultiForm(List<FormDataBodyPart> filesBodypart,String tag,String iovsetupload,String xCrestPayloadFormat,String objectType,String version,BigDecimal endtime,String streamerInfo,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
