package hep.crest.server.swagger.api.impl;

import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.Tag;
import hep.crest.server.services.GlobalTagMapService;
import hep.crest.server.services.GlobalTagService;
import hep.crest.server.services.TagMetaService;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.api.AdminApiService;
import hep.crest.swagger.model.GlobalTagDto;
import hep.crest.swagger.model.TagMetaDto;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.Set;

/**
 * Rest endpoint for administration task. Essentially allows to remove
 * resources.
 *
 * @author formica
 */
@Component
public class AdminApiServiceImpl extends AdminApiService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AdminApiServiceImpl.class);

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
    private TagMetaService tagMetaService;

    /**
     * Service.
     */
    @Autowired
    private GlobalTagMapService globalTagMapService;

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

    /**
     * Response helper.
     */
    @Autowired
    private ResponseFormatHelper rfh;

    /*
     * (non-Javadoc)
     * @see hep.crest.server.swagger.api.AdminApiService#removeGlobalTag(java.lang.
     * String, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
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
        log.info("AdminRestController processing request for removing a tag");
        // Remove the tag with name.
        Tag removableTag = tagService.findOne(name);
        Iterable<GlobalTagMap> assgt = globalTagMapService.getTagMapByTagName(name);
        if (assgt.iterator().hasNext()) {
            log.error("Cannot remove tag {}", name);
            throw new CdbSQLException("Cannot remove tag " + name + ": clean up associations with global tags");
        }
        // TODO: you can also test for locking of the tag or similar.
        // Remove meta information associated with the tag.
        TagMetaDto metadto;
        try {
            metadto = tagMetaService.findMeta(name);
            if (metadto != null) {
                tagMetaService.removeTagMeta(name);
            }
        }
        catch (CdbNotFoundException e) {
            log.warn("The meta information for tag {} is not present...", name);
        }
        tagService.removeTag(name);
        return Response.ok().build();
    }

    /*
     * (non-Javadoc)
     * @see hep.crest.server.swagger.api.AdminApiService#updateGlobalTag(java.lang.
     * String, hep.crest.swagger.model.GlobalTagDto,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response updateGlobalTag(String name, GlobalTagDto body, SecurityContext securityContext, UriInfo info) {
        log.info("AdminRestController processing request for updating a global tag using " + body);
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
