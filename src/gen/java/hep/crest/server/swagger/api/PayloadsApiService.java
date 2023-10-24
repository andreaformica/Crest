package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.server.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.math.BigDecimal;
import java.io.File;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.HTTPResponse;
import java.util.Map;
import hep.crest.server.swagger.model.PayloadDto;
import hep.crest.server.swagger.model.PayloadSetDto;
import hep.crest.server.swagger.model.StoreSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class PayloadsApiService {
    public abstract Response getPayload(String hash, @NotNull String format,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response listPayloads(String hash,String objectType,Integer minsize,Integer page,Integer size,String sort,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response storePayloadBatch(String tag,String storeset,String xCrestPayloadFormat,List<FormDataBodyPart> filesBodypart,String objectType,String compressionType,String version,BigDecimal endtime,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updatePayload(String hash,Map<String, String> requestBody,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response uploadJson(String tag,FormDataBodyPart storesetBodypart,String objectType,String compressionType,String version,BigDecimal endtime,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
