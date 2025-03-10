/**
 *
 */
package hep.crest.server.services;

import hep.crest.server.data.pojo.GlobalTag;
import hep.crest.server.data.pojo.GlobalTagMap;
import hep.crest.server.data.pojo.GlobalTagTypeEnum;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.GlobalTagMapRepository;
import hep.crest.server.data.repositories.GlobalTagRepository;
import hep.crest.server.data.repositories.TagRepository;
import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.exceptions.ConflictException;
import hep.crest.server.swagger.model.TagDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * @author formica
 * @author rsipos
 */
@Service
@Slf4j
public class GlobalTagMapService {
    /**
     * Repository.
     */
    private GlobalTagMapRepository globalTagMapRepository;
    /**
     * Repository.
     */
    private GlobalTagRepository globalTagRepository;
    /**
     * Repository.
     */
    private TagRepository tagRepository;
    /**
     * Service.
     */
    private TagService tagService;

    /**
     * Ctor with injection.
     * @param globalTagMapRepository
     * @param globalTagRepository
     * @param tagService
     */
    @Autowired
    public GlobalTagMapService(GlobalTagMapRepository globalTagMapRepository,
                               GlobalTagRepository globalTagRepository,
                               TagService tagService) {
        this.globalTagMapRepository = globalTagMapRepository;
        this.globalTagRepository = globalTagRepository;
        this.tagService = tagService;
        this.tagRepository = tagService.getTagRepository();
    }


    /**
     * @param gtName the String represnting the GlobalTag name
     * @return Iterable<GlobalTagMap>
     */
    public List<GlobalTagMap> getTagMap(String gtName) {
        log.debug("Search for GlobalTagMap entries by GlobalTag name {}", gtName);
        return globalTagMapRepository.findByGlobalTagName(gtName);
    }

    /**
     * @param tagName the String
     * @return Iterable<GlobalTagMapDto>
     */
    public List<GlobalTagMap> getTagMapByTagName(String tagName) {
        log.debug("Search for GlobalTagMap entries by Tag name {}", tagName);
        return globalTagMapRepository.findByTagName(tagName);
    }

    /**
     * @param entity the GlobalTagMap
     * @return GlobalTagMap
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    @Transactional
    public GlobalTagMap insertGlobalTagMap(GlobalTagMap entity) throws AbstractCdbServiceException {
        log.debug("Create GlobalTagMap from {}", entity);
        Optional<GlobalTagMap> map = globalTagMapRepository.findById(entity.getId());
        if (map.isPresent()) {
            log.warn("GlobalTagMap {} already exists.", map.get());
            throw new ConflictException("GlobalTagMap already exists for ID " + entity.getId());
        }
        String gtname = entity.getId().getGlobalTagName();
        final Optional<GlobalTag> gt = globalTagRepository.findById(gtname);
        if (!gt.isPresent()) {
            log.warn("GlobalTag {} does not exists.", gtname);
            throw new CdbNotFoundException("GlobalTag does not exists for name " + gtname);
        }
        String tagname = entity.getTag().getName();
        final Optional<Tag> tg = tagRepository.findById(tagname);
        if (!tg.isPresent()) {
            log.warn("Tag {} does not exists.", tagname);
            throw new CdbNotFoundException("Tag does not exists for name " + tagname);
        }
        entity.setGlobalTag(gt.get());
        entity.setTag(tg.get());
        final GlobalTagMap saved = globalTagMapRepository.save(entity);
        log.debug("Saved entity: {}", saved);
        return saved;
    }

    /**
     * Find maps by global tag label tag.
     *
     * @param globaltag the globaltag
     * @param label     the label
     * @param tag       the tag
     * @param mrecord   the record
     * @return the iterable
     */
    public List<GlobalTagMap> findMapsByGlobalTagLabelTag(String globaltag, String label,
                                                              String tag,
                                                              String mrecord) {
        log.debug("Search for GlobalTagMap entries by Global, Label, Tag and eventually filter by"
                  + " record : {} {} {} {}",
                globaltag, label, tag, mrecord);
        if (tag == null || tag.isEmpty() || tag.equals("none")) {
            tag = "%";
        }
        tag = tag.replace("*", "%");
        List<GlobalTagMap> maplist =
                globalTagMapRepository.findByGlobalTagNameAndLabelAndTagNameLike(globaltag, label,
                tag);
        if (mrecord != null && !mrecord.isEmpty()) {
            return StreamSupport.stream(maplist.spliterator(), false)
                    .filter(c -> c.getId().getTagRecord().equals(mrecord))
                    .toList();
        }
        return maplist;
    }

    /**
     * Delete map.
     *
     * @param entity the entity
     * @return the global tag map
     */
    @Transactional
    protected GlobalTagMap deleteMap(GlobalTagMap entity) {
        globalTagMapRepository.delete(entity);
        return entity;
    }

    /**
     * Delete a list of mappings between tags and global tags.
     *
     * @param entitylist
     * @return a list of deleted mappings
     */
    @Transactional
    public List<GlobalTagMap> deleteMapList(Iterable<GlobalTagMap> entitylist) {
        List<GlobalTagMap> deletedlist = new ArrayList<>();
        for (GlobalTagMap globalTagMap : entitylist) {
            GlobalTag globalTag = globalTagMap.getGlobalTag();
            if (globalTag.getType() == GlobalTagTypeEnum.LOCKED.getCode()) {
                log.warn("GlobalTag {} is locked, cannot delete mapping {}",
                        globalTag.getName(), globalTagMap);
                throw new ConflictException("GlobalTag is locked, cannot delete mapping");
            }
            Tag tag = globalTagMap.getTag();
            if (tag.getStatus().equals(TagDto.StatusEnum.LOCKED.toString())) {
                log.warn("Tag {} is locked, cannot delete mapping {}",
                        tag.getName(), globalTagMap);
                throw new ConflictException("Tag is locked, cannot delete mapping");
            }
            log.info("Delete GlobalTagMap entry {}: {} {}", globalTagMap, globalTag, tag);
            GlobalTagMap delentity = this.deleteMap(globalTagMap);
            if (delentity != null) {
                deletedlist.add(delentity);
            }
        }
        return deletedlist;
    }

    /**
     * Check the type of the tag, using the list of GlobalTagMap entries.
     * If a locked global tag is found, the type is locked.
     * When locked, a tag takes by default a synchronized status of append-only, in case
     * the synchronization is not set (or set to none).
     *
     * @param globaltag the GlobalTag to check
     */
    @Transactional
    public void updateAssociatedTagsType(GlobalTag globaltag) {
        log.debug("Check tag type for list of GlobalTagMap entries");
        List<GlobalTagMap> mapList = this.getTagMap(globaltag.getName());
        // Check if the list is empty.
        if (mapList.isEmpty()) {
            log.info("No GlobalTagMap entries found.");
            return;
        }
        char gtype = globaltag.getType();
        for (GlobalTagMap map : mapList) {
            if (GlobalTagTypeEnum.fromCode(gtype) == GlobalTagTypeEnum.LOCKED) {
                Tag tag = map.getTag();
                tag.setStatus(TagDto.StatusEnum.LOCKED.toString());
                String sync = tag.getSynchronization();
                if (sync == null || sync.isEmpty() || sync.equals("none")) {
                    log.warn("Tag {} is locked, setting synchronization to append-only",
                            tag.getName());
                    tag.setSynchronization(TagDto.SynchronizationEnum.SV.toString());
                }
                tagService.updateTag(tag);
            }
            else {
                Tag tag = updateTagStatus(map.getTag().getName(),
                        TagDto.StatusEnum.UNLOCKED.toString());
                log.debug("Tag status after attempt to unlock : {} ", tag);
            }
        }
    }

    /**
     * @param tagname the String
     * @param status  the String
     * @return Tag
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    public Tag updateTagStatus(String tagname, String status) throws AbstractCdbServiceException {
        log.debug("Update Tag status for {} to {}", tagname, status);
        List<GlobalTagMap> tagMaps = this.getTagMapByTagName(tagname);
        Tag tag = tagRepository.findById(tagname).orElseThrow(
                () -> new CdbNotFoundException("Tag does not exists for name " + tagname));
        for (GlobalTagMap map : tagMaps) {
            char gtypechar = map.getGlobalTag().getType();
            if (GlobalTagTypeEnum.fromCode(gtypechar) == GlobalTagTypeEnum.LOCKED) {
                log.warn("Tag {} is associated to a locked global tag {}, cannot change status to"
                                + " {}",
                        tagname, map.getGlobalTag().getName(), status);
                throw new ConflictException("Tag is locked, cannot change status to " + status);
            }
        }
        // No constraints found, update the tag.
        tag.setStatus(TagDto.StatusEnum.valueOf(status).toString());
        return tagService.updateTag(tag);
    }
}
