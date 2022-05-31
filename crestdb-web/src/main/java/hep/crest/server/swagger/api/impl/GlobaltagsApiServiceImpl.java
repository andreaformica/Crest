package hep.crest.server.swagger.api.impl;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.args.GtagQueryArgs;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.services.GlobalTagService;
import hep.crest.server.swagger.api.GlobaltagsApiService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.server.swagger.model.CrestBaseResponse;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.GlobalTagSetDto;
import hep.crest.server.swagger.model.RespPage;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagSetDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Rest endpoint to manage global tags. It is used for creation or search of
 * global tags.
 *
 * @author formica
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen",
        date = "2017-09-05T16:23:23.401+02:00")
@Component
@Slf4j
public class GlobaltagsApiServiceImpl extends GlobaltagsApiService {
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;

    /**
     * Helper.
     */
    @Autowired
    private EntityDtoHelper edh;

    /**
     * Service.
     */
    @Autowired
    private GlobalTagService globaltagService;

    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    /**
     * Resource bundle.
     */
    private final ResourceBundle bundle = ResourceBundle.getBundle("messages", new Locale("US"));

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.GlobaltagsApiService#createGlobalTag(hep.crest.
     * swagger.model.GlobalTagDto, java.lang.String,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createGlobalTag(String force, GlobalTagDto body,
                                    SecurityContext securityContext, UriInfo info) {
        log.info("GlobalTagRestController processing request for creating a global tag");
        // If the force mode is active, the insertion time is imposed by the client.
        if (force.equals("false")) {
            // Set to null so that is automatically generated.
            body.setInsertionTime(null);
        }
        // Insert a new global tag.
        final GlobalTag entity = mapper.map(body, GlobalTag.class);
        final GlobalTag saved = globaltagService.insertGlobalTag(entity);
        final GlobalTagDto dto = mapper.map(saved, GlobalTagDto.class);
        // Send the created status.
        return Response.created(info.getRequestUri()).entity(dto).build();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.GlobaltagsApiService#findGlobalTag(java.lang.
     * String, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response findGlobalTag(String name, SecurityContext securityContext, UriInfo info) {
        log.info("GlobalTagRestController processing request for global tag name " + name);
        // Prepare filters.
        final GenericMap filters = new GenericMap();
        filters.put("name", name);
        // Search for a global tag resource.
        final GlobalTag entity = globaltagService.findOne(name);
        final GlobalTagDto dto = mapper.map(entity, GlobalTagDto.class);
        log.debug("Found GlobalTag " + name);
        // Prepare response set.
        final CrestBaseResponse setdto = new GlobalTagSetDto().addResourcesItem(dto)
                .filter(filters).size(1L).datatype("globaltags");
        return Response.ok().entity(setdto).build();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.GlobaltagsApiService#findGlobalTagFetchTags(java
     * .lang.String, java.lang.String, java.lang.String,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response findGlobalTagFetchTags(String name, String record, String label,
                                           SecurityContext securityContext, UriInfo info) {
        // Prepare filters.
        final GenericMap filters = new GenericMap();
        filters.put("name", name);
        // Search for a global tag and associated tags. Use record and label.
        // Presets for record and label is "none".
        log.info("GlobalTagRestController processing request for global tag name " + name);
        // Fetch tags via record and label.
        final List<Tag> entitylist = globaltagService.getGlobalTagByNameFetchTags(name, record,
                label);
        final List<TagDto> dtolist = edh.entityToDtoList(entitylist, TagDto.class);
        final long listsize = dtolist == null ? 0L : dtolist.size();
        log.debug("Found list of tags of length {}", listsize);

        final CrestBaseResponse setdto = new TagSetDto().resources(dtolist)
                .format("TagSetDto")
                .filter(filters).size(listsize).datatype("tags");
        return Response.ok().entity(setdto).build();
    }

    @Override
    public Response listGlobalTags(String name, String workflow, String scenario, String release, BigDecimal validity
            , String description, Integer page, Integer size, String sort, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        log.debug("Search resource list using name={}, page={}, size={}, sort={}", name, page, size,
                sort);
        if (name.equalsIgnoreCase("all")) {
            name = "%";
        }
        // Create query params object
        GtagQueryArgs args = new GtagQueryArgs();
        args.name(name).validity(validity).release(release).workflow(workflow).description(description).scenario(scenario);
        // Create pagination request
        final PageRequest preq = prh.createPageRequest(page, size, sort);
        // Launch query
        // Search for global tags using where conditions.
        final Page<GlobalTag> entitypage = globaltagService.selectGlobalTagList(args, preq);
        log.debug("Retrieved pages of global tags: {}", entitypage.getTotalPages());
        RespPage respPage = new RespPage().size(entitypage.getSize())
                .totalElements(entitypage.getTotalElements()).totalPages(entitypage.getTotalPages())
                .number(entitypage.getNumber());

        log.debug("Transform to dto global tags");
        final List<GlobalTagDto> dtolist = edh.entityToDtoList(entitypage.toList(), GlobalTagDto.class);
        final Response.Status rstatus = Response.Status.OK;
        log.debug("Prepare response dto set global tags : {}", dtolist.size());
        final CrestBaseResponse setdto = new GlobalTagSetDto().resources(dtolist)
                .size((long) dtolist.size())
                .page(respPage)
                .datatype("globaltags");
        // Create filters
        GenericMap filters = new GenericMap();
        filters.put("name", name);
        filters.put("workflow", workflow);
        filters.put("description", description);
        filters.put("scenario", scenario);
        filters.put("release", release);
        filters.put("validity", (validity != null) ? validity.toString() : null);
        setdto.filter(filters);
        return Response.status(rstatus).entity(setdto).build();
    }
}
