package hep.crest.server.swagger.api;

import hep.crest.swagger.model.*;
import hep.crest.server.swagger.api.GlobaltagmapsApiService;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import hep.crest.swagger.model.GlobalTagMapDto;

import java.util.Map;
import java.util.List;
import hep.crest.server.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.*;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/globaltagmaps")


@io.swagger.annotations.Api(description = "the globaltagmaps API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class GlobaltagmapsApi  {
   @Autowired
   private GlobaltagmapsApiService delegate;

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a GlobalTagMap in the database.", notes = "This method allows to insert a GlobalTag.Arguments: GlobalTagMapDto should be provided in the body as a JSON file.", response = GlobalTagMapDto.class, tags={ "globaltagmaps", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 201, message = "successful operation", response = GlobalTagMapDto.class)
    })
    public Response createGlobalTagMap(@ApiParam(value = "") @Valid  GlobalTagMapDto globalTagMapDto,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.createGlobalTagMap(globalTagMapDto, securityContext, info);
    }
    @DELETE
    @Path("/{name}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete GlobalTagMapDto lists.", notes = "This method search for mappings using the global tag name and deletes all mappings.", response = GlobalTagMapDto.class, tags={ "globaltagmaps", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GlobalTagMapDto.class)
    })
    public Response deleteGlobalTagMap(@ApiParam(value = "the global tag name", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "label: the generic name labelling all tags of a certain kind.", required = true, defaultValue = "none") @DefaultValue("none") @QueryParam("label") @NotNull  String label,@ApiParam(value = "tagname: the name of the tag associated.", required = true, defaultValue = "none") @DefaultValue("none") @QueryParam("tagname") @NotNull  String tagname,@ApiParam(value = "record: the record.") @QueryParam("record")  String record,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.deleteGlobalTagMap(name, label, tagname, record, securityContext, info);
    }
    @GET
    @Path("/{name}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Find GlobalTagMapDto lists.", notes = "This method search for mappings using the global tag name.", response = GlobalTagMapDto.class, tags={ "globaltagmaps", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GlobalTagMapDto.class)
    })
    public Response findGlobalTagMap(@ApiParam(value = "", required = true) @PathParam("name") @NotNull  String name,@ApiParam(value = "If the mode is BackTrace then it will search for global tags containing the tag <name>" , defaultValue="Trace")@HeaderParam("X-Crest-MapMode") String xCrestMapMode,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        return delegate.findGlobalTagMap(name, xCrestMapMode, securityContext, info);
    }
}
