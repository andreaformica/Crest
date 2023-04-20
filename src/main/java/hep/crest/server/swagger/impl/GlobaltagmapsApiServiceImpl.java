package hep.crest.server.swagger.impl;

import hep.crest.server.data.pojo.GlobalTagMap;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.services.GlobalTagMapService;
import hep.crest.server.swagger.api.GlobaltagmapsApiService;
import hep.crest.server.swagger.model.CrestBaseResponse;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.GlobalTagMapDto;
import hep.crest.server.swagger.model.GlobalTagMapSetDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Rest endpoint to deal with mappings between tags and global tags. Allows to
 * create and find mappings.
 *
 * @author formica
 */
@Component
@Slf4j
public class GlobaltagmapsApiServiceImpl extends GlobaltagmapsApiService {
    /**
     * Service.
     */
    @Autowired
    private GlobalTagMapService globaltagmapService;

    /**
     * Helper.
     */
    @Autowired
    EntityDtoHelper edh;

    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    /*
     * (non-Javadoc)
     * @see
     * hep.crest.server.swagger.api.GlobaltagmapsApiService#createGlobalTagMap(hep.
     * crest.swagger.model.GlobalTagMapDto, javax.ws.rs.core.SecurityContext,
     * javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createGlobalTagMap(GlobalTagMapDto body, SecurityContext securityContext, UriInfo info) {
        log.info("Associate tag {} to globaltag {}", body.getTagName(), body.getGlobalTagName());
        // Insert new mapping resource.
        GlobalTagMap entity = mapper.map(body, GlobalTagMap.class);
        final GlobalTagMap saved = globaltagmapService.insertGlobalTagMap(entity);
        GlobalTagMapDto dto = mapper.map(saved, GlobalTagMapDto.class);
        return Response.created(info.getRequestUri()).entity(dto).build();
    }

    /*
     * (non-Javadoc)
     * @see
     * hep.crest.server.swagger.api.GlobaltagmapsApiService#findGlobalTagMap(java.
     * lang.String, java.lang.String, javax.ws.rs.core.SecurityContext,
     * javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response findGlobalTagMap(String name, String xCrestMapMode, SecurityContext securityContext, UriInfo info) {
        log.info("Get tags for globaltag {} ", name);
        // Prepare filters
        final GenericMap filters = new GenericMap();
        filters.put("name", name);
        filters.put("mode", xCrestMapMode);
        Iterable<GlobalTagMap> entitylist = null;
        // If there is no header then set it to Trace mode. Implies that you search tags
        // associated with a global tag. The input name will be considered as a
        // GlobalTag name.
        if (xCrestMapMode == null) {
            xCrestMapMode = "Trace";
        }
        if ("trace".equalsIgnoreCase(xCrestMapMode)) {
            // The header is Trace, so search for tags associated to a global tag.
            entitylist = globaltagmapService.getTagMap(name);
        }
        else {
            // The header is not Trace, so search for global tags associated to a tag.
            // The input name is considered a Tag name.
            entitylist = globaltagmapService.getTagMapByTagName(name);
        }
        List<GlobalTagMapDto> dtolist = edh.entityToDtoList(entitylist, GlobalTagMapDto.class);
        final CrestBaseResponse setdto = new GlobalTagMapSetDto().resources(dtolist).filter(filters)
                .size((long) dtolist.size()).datatype("maps");
        Response.Status status = Response.Status.OK;
        return Response.status(status).entity(setdto).build();
    }

    @Override
    public Response deleteGlobalTagMap(String name, @NotNull String label,
                                       @NotNull String tagname, String mrecord,
                                       SecurityContext securityContext, UriInfo info) {
        log.info("Remove association of tag {} for globaltag {} ", tagname, name);
        // Prepare filters
        final GenericMap filters = new GenericMap();
        filters.put("globaltagname", name);
        filters.put("label", label);
        filters.put("tagname", tagname);
        if (mrecord != null) {
            filters.put("record", mrecord);
        }
        Iterable<GlobalTagMap> entitylist = null;
        // If there is no header then set it to Trace mode. Implies that you search tags
        // associated with a global tag. The input name will be considered as a
        // GlobalTag name.
        entitylist = globaltagmapService.findMapsByGlobalTagLabelTag(name, label, tagname, mrecord);
        // Delete the full list inside a transaction.
        List<GlobalTagMap> deletedlist = globaltagmapService.deleteMapList(entitylist);
        // Return the deleted list.
        List<GlobalTagMapDto> dtolist = edh.entityToDtoList(deletedlist, GlobalTagMapDto.class);
        final CrestBaseResponse setdto = new GlobalTagMapSetDto().resources(dtolist).filter(filters)
                .size((long) dtolist.size()).datatype("maps");
        return Response.status(Response.Status.OK).entity(setdto).build();
    }

}
