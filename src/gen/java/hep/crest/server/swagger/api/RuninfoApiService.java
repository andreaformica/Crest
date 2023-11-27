package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.server.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.server.swagger.model.RunLumiInfoDto;
import hep.crest.server.swagger.model.RunLumiSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class RuninfoApiService {
    public abstract Response createRunInfo(RunLumiSetDto runLumiSetDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response listRunInfo(String from,String to,String format,String mode,Integer page,Integer size,String sort,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response updateRunInfo(RunLumiInfoDto runLumiInfoDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
