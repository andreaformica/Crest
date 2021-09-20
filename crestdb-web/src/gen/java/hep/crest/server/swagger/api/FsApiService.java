package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.swagger.model.TagSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class FsApiService {
    public abstract Response buildTar( @NotNull String tagname, @NotNull Long snapshot,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response findFsTag( @NotNull String tagname, @NotNull String reqid,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
