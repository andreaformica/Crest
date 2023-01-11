package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.*;
import hep.crest.server.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import hep.crest.server.swagger.model.FolderDto;
import hep.crest.server.swagger.model.FolderSetDto;

import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public abstract class FoldersApiService {
    public abstract Response createFolder(FolderDto folderDto,SecurityContext securityContext, UriInfo info) throws NotFoundException;
    public abstract Response listFolders(String schema,SecurityContext securityContext, UriInfo info) throws NotFoundException;
}
