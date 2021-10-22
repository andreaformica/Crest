package hep.crest.server.swagger.api.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.querydsl.IFilteringCriteria;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.services.TagMetaService;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.server.swagger.api.TagsApiService;
import hep.crest.swagger.model.CrestBaseResponse;
import hep.crest.swagger.model.GenericMap;
import hep.crest.swagger.model.RespPage;
import hep.crest.swagger.model.TagDto;
import hep.crest.swagger.model.TagMetaDto;
import hep.crest.swagger.model.TagMetaSetDto;
import hep.crest.swagger.model.TagSetDto;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Rest endpoint for tag management.
 *
 * @author formica
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen",
        date = "2017-09-05T16:23:23.401+02:00")
@Component
public class TagsApiServiceImpl extends TagsApiService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TagsApiServiceImpl.class);
    /**
     * Resource bundle.
     */
    private final ResourceBundle bundle = ResourceBundle.getBundle("messages", new Locale("US"));
    /**
     * Helper.
     */
    @Autowired
    EntityDtoHelper edh;
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;
    /**
     * Filtering.
     */
    @Autowired
    @Qualifier("tagFiltering")
    private IFilteringCriteria filtering;
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
     *
     * @see
     * hep.crest.server.swagger.api.TagsApiService#createTag(hep.crest.swagger.model
     * .TagDto, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createTag(TagDto body, SecurityContext securityContext, UriInfo info) {
        log.info("TagRestController processing request for creating a tag");
        // Create a tag.
        Tag entity = mapper.map(body, Tag.class);
        final Tag saved = tagService.insertTag(entity);
        log.debug("Created tag {}", saved);
        TagDto dto = mapper.map(saved, TagDto.class);
        // Response is 201.
        log.debug("Created tag DTO {}", dto);
        return Response.created(info.getRequestUri()).entity(dto).build();
    }

    /*
     * (non-Javadoc)
     *
     * @see hep.crest.server.swagger.api.TagsApiService#updateTag(java.lang.String,
     * hep.crest.swagger.model.GenericMap, javax.ws.rs.core.SecurityContext,
     * javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response updateTag(String name, Map<String, String> body, SecurityContext securityContext,
                              UriInfo info) {
        log.info("TagRestController processing request for updating a tag");
        // Search tag.
        final Tag entity = tagService.findOne(name);
        // Send a bad request if body is null.
        if (body == null) {
            return rfh.badRequest("Cannot update tag " + entity.name() + ": body is null");
        }
        // Loop over map body keys.
        for (final String key : body.keySet()) {
            if ("description".equals(key)) {
                // Update description.
                entity.description(body.get(key));
            }
            else if (key == "timeType") {
                entity.timeType(body.get(key));
            }
            else if (key == "lastValidatedTime") {
                final BigDecimal val = new BigDecimal(body.get(key));
                entity.lastValidatedTime(val);
            }
            else if (key == "endOfValidity") {
                final BigDecimal val = new BigDecimal(body.get(key));
                entity.endOfValidity(val);
            }
            else if (key == "synchronization") {
                entity.synchronization(body.get(key));
            }
            else if (key == "payloadSpec") {
                entity.objectType(body.get(key));
            }
            else {
                log.warn("Ignored key {} in updateTag: field does not exists", key);
            }
        }
        final Tag saved = tagService.updateTag(entity);
        TagDto dto = mapper.map(saved, TagDto.class);
        return Response.ok(info.getRequestUri()).entity(dto).build();
    }

    /*
     * (non-Javadoc)
     *
     * @see hep.crest.server.swagger.api.TagsApiService#findTag(java.lang.String,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response findTag(String name, SecurityContext securityContext, UriInfo info) {
        log.info("TagRestController processing request for tag name " + name);
        final GenericMap filters = new GenericMap();
        filters.put("name", name);
        final Tag entity = tagService.findOne(name);
        TagDto dto = mapper.map(entity, TagDto.class);
        // Create the set.
        final TagSetDto respdto = (TagSetDto) new TagSetDto().addResourcesItem(dto).size(1L)
                .filter(filters).datatype("tags");
        return Response.ok().entity(respdto).build();
    }

    /*
     * (non-Javadoc)
     *
     * @see hep.crest.server.swagger.api.TagsApiService#listTags(java.lang.String,
     * java.lang.Integer, java.lang.Integer, java.lang.String,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response listTags(String by, Integer page, Integer size, String sort,
                             SecurityContext securityContext, UriInfo info) {
        log.debug("Search resource list using by={}, page={}, size={}, sort={}", by, page, size,
                sort);
        // Create filters
        GenericMap filters = prh.getFilters(prh.createMatcherCriteria(by));
        // Create pagination request.
        final PageRequest preq = prh.createPageRequest(page, size, sort);
        BooleanExpression wherepred = null;
        if (!"none".equals(by)) {
            // Create search conditions for where statement in SQL
            wherepred = prh.buildWhere(filtering, by);
        }
        // Retrieve tag list using filtering.
        Page<Tag> entitypage = tagService.findAllTags(wherepred, preq);
        RespPage respPage = new RespPage().size(entitypage.getSize())
                .totalElements(entitypage.getTotalElements()).totalPages(entitypage.getTotalPages())
                .number(entitypage.getNumber());
        List<TagDto> dtolist = edh.entityToDtoList(entitypage.toList(), TagDto.class);
        // Create the Set.
        final CrestBaseResponse setdto = new TagSetDto().resources(dtolist)
                .page(respPage)
                .size((long) dtolist.size()).datatype("tags");
        if (filters != null) {
            setdto.filter(filters);
        }
        // Response is 200.
        return Response.ok().entity(setdto).build();
    }

    /* (non-Javadoc)
     * @see hep.crest.server.swagger.api.TagsApiService#createTagMeta(java.lang.String, hep.crest.swagger.model
     * .TagMetaDto, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createTagMeta(String name, TagMetaDto body, SecurityContext securityContext, UriInfo info) {
        log.info("TagRestController processing request for creating a tag meta data entry for {}", name);
        final Tag tag = tagService.findOne(name);
        log.debug("Add meta information to tag {}", name);
        final TagMetaDto saved = tagMetaService.insertTagMeta(body);
        return Response.created(info.getRequestUri()).entity(saved).build();
    }

    /* (non-Javadoc)
     * @see hep.crest.server.swagger.api.TagsApiService#findTagMeta(java.lang.String, javax.ws.rs.core
     * .SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response findTagMeta(String name, SecurityContext securityContext, UriInfo info) throws NotFoundException {
        this.log.info("TagRestController processing request to find tag metadata for name " + name);
        final TagMetaDto dto = tagMetaService.findMeta(name);
        final TagMetaSetDto respdto = (TagMetaSetDto) new TagMetaSetDto().addResourcesItem(dto).size(1L)
                .datatype("tagmetas");
        return Response.ok().entity(respdto).build();
    }


    /* (non-Javadoc)
     * @see hep.crest.server.swagger.api.TagsApiService#updateTagMeta(java.lang.String, hep.crest.swagger.model
     * .GenericMap, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response updateTagMeta(String name, Map<String, String> body, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        log.info("TagRestController processing request for updating a tag meta information");
        final TagMetaDto dto = tagMetaService.findMeta(name);
        if (dto == null) {
            log.debug("Cannot update meta data on null tag meta entity for {}", name);
            final String message = "TagMeta " + name + " not found...";
            return rfh.notFoundPojo("updateTagMeta error: cannot find meta for tag " + name);
        }
        for (final String key : body.keySet()) {
            if (key == "description") {
                dto.setDescription(body.get(key));
            }
            if (key == "chansize") {
                dto.setChansize(new Integer(body.get(key)));
            }
            if (key == "colsize") {
                dto.setColsize(new Integer(body.get(key)));
            }
            if (key == "tagInfo") {
                // The field is a string ... this is mandatory for the moment....
                dto.setTagInfo(body.get(key));
            }
        }
        final TagMetaDto saved = tagMetaService.updateTagMeta(dto);
        return Response.ok(info.getRequestUri()).entity(saved).build();
    }
}
