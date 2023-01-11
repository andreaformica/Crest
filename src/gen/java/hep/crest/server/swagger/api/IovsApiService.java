package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.server.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.server.swagger.model.HTTPResponse;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovPayloadSetDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.TagSummarySetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class IovsApiService {
    public abstract Response findAllIovs( @NotNull String method,String tagname,Long snapshot,String since,String until,String timeformat,Long groupsize,String hash,Integer page,Integer size,String sort,String xCrestQuery,String xCrestSince,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response getSizeByTag( @NotNull String tagname,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response selectIovPayloads( @NotNull String tagname,String since,String until,String timeformat,Integer page,Integer size,String sort,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response storeIovBatch(IovSetDto iovSetDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response storeIovOne(IovDto iovDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
