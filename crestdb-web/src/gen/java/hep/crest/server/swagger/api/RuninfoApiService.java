package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.swagger.model.RunInfoSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class RuninfoApiService {
    public abstract Response createRunInfo(RunInfoSetDto body,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response listRunInfo(String by,Integer page,Integer size,String sort,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response selectRunInfo(String from,String to,String format,String mode,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
