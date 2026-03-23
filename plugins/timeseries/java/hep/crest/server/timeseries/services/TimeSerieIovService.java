package hep.crest.server.timeseries.services;

import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.pojo.TagMeta;
import hep.crest.server.data.repositories.TagMetaRepository;
import hep.crest.server.data.repositories.TagRepository;
import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbBadRequestException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.exceptions.ConflictException;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.timeseries.model.TimeSerieData;
import hep.crest.server.timeseries.model.TimeSerieIov;
import hep.crest.server.timeseries.model.TimeSerieIovId;
import hep.crest.server.timeseries.model.repositories.TimeSerieIovQueryArgs;
import hep.crest.server.timeseries.model.repositories.TimeSerieIovRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * TimeSerieIovService.
 * Deals with TimeSerieIov. It is a service class that provides methods to
 * retrieve and store TimeSerieIov objects.
 */
@Slf4j
@Service
public class TimeSerieIovService {

    /**
     * NodeRepository.
     */
    private TagRepository tagRepository;
    /**
     * ChannelRepository.
     */
    private TagMetaRepository tagMetaRepository;
    /**
     * IovRepository.
     */
    private TimeSerieIovRepository iovRepository;
    /**
     * TagInfo serializer.
     */
    private TagInfoSerializer tagInfoSerializer;
    /**
     * TimeSerieData serializer.
     */
    private TimeSerieDataSerializer timeSerieDataSerializer;

    /**
     * Constructor.
     *
     * @param tagRepository
     * @param tagMetaRepository
     * @param iovRepository
     * @param tagInfoSerializer
     * @param timeSerieDataSerializer
     */
    TimeSerieIovService(@Autowired TagRepository tagRepository,
                        @Autowired TagMetaRepository tagMetaRepository,
                        @Autowired TimeSerieIovRepository iovRepository,
                        @Autowired TagInfoSerializer tagInfoSerializer,
                        @Autowired TimeSerieDataSerializer timeSerieDataSerializer) {
        log.info("TimeSerieIovService created");
        this.tagRepository = tagRepository;
        this.tagMetaRepository = tagMetaRepository;
        this.iovRepository = iovRepository;
        this.tagInfoSerializer = tagInfoSerializer;
        this.timeSerieDataSerializer = timeSerieDataSerializer;
    }

    /**
     * The request to select iov List.
     *
     * @param qryArgs
     * @param req
     * @return Page of TimeSerieIov
     */
    public Page<TimeSerieIov> findIovList(TimeSerieIovQueryArgs qryArgs, Pageable req)
            throws AbstractCdbServiceException {
        Page<TimeSerieIov> entitylist = null;
        Tag tag = null;
        TagMeta tagMeta = null;
        List<Map<String, String>> channelList = null;
        if (req == null) {
            req = PageRequest.of(0, 1000);
        }
        // Get the Tag and TagMeta information
        if (qryArgs.tagName() != null) {
            if (qryArgs.tagName().contains("%")) {
                qryArgs.tagName(qryArgs.tagName().replaceAll("%", ""));
            }
            log.debug("Add where condition on tag: {}", qryArgs.tagName());
            tag = tagRepository.findByName(qryArgs.tagName())
                    .orElseThrow(() -> new CdbNotFoundException("Tag not found"));
            tagMeta = tagMetaRepository.findById(tag.getName())
                    .orElseThrow(() -> new CdbNotFoundException("TagMeta not found"));
            if (tag.getObjectType().equalsIgnoreCase("crest-json-timeserie")) {
                // Get the tag_info field in tagMeta and load channel_list
                // The tagInfo field is a byte array, so we need to convert it to a string.
                // The convention for ATLAS is that the tagInfo field is a JSON string.
                // We need to parse the JSON string to get the channel list.
                byte[] tagInfoBytes = tagMeta.getTagInfo();
                String tagInfoString = new String(tagInfoBytes);
                log.debug("TagInfo: {}", tagInfoString);
                // Parse the JSON string to get the channel list.
                channelList = tagInfoSerializer.extractChannelList(tagInfoString);
            }
        }
        else {
            log.error("TimeSerieIovService.findIovList: empty tag name");
            throw new CdbBadRequestException("Empty tag name");
        }
        // Check if channel selection is requested
        if (qryArgs.channelId() != null) {
            log.debug("Add where condition on channel: {}", qryArgs.channelId());
            // Check if the channel is in the channel list
            boolean found = false;
            for (Map<String, String> channel : channelList) {
                // The key in the map is the channel id
                found = channel.containsKey(String.valueOf(qryArgs.channelId()));
                break;
            }
            if (!found) {
                log.error("TimeSerieIovService.findIovList: channel {} not found in tag {}",
                        qryArgs.channelId(), qryArgs.tagName());
                throw new CdbBadRequestException("Channel not found in tag");
            }
        }
        entitylist = iovRepository.findIovList(qryArgs, req);
        log.debug("Retrieved list of iovs {}", entitylist);
        return entitylist;
    }

    /**
     * Save a set of resources.
     *
     * @param dto
     * @param tagEntity
     * @return Number of saved resources
     */
    @Transactional
    public Integer saveDto(StoreDto dto, Tag tagEntity) {
        log.info("TimeSerieIovService.save called");
        Integer nsaved = 0;
        if (dto == null) {
            log.error("TimeSerieIovService.save: null dto");
            throw new CdbNotFoundException("Empty list of resources");
        }
        String payloadData = dto.getData();
        BigInteger since = BigInteger.valueOf(dto.getSince());
        TimeSerieData timeSerieData = timeSerieDataSerializer.extractTimeSerieData(payloadData);
        TimeSerieIov entity = new TimeSerieIov();
        TimeSerieIovId id = new TimeSerieIovId();
        id.setSince(since);
        id.setChannelId(timeSerieData.getChannelId());
        id.setTagId(tagEntity.getTagId());
        log.info("Inserting TimeSerieIov with tagId={}, channelId={}, since={}", 
                 tagEntity.getTagId(), timeSerieData.getChannelId(), since);
        entity.setId(id);
        entity.setUntil(timeSerieData.getUntil());
        entity.setJsonPayload(timeSerieData.getData());
        TimeSerieIov saved = save(entity, tagEntity);
        if (saved != null) {
            log.info(" - iov saved: {}", saved);
            nsaved++;
        }
        log.info("TimeSerieIovService.save: {} timeserie saved", nsaved);
        return nsaved;
    }


    /**
     * Save a resource.
     *
     * @param entity
     * @param tagEntity
     * @return TimeSerieIov
     */
    protected TimeSerieIov save(TimeSerieIov entity, Tag tagEntity) {
        log.debug("IovService.save called");
        // Check if an entity exists for which the since until ranges do contain the new entity.
        TimeSerieIov existing = iovRepository.selectAtTime(tagEntity.getName(),
                entity.getId().getChannelId(), entity.getId().getSince());
        if (existing != null) {
            // Change the until time of the existing entity.
            log.warn("IovService.save WARN: entity already exists, update  until: {}", existing);
            existing.setUntil(entity.getId().getSince());
            iovRepository.save(existing);
        }
        // Check the PK.
        TimeSerieIov existingPk = iovRepository.findById(entity.getId())
                .orElse(null);
        if (existingPk != null) {
            log.warn("SKIP Iov (already exists) with PK: {}", entity.getId());
            return null;
        }
        BigInteger lastSince = iovRepository.checkLast(tagEntity.getName(),
                entity.getId().getChannelId());
        if (lastSince != null && lastSince.compareTo(entity.getId().getSince()) > 0) {
            log.error("IovService.save ERROR: last since {} is after the one you are tying to "
                            + "insert {}, insertion are allowed only in append mode.", lastSince,
                    entity.getId().getSince());
            throw new ConflictException("TimeSerieIov already exists with since after the one "
                    + "you are tying to insert for channel "
                    + entity.getId().getChannelId()
                    + " and tag " + entity.getId().getTagId());
        }
        return iovRepository.save(entity);
    }

    /**
     * Delete all iovs for a given tag.
     * @param name the tag name
     * @return the number of deleted entries
     */
    @Transactional
    public Integer deleteIovForTag(String name) {
        return iovRepository.deleteByTagName(name);
    }
}
