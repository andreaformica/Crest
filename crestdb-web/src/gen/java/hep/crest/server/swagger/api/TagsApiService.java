package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.util.Map;
import hep.crest.swagger.model.TagDto;
import hep.crest.swagger.model.TagMetaDto;
import hep.crest.swagger.model.TagMetaSetDto;
import hep.crest.swagger.model.TagSetDto;

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
    public abstract Response listTags(String by,Integer page,Integer size,String sort,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updateTag(String name,Map<String, String> requestBody,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updateTagMeta(String name,Map<String, String> requestBody,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
