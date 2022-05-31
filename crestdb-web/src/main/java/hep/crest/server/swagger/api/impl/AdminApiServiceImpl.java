package hep.crest.server.swagger.api.impl;

import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.Tag;
import hep.crest.server.services.GlobalTagMapService;
import hep.crest.server.services.GlobalTagService;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.api.AdminApiService;
import hep.crest.server.swagger.model.GlobalTagDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Rest endpoint for administration task. Essentially allows to remove
 * resources.
 *
 * @author formica
 */
@Component
@Slf4j
public class AdminApiServiceImpl extends AdminApiService {
    /**
     * Service.
     */
    @Autowired
    private GlobalTagService globalTagService;

    /**
     * Service.
     */
    @Autowired
    private TagService tagService;

    /**
     * Service.
     */
    @Autowired
    private GlobalTagMapService globalTagMapService;

    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    /* (non-Javadoc)
     * @see hep.crest.server.swagger.api.AdminApiService#removeGlobalTag(java.lang.String, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response removeGlobalTag(String name, SecurityContext securityContext, UriInfo info) {
        log.info("AdminRestController processing request for removing a global tag");
        // Remove the global tag identified by name.
        globalTagService.removeGlobalTag(name);
        return Response.ok().build();
    }

    /*
     * (non-Javadoc)
     * @see hep.crest.server.swagger.api.AdminApiService#removeTag(java.lang.String,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response removeTag(String name, SecurityContext securityContext, UriInfo info) {
        log.info("AdminRestController processing request for removing tag {}", name);
        // Remove the tag with name.
        Tag removableTag = tagService.findOne(name);
        Iterable<GlobalTagMap> assgt = globalTagMapService.getTagMapByTagName(name);
        if (assgt.iterator().hasNext()) {
            log.error("Cannot remove tag {}", name);
            throw new CdbSQLException("Cannot remove tag " + name + ": clean up associations with global tags");
        }
        // TODO: you can also test for locking of the tag or similar.
        try {
            tagService.removeTag(name);
            return Response.ok().build();
        }
        catch (JDBCException e) {
            throw new CdbSQLException("Error in SQL execution", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see hep.crest.server.swagger.api.AdminApiService#updateGlobalTag(java.lang.
     * String, hep.crest.swagger.model.GlobalTagDto,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response updateGlobalTag(String name, GlobalTagDto body, SecurityContext securityContext, UriInfo info) {
        log.info("AdminRestController processing request for updating global tag {} using {}", name, body);
        final char type = body.getType() != null ? body.getType().charAt(0) : 'N';

        // Find the global tag corresponding to input name.
        final GlobalTag entity = globalTagService.findOne(name);
//	        final char type = entity.getType() != null ? entity.getType() : 'N';

        // Compare fields to set them from the input body object provided by the client.
        if (entity.description() != body.getDescription()) {
            // change description.
            entity.description(body.getDescription());
        }
        if (entity.release() != body.getRelease()) {
            // change release.
            entity.release(body.getRelease());
        }
        if (entity.workflow() != body.getWorkflow()) {
            // change workflow.
            entity.workflow(body.getWorkflow());
        }
        if (entity.scenario() != body.getScenario()) {
            // change scenario.
            entity.scenario(body.getScenario());
        }
        if (entity.type() != type) {
            // change type.
            entity.type(type);
        }
        // Update the global tag.
        final GlobalTag saved = globalTagService.updateGlobalTag(entity);
        final GlobalTagDto dto = mapper.map(saved, GlobalTagDto.class);
        return Response.ok().entity(dto).build();
    }
}
