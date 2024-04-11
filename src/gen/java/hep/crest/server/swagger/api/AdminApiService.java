package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.server.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.HTTPResponse;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class AdminApiService {
    public abstract Response removeGlobalTag(String name,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response removeTag(String name,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updateGlobalTag(String name,GlobalTagDto globalTagDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
