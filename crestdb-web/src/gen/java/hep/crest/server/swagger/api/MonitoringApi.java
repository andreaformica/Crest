package hep.crest.server.swagger.api;

import io.swagger.annotations.ApiParam;

import hep.crest.server.swagger.impl.JAXRSContext;

import hep.crest.server.swagger.model.HTTPResponse;
import hep.crest.server.swagger.model.PayloadTagInfoSetDto;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.*;

@Path("/monitoring/payloads")


@io.swagger.annotations.Api(description = "the monitoring API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen")
public class MonitoringApi  {
   @Autowired
   private MonitoringApiService delegate;
   @Context
   protected Request request;
   @Context
   protected HttpHeaders headers;
   @Autowired
   protected JAXRSContext context;

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieves monitoring information on payload as a list of PayloadTagInfoDtos.", notes = "This method allows to perform search and sorting.Arguments: tagname=<pattern>, page={ipage}, size={isize}, sort=<sortpattern>. The pattern <pattern> is in the form <param-name><operation><param-value>       <param-name> is the name of one of the fields in the dto       <operation> can be [< : >] ; for string use only [:]        <param-value> depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for <pattern>.      The pattern <sortpattern> is <field>:[DESC|ASC]", response = PayloadTagInfoSetDto.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "BearerAuth")
    }, tags={ "monitoring", })
    @io.swagger.annotations.ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = PayloadTagInfoSetDto.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = HTTPResponse.class),
        @io.swagger.annotations.ApiResponse(code = 200, message = "Generic error response", response = HTTPResponse.class)
    })
    public Response listPayloadTagInfo(@ApiParam(value = "tagname: the search pattern {none}", defaultValue = "none") @DefaultValue("none") @QueryParam("tagname")  String tagname,@Context SecurityContext securityContext,@Context UriInfo info)
    throws NotFoundException {
        context.setHttpHeaders(headers);
        context.setRequest(request);
        return delegate.listPayloadTagInfo(tagname, securityContext, info);
    }
}
