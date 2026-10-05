package hep.crest.server.timeseries;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.args.IovQueryArgs;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import hep.crest.server.timeseries.model.TimeSerieIov;
import hep.crest.server.timeseries.model.repositories.TimeSerieIovQueryArgs;
import hep.crest.server.timeseries.services.TimeSerieIovService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


/**
 * Aspect to be used for NATS messaging triggered on new insertions.
 *
 * @author formica
 * @version %I%, %G%
 */
@Aspect
@Component
@Slf4j
public class TimeSerieAspect {

    /**
     * Service.
     */
    @Autowired
    private TimeSerieIovService timeSerieIovService;
    /**
     * Service.
     */
    @Autowired
    private TagService tagService;
    /**
     * PageRequestHelper.
     */
    @Autowired
    private PageRequestHelper prh;

    /**
     * ObjectMapper.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Store as TimeSerieIov.
     *
     * @param pjp
     * @param dto
     * @param objectType
     * @param version
     * @param compressionType
     * @param tag
     * @return Object
     */
    @Around("execution(* hep.crest.server.services.PayloadService.savePayloadIov("
            + "hep.crest.server.swagger.model.StoreDto, String, String, String, "
            + "hep.crest.server.data.pojo.Tag)) "
            + "&& args(dto, objectType, version, compressionType, tag)")
    public Object storeTimeSerie(ProceedingJoinPoint pjp, StoreDto dto, String objectType,
                                 String version, String compressionType, Tag tag)
            throws Throwable {
        log.info("Time serie insertion for : {} - {} {}", dto, version, compressionType);
        Object retVal = null;
        // If there is no or weak security activated then return.
        if (!"crest-json-timeserie".equals(objectType)) {
            log.warn("Normal payload insertion....");
            retVal = pjp.proceed();
        }
        else {
            log.warn("TimeSerie payload insertion....{} {}", objectType, tag.getName());
            // tag is already a Tag object, no need to find it
            Integer nstored = timeSerieIovService.saveDto(dto, tag);
            dto.data("{\"stored\": " + nstored + "}");
            retVal = dto;
        }
        return retVal;
    }


    /**
     * Remove tag.
     *
     * @param pjp
     * @param name
     * @throws Throwable
     */
    @Around("execution(* hep.crest.server.services.TagService.removeTag(String)) "
            + "&& args(name)")
    public void removeTagTimeSerie(ProceedingJoinPoint pjp, String name)
            throws Throwable {
        log.info("Time serie tag removal for : {}", name);
        Tag tag = tagService.findOne(name);
        // If there is no or weak security activated then return.
        if (!"crest-json-timeserie".equals(tag.getObjectType())) {
            log.warn("Normal tag removal....");
            pjp.proceed();
        }
        else {
            log.warn("TimeSerie tag removal....{}", tag);
            Integer nremoved = timeSerieIovService.deleteIovForTag(name);
            log.info("TimeSerie tag removed....{} for tag {}", nremoved, name);
            pjp.proceed();
        }
    }

    /**
     * Remove tag.
     *
     * @param pjp
     * @param args the IovQueryArgs
     * @param preq the Pageable
     * @return Page of Iov
     * @throws Throwable
     */
    @Around("execution(* hep.crest.server.services.IovService.selectIovList("
            + " hep.crest.server.data.repositories.args.IovQueryArgs, "
            + " org.springframework.data.domain.Pageable)) "
            + "&& args(args, preq)")
    public Object findTimeSerie(ProceedingJoinPoint pjp, IovQueryArgs args, Pageable preq)
            throws Throwable {
        log.info("Time serie find Iovs : {}", args);
        String tagname = args.tagName();
        if (tagname == null) {
            log.error("TimeSerieAspect.findTimeSerie: empty tag name");
            throw new IllegalArgumentException("Empty tag name");
        }
        Tag tagEntity = tagService.findOne(tagname);
        if (!tagEntity.getObjectType().equalsIgnoreCase("crest-json-timeserie")) {
            log.warn("Normal iov retrieval....");
            return pjp.proceed();
        }
        if (preq == null) {
            String sort = "id.since:ASC,insertionTime:DESC";
            preq = prh.createPageRequest(0, 1000, sort);
        }
        TimeSerieIovQueryArgs qryArgs = new TimeSerieIovQueryArgs();
        qryArgs.tagName(args.tagName());
        qryArgs.since(args.since());
        qryArgs.until(args.until());
        Page<TimeSerieIov> tsIovPage = timeSerieIovService.findIovList(qryArgs, preq);
        // Transform the page to a list of Iov
        return tsIovPage.map(iov -> {
            Iov iovObj = new Iov();
            IovId iovId = new IovId();
            iovId.setTagId(tagEntity.getTagId());
            iovId.setSince(iov.getId().getSince());
            iovId.setInsertionTime(iov.getId().getInsertionTime());
            iovObj.setId(iovId);
            // Create json string with until and channelId plus the payload.
            // Create JSON object using ObjectMapper.
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("until", iov.getUntil());
            jsonNode.put("channelId", iov.getId().getChannelId());
            jsonNode.put("payload", iov.getJsonPayload());

            iovObj.setPayloadHash(jsonNode.toString());

            return iovObj;
        });
    }

    /**
     * Intercept storeData to handle TimeSerieData.
     * This intercepts at the public method level before the payload is written to disk.
     *
     * @param pjp the ProceedingJoinPoint
     * @return StoreSetDto
     * @throws Throwable if an error occurs
     */
    @Around("execution(* hep.crest.server.services.PayloadService.storeData(..)) ")
    public Object storeDataAspect(ProceedingJoinPoint pjp) throws Throwable {

        log.info("=== ASPECT CALLED ===");
        log.info("ASPECT: storeData called");
        log.info("ASPECT: Method being intercepted: {}", pjp.getSignature());
        log.info("ASPECT: Target class: {}", pjp.getTarget().getClass().getName());
        log.info("ASPECT: Number of arguments: {}", pjp.getArgs().length);
        log.info("=== END ASPECT HEADER ===");

        // Extract parameters from the ProceedingJoinPoint
        Object[] args = pjp.getArgs();
        if (args.length < 6) {
            log.warn("ASPECT: Expected at least 6 arguments, got {}", args.length);
            return pjp.proceed();
        }

        StoreSetDto dtoset = (StoreSetDto) args[0];
        String tag = (String) args[1];
        String objectType = (String) args[2];
        String compressionType = (String) args[3];
        String version = (String) args[4];
        java.util.List filesbodyparts = (java.util.List) args[5];

        log.info("ASPECT: storeData called for tag: {} with objectType: {}", tag, objectType);
        log.info("ASPECT: dtoset size: {}", dtoset != null ? dtoset.getResources().size() : "null");

        // Check if this is a TimeSerieData tag
        if (objectType != null && "crest-json-timeserie".equals(objectType)) {
            log.warn("ASPECT: TimeSerieData insertion detected for tag: {}", tag);

            try {
                // Get the tag entity to verify it's TimeSerieData
                Tag tagEntity = tagService.findOne(tag);
                if ("crest-json-timeserie".equals(tagEntity.getObjectType())) {
                    log.warn("ASPECT: Confirmed TimeSerieData tag, intercepting insertion");

                    // Process each payload directly
                    int nstored = 0;
                    for (StoreDto piovDto : dtoset.getResources()) {
                        log.info("ASPECT: Processing TimeSerieData for tag: {} at since: {}",
                                tag, piovDto.getSince());

                        // Call timeSerieIovService.saveDto with the StoreDto
                        // The StoreDto.data contains the TimeSerieData JSON
                        Integer stored = timeSerieIovService.saveDto(piovDto, tagEntity);
                        if (stored != null) {
                            nstored += stored;
                        }
                    }

                    log.info("ASPECT: TimeSerieIovService stored {} items for tag {}", nstored,
                            tag);

                    // Return a StoreSetDto with the results
                    StoreSetDto responseDto = new StoreSetDto();
                    responseDto.setSize((long) nstored);
                    responseDto.setDatatype("iovs");
                    responseDto.setFormat("StoreSetDto");

                    return responseDto;
                }
            }
            catch (Exception e) {
                log.error("ASPECT: Error processing TimeSerieData, falling back to normal flow", e);
                // Fall through to normal processing
            }
        }

        log.info("ASPECT: Normal payload storage, proceeding");
        return pjp.proceed();
    }

}
