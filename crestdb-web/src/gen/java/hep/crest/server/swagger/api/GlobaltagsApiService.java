package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.swagger.model.GlobalTagDto;
import hep.crest.swagger.model.GlobalTagSetDto;
import hep.crest.swagger.model.TagSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class GlobaltagsApiService {
    public abstract Response createGlobalTag(String force,GlobalTagDto globalTagDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response findGlobalTag(String name,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response findGlobalTagFetchTags(String name,String record,String label,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response listGlobalTags(String by,Integer page,Integer size,String sort,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
