package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.server.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.HTTPResponse;
import java.util.Map;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagMetaDto;
import hep.crest.server.swagger.model.TagMetaSetDto;
import hep.crest.server.swagger.model.TagSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class TagsApiService {
    public abstract Response createTag(TagDto tagDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response createTagMeta(String name,TagMetaDto tagMetaDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response findTag(String name,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response findTagMeta(String name,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response listTags(String name,String timeType,String objectType,String description,Integer page,Integer size,String sort,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updateTag(String name,Map<String, String> requestBody,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updateTagMeta(String name,Map<String, String> requestBody,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
