/**
 *
 */
package hep.crest.server.services;

import com.querydsl.core.types.Predicate;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.CdbServiceException;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.GlobalTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     * @param qry
     *            the Predicate
     * @param req
     *            the Pageable
     * @return Page<GlobalTag>
     */
    public Page<GlobalTag> findAllGlobalTags(Predicate qry, Pageable req) {
        Page<GlobalTag> entitylist = null;
        if (req == null) {
            req = PageRequest.of(0, 1000);
        }
        if (qry == null) {
            entitylist = globalTagRepository.findAll(req);
        }
        else {
            entitylist = globalTagRepository.findAll(qry, req);
        }
        return entitylist;
    }


    /**
     * @param globaltagname
     *            the String
     * @throws CdbServiceException
     *             If object was not found
     * @return GlobalTag
     */
    public GlobalTag findOne(String globaltagname) throws CdbServiceException {
        log.debug("Search for global tag by name {}", globaltagname);
        final GlobalTag entity = globalTagRepository.findByName(globaltagname).orElseThrow(
                () -> new CdbNotFoundException("Cannot find global tag " + globaltagname));
        return entity;
    }

    /**
     * @param globaltagname
     *            the String
     * @param record
     *            the String
     * @param label
     *            the String
     * @return List<Tag>
     * @throws CdbServiceException
     *             If an Exception occurred
     */
    public List<Tag> getGlobalTagByNameFetchTags(String globaltagname, String record, String label)
            throws CdbServiceException {
        GlobalTag entity = null;
        log.debug("Search for (record, label) specified tag list for GlobalTag={}", globaltagname);
        if ("none".equalsIgnoreCase(record)) {
            record = "";
        }
        if ("none".equalsIgnoreCase(label)) {
            label = "";
        }
        final String rec = record;
        final String lab = label;
        if (rec.isEmpty() && lab.isEmpty()) {
            entity =
                    globalTagRepository.findByNameAndFetchTagsEagerly(globaltagname).orElseThrow(
                            () -> new CdbNotFoundException("Cannot find tags for globaltag " + globaltagname));
        }
        else if (lab.isEmpty()) {
            entity = globalTagRepository.findByNameAndFetchRecordTagsEagerly(globaltagname, record).orElseThrow(
                    () -> new CdbNotFoundException("Cannot find tags for globaltag " + globaltagname
                                                   + " and record " + rec));
        }
        else {
            entity = globalTagRepository.findByNameAndFetchSpecifiedTagsEagerly(globaltagname,
                    record, label).orElseThrow(
                    () -> new CdbNotFoundException("Cannot find tags for globaltag " + globaltagname
                                                   + ", record " + rec + ", label " + lab));
        }
        final List<Tag> taglist = entity.globalTagMaps().stream().map(GlobalTagMap::tag).collect(Collectors.toList());
        return taglist;
    }

    /**
     * @param entity
     *            the GlobalTag
     * @return GlobalTag
     * @throws CdbServiceException
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
     * @throws CdbServiceException
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
