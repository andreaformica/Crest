/**
 *
 */
package hep.crest.server.services;

import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.data.pojo.GlobalTag;
import hep.crest.server.data.pojo.GlobalTagMap;
import hep.crest.server.data.pojo.GlobalTagTypeEnum;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.GlobalTagRepository;
import hep.crest.server.data.repositories.args.GtagQueryArgs;
import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbBadRequestException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.exceptions.ConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author formica
 * @author rsipos
 */
@Service
@Slf4j
public class GlobalTagService {
    /**
     * Repository.
     */
    private GlobalTagRepository globalTagRepository;
    /**
     * Helper.
     */
    private PageRequestHelper prh;
    /**
     * Service.
     */
    private GlobalTagMapService globalTagMapService;

    /**
     * Ctor with injection.
     * @param globalTagRepository
     * @param globalTagMapService
     * @param prh
     */
    @Autowired
    public GlobalTagService(GlobalTagRepository globalTagRepository,
                            GlobalTagMapService globalTagMapService,
                            PageRequestHelper prh) {
        this.globalTagRepository = globalTagRepository;
        this.globalTagMapService = globalTagMapService;
        this.prh = prh;
    }

    /**
     * Select GlobalTags.
     *
     * @param args
     * @param preq
     * @return Page of GlobalTags
     * @throws AbstractCdbServiceException Bad request
     */
    public Page<GlobalTag> selectGlobalTagList(GtagQueryArgs args, Pageable preq)
            throws AbstractCdbServiceException {
        Page<GlobalTag> entitylist = null;
        try {
            if (preq == null) {
                String sort = "name:ASC";
                preq = prh.createPageRequest(0, 1000, sort);
            }
            entitylist = globalTagRepository.findGlobalTagList(args, preq);
            log.trace("Retrieved list of global tags {}", entitylist);
            return entitylist;
        }
        catch (RuntimeException e) {
            throw new CdbBadRequestException("Something wrong with the request: " + e.getMessage());
        }
    }

    /**
     * @param globaltagname the String
     * @return GlobalTag
     * @throws AbstractCdbServiceException If object was not found
     */
    public GlobalTag findOne(String globaltagname) throws AbstractCdbServiceException {
        log.debug("Search for global tag by name {}", globaltagname);
        return globalTagRepository.findByName(globaltagname).orElseThrow(
                () -> new CdbNotFoundException("Cannot find global tag " + globaltagname));
    }

    /**
     * @param globaltagname the String
     * @param mrecord       the String
     * @param label         the String
     * @return List<Tag>
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    public List<Tag> getGlobalTagByNameFetchTags(String globaltagname, String mrecord, String label)
            throws AbstractCdbServiceException {
        String rec = null;
        String lab = null;
        log.debug("Search for specified tag list for GlobalTag={} using {} {}", globaltagname,
                mrecord, label);
        if (mrecord != null && !"none".equalsIgnoreCase(mrecord)) {
            rec = mrecord;
        }
        if (label != null && !"none".equalsIgnoreCase(label)) {
            lab = label;
        }
        GlobalTag entity =
                globalTagRepository.findGlobalTagFetchTags(globaltagname, rec, lab).orElseThrow(
                () -> new CdbNotFoundException("Cannot find global tag for " + globaltagname));

        return entity.getGlobalTagMaps().stream().map(GlobalTagMap::getTag).toList();
    }

    /**
     * @param entity the GlobalTag
     * @return GlobalTag
     * @throws AbstractCdbServiceException If an Exception occurred because pojo exists
     */
    @Transactional
    public GlobalTag insertGlobalTag(GlobalTag entity) throws ConflictException {
        log.debug("Create GlobalTag from {}", entity);
        final Optional<GlobalTag> tmpgt = globalTagRepository.findById(entity.getName());
        if (tmpgt.isPresent()) {
            log.warn("GlobalTag {} already exists.", tmpgt.get());
            throw new ConflictException(
                    "GlobalTag already exists for name " + entity.getName());
        }
        final GlobalTag saved = globalTagRepository.save(entity);
        log.debug("Saved entity {}", saved);
        return saved;
    }

    /**
     * @param entity the GlobaTag
     * @return GlobalTag
     * @throws AbstractCdbServiceException If object was not found
     */
    @Transactional
    public GlobalTag updateGlobalTag(GlobalTag entity) throws AbstractCdbServiceException {
        log.debug("Update GlobalTag from {}", entity);
        final GlobalTag toupd =
                globalTagRepository.findById(entity.getName()).orElseThrow(
                        () -> new CdbNotFoundException(
                                "GlobalTag does not exists for name " + entity.getName()));
        char type = entity.getType();
        GlobalTagTypeEnum.fromCode(type);
        toupd.setDescription(entity.getDescription()).setRelease(entity.getRelease())
                .setScenario(entity.getScenario()).setSnapshotTime(entity.getSnapshotTime())
                .setWorkflow(entity.getWorkflow()).setType(type)
                .setValidity(entity.getValidity());
        final GlobalTag saved = globalTagRepository.save(toupd);
        log.debug("Saved entity: {}", saved);

        return saved;
    }

    /**
     * @param name the String
     */
    @Transactional
    public void removeGlobalTag(String name) throws CdbNotFoundException {
        log.debug("Remove global tag {}", name);
        GlobalTag globalTag = globalTagRepository.findByName(name).orElseThrow(
                () -> new CdbNotFoundException("Cannot remove global tag " + name)
        );
        if (globalTag.getType() == GlobalTagTypeEnum.LOCKED.getCode()) {
            throw new ConflictException("Cannot remove locked global tag " + name);
        }
        // Check if mappings are present
        List<GlobalTagMap> maps = globalTagMapService.getTagMap(name);
        if (!maps.isEmpty()) {
            log.error("Cannot remove global tag {}, found association with tags.", name);
            throw new ConflictException("Cannot remove global tag "
                    + name
                    + ": clean up associations with tags");
        }
        globalTagRepository.deleteById(name);
        log.debug("Removed entity: {}", name);
    }

    @Transactional
    public GlobalTag lockGlobalTag(String name, String status) {
        GlobalTag entity = globalTagRepository.findByName(name).orElseThrow(
                () -> new CdbNotFoundException("Cannot find global tag " + name));
        char inputState = status.charAt(0);
        GlobalTagTypeEnum typeEnum = GlobalTagTypeEnum.fromCode(inputState);
        entity.setType(typeEnum.getCode());
        GlobalTag saved = globalTagRepository.save(entity);
        globalTagMapService.updateAssociatedTagsType(saved);
        return saved;
    }
}
