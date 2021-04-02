package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.swagger.model.CrestBaseResponse;
import hep.crest.swagger.model.IovDto;
import hep.crest.swagger.model.IovPayloadSetDto;
import hep.crest.swagger.model.IovSetDto;
import hep.crest.swagger.model.TagSummarySetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class IovsApiService {
    public abstract Response createIov(IovDto body,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response findAllIovs( @NotNull String by,Integer page,Integer size,String sort,String dateformat,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response getSize( @NotNull String tagname,Long snapshot,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response getSizeByTag( @NotNull String tagname,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response lastIov(String tagname, String since, Long snapshot, String dateformat, SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response selectGroups( @NotNull String tagname,Long snapshot,SecurityContext securityContext, UriInfo info, Request request, HttpHeaders headers) throws NotFoundException;
    public abstract Response selectIovPayloads(String xCrestQuery,String tagname,String since,String until,Long snapshot,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response selectIovs(String xCrestQuery,String tagname,String since,String until,Long snapshot,SecurityContext securityContext, UriInfo info, Request request, HttpHeaders headers) throws NotFoundException;
    public abstract Response selectSnapshot( @NotNull String tagname, @NotNull Long snapshot,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response storeBatchIovMultiForm(IovSetDto body,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
