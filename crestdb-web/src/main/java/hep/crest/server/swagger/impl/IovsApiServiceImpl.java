package hep.crest.server.swagger.impl;

import hep.crest.data.exceptions.CdbBadRequestException;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.args.IovModeEnum;
import hep.crest.data.repositories.args.IovQueryArgs;
import hep.crest.server.caching.CachingPolicyService;
import hep.crest.server.caching.CachingProperties;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.serializers.ArgTimeUnit;
import hep.crest.server.services.IovService;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.api.ApiResponseMessage;
import hep.crest.server.swagger.api.IovsApiService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.server.swagger.model.CrestBaseResponse;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovPayloadDto;
import hep.crest.server.swagger.model.IovPayloadSetDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.RespPage;
import hep.crest.server.swagger.model.TagSummaryDto;
import hep.crest.server.swagger.model.TagSummarySetDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Rest endpoint for iov management. It allows to create and find iovs.
 *
 * @author formica
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen",
        date = "2017-09-05T16:23:23.401+02:00")
@Component
@Slf4j
public class IovsApiServiceImpl extends IovsApiService {
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;
    /**
     * Helper.
     */
    @Autowired
    EntityDtoHelper edh;

    /**
     * Service.
     */
    @Autowired
    private CachingPolicyService cachesvc;

    /**
     * Service.
     */
    @Autowired
    private IovService iovService;

    /**
     * Service.
     */
    @Autowired
    private TagService tagService;

    /**
     * Properties.
     */
    @Autowired
    private CachingProperties cprops;
    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    /**
     * The context from the request.
     */
    @Autowired
    private JAXRSContext context;

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.IovsApiService#createIov(hep.crest.swagger.model
     * .IovDto, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response storeIovOne(IovDto body, SecurityContext securityContext, UriInfo info) {
        log.info("IovRestController processing request for creating an iov");
        // Create a new IOV.
        String tagname = body.getTagName();
        Iov entity = mapper.map(body, Iov.class);
        entity.tag(new Tag().name(tagname));
        final Iov saved = iovService.insertIov(entity);
        // Change the modification time in the tag.
        Tag tagEntity = tagService.findOne(tagname);
        tagEntity.modificationTime(new Date(Instant.now().toEpochMilli()));
        tagService.updateTag(tagEntity);

        IovDto dto = mapper.map(saved, IovDto.class);
        dto.tagName(tagname);
        List<IovDto> dtoList = new ArrayList<>();
        dtoList.add(dto);
        CrestBaseResponse resp = buildEntityResponse(dtoList, new GenericMap());
        return Response.created(info.getRequestUri()).entity(resp).build();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.IovsApiService#storeBatchIovMultiForm(hep.crest.
     * swagger.model.IovSetDto, javax.ws.rs.core.SecurityContext,
     * javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response storeIovBatch(IovSetDto dto, SecurityContext securityContext,
                                           UriInfo info) {
        log.info("IovRestController processing request to upload iovs batch {}", dto);
        // Get filters map and initializa tag name.
        final GenericMap filters = dto.getFilter();
        String tagName = "unknown";

        // Check the filter map. If it exists it should contain the tag name.
        if (filters != null && filters.containsKey("tagName")) {
            // the tag name is in the filter map.
            tagName = filters.get("tagName");
        }
        // If no tag name was found in the filter we search for it in the resources list.
        // This check is performed below.
        // Now start going through uploaded iovs.
        log.info("Batch insertion of {} iovs using file for tag {}",
                dto.getSize(), tagName);
        // Prepare the iov list to insert and a list representing iovs really inserted.
        final List<IovDto> iovlist = dto.getResources();
        final List<IovDto> savedList = new ArrayList<>();
        if (iovlist == null) {
            throw new BadRequestException("Cannot store null list of iovs");
        }
        // Loop over resources uploaded.
        for (final IovDto iovDto : iovlist) {
            log.debug("Create iov from dto {}", iovDto);
            // Verify if tagname should be taken inside the iovdto.
            if (!"unknown".equals(tagName)
                && (iovDto.getTagName() == null || !iovDto.getTagName().equals(tagName))) {
                iovDto.setTagName(tagName);
            }
            else if (iovDto.getTagName() == null) {
                // Tag name is not available, send a response 406.
                final String msg = "Error creating multi iov resource because tagName is not defined ";
                final ApiResponseMessage resp = new ApiResponseMessage(ApiResponseMessage.ERROR,
                        msg);
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resp).build();
            }
            log.debug("Iov tag is {}", iovDto.getTagName());
            tagName = iovDto.getTagName();
            // Create new iov.
            Iov entity = mapper.map(iovDto, Iov.class);
            entity.tag(new Tag().name(iovDto.getTagName()));
            final Iov saved = iovService.insertIov(entity);
            IovDto saveddto = mapper.map(saved, IovDto.class);
            saveddto.tagName(iovDto.getTagName());
            // Add to saved list.
            savedList.add(saveddto);
        }
        // Change the modification time in the tag.
        Tag tagEntity = tagService.findOne(tagName);
        tagEntity.modificationTime(new Date(Instant.now().toEpochMilli()));
        tagService.updateTag(tagEntity);

        // Prepare the Set for the response.
        final CrestBaseResponse saveddto = buildEntityResponse(savedList, filters);
        // Send 201.
        return Response.created(info.getRequestUri()).entity(saveddto).build();
    }

    @Override
    public Response findAllIovs(String method, String tagname, Long snapshot, String since, String until,
                                String timeformat,
                                Long groupsize,
                                String hash,
                                Integer page, Integer size, String sort,
                                String xCrestQuery, String xCrestSince, SecurityContext securityContext,
                                UriInfo info) {
        log.debug("Search resource list using method={}, tag={}, timeformat={}, page={}, size={}, sort={}", method,
                tagname,
                timeformat,
                page,
                size, sort);
        // Date format. Default is milliseconds.
        if (timeformat == null) {
            timeformat = "MS";
        }
        if (xCrestSince == null) {
            xCrestSince = "NUMBER";
        }
        if (xCrestQuery == null) {
            xCrestQuery = "IOVS";
        }
        log.debug("Use input time format: {}", timeformat);
        log.debug("Use iov query mode: {}", xCrestQuery);
        ArgTimeUnit inputformat = ArgTimeUnit.valueOf(timeformat);
        ArgTimeUnit outformat = ArgTimeUnit.valueOf(xCrestSince);
        IovModeEnum queryMode = IovModeEnum.valueOf(xCrestQuery);

        // The following is valid for method: iovs or groups.
        // Search if tag exists: if it is not modified send back the NOT-MODIFIED.
        // Else it will continue to perform the query.
        if (IovModeEnum.IOVS.mode().equalsIgnoreCase(method) || IovModeEnum.GROUPS.mode().equalsIgnoreCase(method)) {
            if (tagname == null || tagname.contains("%")) {
                throw new CdbBadRequestException("Cannot search iovs with tag " + tagname);
            }
            final Tag tagentity = tagService.findOne(tagname);
            log.debug("Found tag " + tagentity);
            HttpHeaders headers = context.getHttpHeaders();
            Request request = context.getRequest();
            // Apply caching on iov selections.
            // Use cache service to detect if a tag was modified.
            final ResponseBuilder builder = cachesvc.verifyLastModified(request, tagentity);
            if (builder != null) {
                // Get request headers: this is just to dump the If-Modified-Since
                final String ifmodsince = headers.getHeaderString("If-Modified-Since");
                log.debug("The output data are not modified since " + ifmodsince);
                // Send back the response via the builder.
                return builder.build();
            }
        }
        // If it is a group method query, immediately call the method
        if (IovModeEnum.GROUPS.mode().equalsIgnoreCase(method)) {
            return this.selectGroups(tagname, snapshot, groupsize);
        }

        // From now on, it is an iovs or monitor or attime query.
        IovQueryArgs args = new IovQueryArgs();
        Timestamp snap = null;
        if (snapshot != null && snapshot > 0) {
            Instant inst = Instant.ofEpochMilli(snapshot);
            snap = Timestamp.from(inst);
            log.debug("Use snapshot {}", snap);
        }
        // It is an IOV query, then tagname should be provided without regexp.
        if (IovModeEnum.IOVS.mode().equalsIgnoreCase(method) ||
            IovModeEnum.AT.mode().equalsIgnoreCase(method)) {
            if (tagname == null || tagname.contains("%")) {
                throw new CdbBadRequestException("IOVS or AT query need a full tagname");
            }
        }
        BigInteger rsince = prh.getTimeFromArg(since, inputformat, outformat, null);
        BigInteger runtil = prh.getTimeFromArg(until, inputformat, outformat, null);
        // Set arguments for query.
        if (queryMode.equals(IovModeEnum.AT)) {
            runtil = null;
        }
        args.mode(queryMode).hash(hash).tagName(tagname).snapshot(snap)
                .since(rsince).until(runtil);
        if (args.checkArgsNull(method)) {
            throw new CdbBadRequestException("Arguments not compatible with method " + method);
        }
        // Create filters
        GenericMap filters = new GenericMap();
        filters.put("tagName", tagname);
        filters.put("since", rsince.toString());
        filters.put("until", runtil.toString());
        filters.put("timeformat", timeformat);
        filters.put("method", method);
        filters.put("mode", xCrestQuery);
        // Create pagination request
        final PageRequest preq = prh.createPageRequest(page, size, sort);

        // Search for global tags using where conditions.
        Page<Iov> entitypage = iovService.selectIovList(args, preq);
        RespPage respPage = new RespPage().size(entitypage.getSize())
                .totalElements(entitypage.getTotalElements()).totalPages(entitypage.getTotalPages())
                .number(entitypage.getNumber());

        final List<IovDto> dtolist = edh.entityToDtoList(entitypage.toList(), IovDto.class);
        Response.Status rstatus = Response.Status.OK;
        // Prepare the Set.
        final CrestBaseResponse saveddto = buildEntityResponse(dtolist, filters);
        saveddto.page(respPage);
        // Send a response and status 200.
        return Response.status(rstatus).entity(saveddto).build();
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.IovsApiService#getSizeByTag(java.lang.String,
     * javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response getSizeByTag(@NotNull String tagname, SecurityContext securityContext,
                                 UriInfo info) {
        // Get the tag summary list corresponding to the tagname pattern.
        // The method in the service sends back always a list, eventually empty.
        final List<TagSummaryDto> entitylist = iovService.getTagSummaryInfo(tagname);
        final TagSummarySetDto respdto = new TagSummarySetDto();
        final GenericMap filters = new GenericMap();
        filters.put("tagName", tagname);
        // Prepare the Set.
        ((TagSummarySetDto) respdto.size((long) entitylist.size()).datatype("count")
                .filter(filters)).resources(entitylist);
        // Send a response 200. Even if the result is an empty list.
        return Response.ok().entity(respdto).build();
    }

    /**
     * Custom query to return groups.
     *
     * @param tagname
     * @param snapshot
     * @return
     */
    protected Response selectGroups(String tagname, Long snapshot, Long groupsize) {
        final Tag tagentity = tagService.findOne(tagname);
        HttpHeaders headers = context.getHttpHeaders();
        Request request = context.getRequest();
        // Apply caching on iov groups selections.
        // Use cache service to detect if a tag was modified.
        final ResponseBuilder builder = cachesvc.verifyLastModified(request, tagentity);
        if (builder != null) {
            // Get request headers: this is just to dump the If-Modified-Since
            final String ifmodsince = headers.getHeaderString("If-Modified-Since");
            log.debug("The output data are not modified since " + ifmodsince);
            return builder.build();
        }
        // Get the time type to apply different group selections.
        // This are typical values representative for COOL types (NANO_SEC).
        // The groupsize can be provided in input.
        final String timetype = tagentity.timeType();
        if (groupsize == null) {
            if (timetype.equalsIgnoreCase("run")) {
                // The iov is of type RUN. Use the group size from properties.
                groupsize = new Long(cprops.getRuntypeGroupsize());
            }
            else if (timetype.equalsIgnoreCase("run-lumi")) {
                // The iov is of type RUN-LUMI. Use the group size from properties.
                groupsize = new Long(cprops.getRuntypeGroupsize());
                // transform to COOL run-lumi
                groupsize = groupsize * 4294967296L;
            }
            else {
                // Assume COOL time format...
                groupsize = new Long(cprops.getTimetypeGroupsize());
                // transform to COOL nanosec
                groupsize = groupsize * 1000000000L;
            }
        }
        // Set caching policy depending on snapshot argument
        // this is filling a max-age parameter in the header
        final CacheControl cc = cachesvc.getGroupsCacheControl(snapshot);
        // Retrieve all iovs groups
        Date snap = null;
        if (snapshot != 0L) {
            // Set the snapshot.
            snap = new Date(snapshot);
        }
        // Get the iov groups from the DB, use eventually snapshot.
        final CrestBaseResponse respdto = iovService
                .selectGroupDtoByTagNameAndSnapshotTime(tagname, snap, groupsize);
        final GenericMap filters = new GenericMap();
        filters.put("tagName", tagname);
        filters.put("snapshot", snapshot.toString());
        filters.put("groupsize", groupsize.toString());
        respdto.datatype("groups").filter(filters);
        // In the response set the cachecontrol flag as well.
        return Response.ok().entity(respdto).cacheControl(cc).lastModified(tagentity.modificationTime()).build();
    }

    @Override
    public Response selectIovPayloads(@NotNull String tagname, String since, String until, String timeformat,
                                      Integer page, Integer size, String sort, SecurityContext securityContext,
                                      UriInfo info)
            throws NotFoundException {

        log.info(
                "IovRestController processing request for iovs and payloads meta using tag name {} and range {} - {} ",
                tagname, since, until);
        List<IovPayloadDto> dtolist = null;
        if (timeformat == null) {
            timeformat = "MS";
        }
        log.debug("Use input time format: {}", timeformat.toUpperCase());
        ArgTimeUnit inputformat = ArgTimeUnit.valueOf(timeformat.toUpperCase());
        ArgTimeUnit outformat = ArgTimeUnit.valueOf("COOL");

        // Retrieve all iovs
        final Tag tagentity = tagService.findOne(tagname);
        log.debug("Found tag " + tagentity);
        BigInteger rsince = prh.getTimeFromArg(since, inputformat, outformat, null);
        BigInteger runtil = prh.getTimeFromArg(until, inputformat, outformat, null);
        log.debug("Setting iov range to : {}, {}", since, until);
        Date snap = new Date();
        log.debug("Use snapshot {}", snap);
        // Get the IOV list.
        dtolist = iovService.selectIovPayloadsByTagRangeSnapshot(tagname, rsince, runtil, snap);
        final IovPayloadSetDto respdto = new IovPayloadSetDto();
        // Create the Set for the response.
        ((IovPayloadSetDto) respdto.datatype("iovpayloads")).resources(dtolist)
                .size((long) dtolist.size());
        final GenericMap filters = new GenericMap();
        filters.put("tagName", tagname);
        filters.put("since", rsince.toString());
        filters.put("until", runtil.toString());
        respdto.filter(filters);
        return Response.ok().entity(respdto).build();
    }

    /**
     * Factorise code to build the IovSetDto.
     *
     * @param dtolist the List<IovDto>
     * @param filters the GenericMap
     * @return IovSetDto
     */
    protected IovSetDto buildEntityResponse(List<IovDto> dtolist, GenericMap filters) {
        final IovSetDto respdto = new IovSetDto();
        // Create the Set for the response.
        ((IovSetDto) respdto.datatype("iovs")).resources(dtolist)
                .size((long) dtolist.size());
        respdto.filter(filters);
        respdto.format("IovSetDto");
        return respdto;
    }

}
