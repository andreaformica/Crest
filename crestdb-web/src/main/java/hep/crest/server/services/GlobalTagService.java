/**
 *
 */
package hep.crest.server.services;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbBadRequestException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.GlobalTagRepository;
import hep.crest.data.repositories.args.GtagQueryArgs;
import hep.crest.server.controllers.PageRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author formica
 * @author rsipos
 *
 */
@Service
public class GlobalTagService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalTagService.class);

    /**
     * Repository.
     */
    @Autowired
    private GlobalTagRepository globalTagRepository;
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;

    /**
     * Select GlobalTags.
     *
     * @param args
     * @param preq
     * @throws AbstractCdbServiceException
     *             Bad request
     * @return Page of GlobalTags
     */
    public Page<GlobalTag> selectGlobalTagList(GtagQueryArgs args, Pageable preq) throws AbstractCdbServiceException {
        Page<GlobalTag> entitylist = null;
        try {
            if (preq == null) {
                String sort = "id.since:ASC,id.insertionTime:DESC";
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
     * @param globaltagname
     *            the String
     * @throws AbstractCdbServiceException
     *             If object was not found
     * @return GlobalTag
     */
    public GlobalTag findOne(String globaltagname) throws AbstractCdbServiceException {
        log.debug("Search for global tag by name {}", globaltagname);
        return globalTagRepository.findByName(globaltagname).orElseThrow(
                () -> new CdbNotFoundException("Cannot find global tag " + globaltagname));
    }

    /**
     * @param globaltagname
     *            the String
     * @param record
     *            the String
     * @param label
     *            the String
     * @return List<Tag>
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    public List<Tag> getGlobalTagByNameFetchTags(String globaltagname, String record, String label)
            throws AbstractCdbServiceException {
        String rec = null;
        String lab = null;
        log.debug("Search for specified tag list for GlobalTag={} using {} {}", globaltagname, record, label);
        if (record != null && !"none".equalsIgnoreCase(record)) {
            rec = record;
        }
        if (label != null && !"none".equalsIgnoreCase(label)) {
            lab = label;
        }
        GlobalTag entity = globalTagRepository.findGlobalTagFetchTags(globaltagname, rec, lab).orElseThrow(
                () -> new CdbNotFoundException("Cannot find global tag for " + globaltagname));

        return entity.globalTagMaps().stream().map(GlobalTagMap::tag).collect(Collectors.toList());
    }

    /**
     * @param entity
     *            the GlobalTag
     * @return GlobalTag
     * @throws AbstractCdbServiceException
     *             If an Exception occurred because pojo exists
     */
    @Transactional
    public GlobalTag insertGlobalTag(GlobalTag entity) throws ConflictException {
        log.debug("Create GlobalTag from {}", entity);
        final Optional<GlobalTag> tmpgt = globalTagRepository.findById(entity.name());
        if (tmpgt.isPresent()) {
            log.warn("GlobalTag {} already exists.", tmpgt.get());
            throw new ConflictException(
                    "GlobalTag already exists for name " + entity.name());
        }
        final GlobalTag saved = globalTagRepository.save(entity);
        log.debug("Saved entity {}", saved);
        return saved;
    }

    /**
     * @param entity
     *            the GlobaTag
     * @return GlobalTag
     * @throws AbstractCdbServiceException
     *             If object was not found
     */
    @Transactional
    public GlobalTag updateGlobalTag(GlobalTag entity) throws CdbNotFoundException {
        log.debug("Update GlobalTag from {}", entity);
        final GlobalTag toupd =
                globalTagRepository.findById(entity.name()).orElseThrow(
                        () -> new CdbNotFoundException("GlobalTag does not exists for name " + entity.name()));
        toupd.description(entity.description()).release(entity.release())
                .scenario(entity.scenario()).snapshotTime(entity.snapshotTime())
                .workflow(entity.workflow()).type(entity.type()).validity(entity.validity());
        final GlobalTag saved = globalTagRepository.save(toupd);
        log.debug("Saved entity: {}", saved);
        return saved;
    }

    /**
     * @param name
     *            the String
     */
    @Transactional
    public void removeGlobalTag(String name) {
        log.debug("Remove global tag {}", name);
        globalTagRepository.deleteById(name);
        log.debug("Removed entity: {}", name);
    }
}
