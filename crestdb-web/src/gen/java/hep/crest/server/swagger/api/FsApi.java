package hep.crest.server.swagger.api;

import hep.crest.server.swagger.api.impl.JAXRSContext;
import hep.crest.swagger.model.*;
import hep.crest.server.swagger.api.FsApiService;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import hep.crest.swagger.model.TagSetDto;

import java.util.Map;
import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.*;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/fs")


@io.swagger.annotations.Api(description = "the fs API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class FsApi  {
   @Autowired
   private FsApiService delegate;
 @Context
 protected HttpServletRequest request;
 @Autowired
 protected JAXRSContext context;

    @POST
    @Path("/tar")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Dump a tag into filesystem and retrieve the tar file asynchronously.", notes = "This method allows to request a tar file from the server using a tag specified in input.", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "fs", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class)
    })
    public Response buildTar(@ApiParam(value = "tagname: the tag name {none}", required = true, defaultValue = "none") @DefaultValue("none") @QueryParam("tagname") @NotNull  String tagname,@ApiParam(value = "snapshot: the snapshot time {0}", required = true, defaultValue = "0") @DefaultValue("0") @QueryParam("snapshot") @NotNull  Long snapshot,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
     context.setHttpServletRequest(request);
        return delegate.buildTar(tagname, snapshot, securityContext, info);
    }
    @POST
    @Path("/tag")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Read a tag from filesystem. If the tag does not exists dump it.", notes = "This method allows to dump a tag on filesystem.", response = TagSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "fs", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = TagSetDto.class)
    })
    public Response findFsTag(@ApiParam(value = "tagname: the tag name {none}", required = true, defaultValue = "none") @DefaultValue("none") @QueryParam("tagname") @NotNull  String tagname,@ApiParam(value = "reqid: the request id from tar method {none}", required = true, defaultValue = "none") @DefaultValue("none") @QueryParam("reqid") @NotNull  String reqid,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.findFsTag(tagname, reqid, securityContext, info);
    }
}
