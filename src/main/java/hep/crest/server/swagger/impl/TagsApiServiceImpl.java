package hep.crest.server.swagger.impl;

import hep.crest.server.annotations.ProfileAndLog;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.pojo.TagMeta;
import hep.crest.server.data.repositories.args.TagQueryArgs;
import hep.crest.server.exceptions.CdbBadRequestException;
import hep.crest.server.services.TagMetaService;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.server.swagger.api.TagsApiService;
import hep.crest.server.swagger.model.CrestBaseResponse;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.RespPage;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagMetaDto;
import hep.crest.server.swagger.model.TagMetaSetDto;
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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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
@Slf4j
public class TagsApiServiceImpl extends TagsApiService {
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
    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.TagsApiService#createTag(hep.crest.swagger.model
     * .TagDto, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createTag(TagDto body, SecurityContext securityContext, UriInfo info) {
        log.info("Creating a new tag {}", body.getName());
        // Create a tag.
        Tag entity = mapper.map(body, Tag.class);
        final Tag saved = tagService.insertTag(entity);
        TagDto dto = mapper.map(saved, TagDto.class);
        // Response is 201.
        log.info("Created tag {}", dto);
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
        log.info("Updating tag {}", name);
        // Search tag.
        final Tag entity = tagService.findOne(name);
        // Send a bad request if body is null.
        if (body == null) {
            throw new CdbBadRequestException("Cannot update tag with null body");
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
                final BigInteger val = new BigInteger(body.get(key));
                entity.lastValidatedTime(val);
            }
            else if (key == "endOfValidity") {
                final BigInteger val = new BigInteger(body.get(key));
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
        log.info("Updated tag {}", dto);
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
        log.debug("Get tag {} ", name);
        final GenericMap filters = new GenericMap();
        filters.put("name", name);
        final Tag entity = tagService.findOne(name);
        TagDto dto = mapper.map(entity, TagDto.class);
        // Create the set.
        final TagSetDto respdto = (TagSetDto) new TagSetDto().addResourcesItem(dto).size(1L)
                .filter(filters).datatype("tags").format("TagSetDto");
        log.info("Retrieved tag {}: {}", name, dto);
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
    @ProfileAndLog
    public Response listTags(String name, String timeType, String objectType, String description, Integer page,
                             Integer size, String sort,
                             SecurityContext securityContext, UriInfo info) {
        log.info("Search tag list using name={}, timeType={}, objectType={}, descrition={}, "
                 + "page={}, "
                 + "size={}, "
                 + "sort={}", name, timeType, objectType, description, page, size, sort);
        if (name.equalsIgnoreCase("all")) {
            name = "%";
        }
        // Create query params object
        TagQueryArgs args = new TagQueryArgs();
        args.name(name).description(description).objectType(objectType).timeType(timeType);
        // Create pagination request
        final PageRequest preq = prh.createPageRequest(page, size, sort);
        // Launch query
        // Search for global tags using where conditions.
        final Page<Tag> entitypage = tagService.selectTagList(args, preq);
        RespPage respPage = new RespPage().size(entitypage.getSize())
                .totalElements(entitypage.getTotalElements()).totalPages(entitypage.getTotalPages())
                .number(entitypage.getNumber());
        List<TagDto> dtolist = edh.entityToDtoList(entitypage.toList(), TagDto.class);
        // Create the Set.
        final CrestBaseResponse setdto = new TagSetDto().resources(dtolist)
                .page(respPage)
                .size((long) dtolist.size())
                .datatype("tags")
                .format("TagSetDto");
        // Create filters
        GenericMap filters = new GenericMap();
        if (name != null) {
            filters.put("name", name);
        }
        if (objectType != null) {
            filters.put("objectType", objectType);
        }
        if (description != null) {
            filters.put("description", description);
        }
        if (timeType != null) {
            filters.put("timeType", timeType);
        }
        setdto.filter(filters);
        // Response is 200.
        log.info("Retrieved tag list from filters {} size={} total={}",
                filters, setdto.getSize(), respPage.getTotalElements());
        return Response.ok().entity(setdto).build();
    }

    /* (non-Javadoc)
     * @see hep.crest.server.swagger.api.TagsApiService#createTagMeta(java.lang.String, hep.crest.swagger.model
     * .TagMetaDto, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createTagMeta(String name, TagMetaDto body, SecurityContext securityContext, UriInfo info) {
        log.debug("TagRestController processing request for creating a tag meta data entry for {}",
                name);
        final Tag tag = tagService.findOne(name);
        log.debug("Add meta information to tag {}", name);
        TagMeta entity = mapper.map(body, TagMeta.class);

        final TagMeta savedEntity = tagMetaService.insertTagMeta(entity);
        TagMetaDto saved = mapper.map(savedEntity, TagMetaDto.class);
        log.info("Created tag meta data {}", saved);
        return Response.created(info.getRequestUri()).entity(saved).build();
    }

    /* (non-Javadoc)
     * @see hep.crest.server.swagger.api.TagsApiService#findTagMeta(java.lang.String, javax.ws.rs.core
     * .SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    @ProfileAndLog
    public Response findTagMeta(String name, SecurityContext securityContext, UriInfo info) throws NotFoundException {
        log.info("Search tag metadata for name " + name);
        final TagMeta entity = tagMetaService.find(name);
        final TagMetaDto dto = mapper.map(entity, TagMetaDto.class);
        log.info("Retrieved tag meta data {}: {}", name, dto);
        return Response.ok().entity(dto).build();
    }


    /* (non-Javadoc)
     * @see hep.crest.server.swagger.api.TagsApiService#updateTagMeta(java.lang.String, hep.crest.swagger.model
     * .GenericMap, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response updateTagMeta(String name, Map<String, String> body, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        log.info("TagRestController processing request for updating a tag meta information for "
                 + "name {}", name);
        TagMeta entity = tagMetaService.find(name);
        for (final String key : body.keySet()) {
            if (key == "description") {
                entity.description(body.get(key));
            }
            if (key == "chansize") {
                entity.chansize(Integer.valueOf(body.get(key)));
            }
            if (key == "colsize") {
                entity.colsize(Integer.valueOf(body.get(key)));
            }
            if (key == "tagInfo") {
                // The field is a string ... this is mandatory for the moment....
                entity.tagInfo(body.get(key).getBytes(StandardCharsets.UTF_8));
            }
        }
        final TagMeta saved = tagMetaService.updateTagMeta(entity);
        final TagMetaDto dto = mapper.map(saved, TagMetaDto.class);
        log.info("Updated tag meta data {}", dto);
        return Response.ok(info.getRequestUri()).entity(dto).build();
    }
}
