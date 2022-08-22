package hep.crest.server.swagger.api.impl;

import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.repositories.monitoring.IMonitoringRepository;
import hep.crest.server.swagger.api.MonitoringApiService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.server.swagger.model.CrestBaseResponse;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.PayloadTagInfoDto;
import hep.crest.server.swagger.model.PayloadTagInfoSetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Rest endpoint for monitoring informations.
 *
 * @author formica
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-11-07T14:29"
                                                                                                   + ":18.354+01:00")
@Component
@Slf4j
public class MonitoringApiServiceImpl extends MonitoringApiService {

    /**
     * Helper.
     */
    @Autowired
    PageRequestHelper prh;

    /**
     * Repository.
     */
    @Autowired
    IMonitoringRepository monitoringrepo;

    /*
     * (non-Javadoc)
     * @see
     * hep.crest.server.swagger.api.MonitoringApiService#listPayloadTagInfo(java.
     * lang.String, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response listPayloadTagInfo(String tagname, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        log.debug("Search resource list using tagname or pattern={}", tagname);
        List<PayloadTagInfoDto> dtolist = null;
        String tagpattern = tagname;

        // Set default tag pattern.
        if ("none".equals(tagpattern)) {
            // select any tag.
            tagpattern = "%";
        }
        else {
            // Add special pattern regexp.
            tagpattern = "%" + tagpattern + "%";
        }
        // Create filters
        final GenericMap filters = new GenericMap();
        filters.put("tagname", tagpattern);
        // Select tag informations.
        dtolist = monitoringrepo.selectTagInfo(tagpattern);
        // The dtolist should always be non null....
        // Create the PayloadTagInfoSet
        final CrestBaseResponse setdto = new PayloadTagInfoSetDto().resources(dtolist)
                .filter(filters).size(1L).datatype("payloadtaginfos");
        // Return 200.
        return Response.ok().entity(setdto).build();
    }
}
