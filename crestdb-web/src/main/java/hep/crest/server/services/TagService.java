/**
 *
 */
package hep.crest.server.services;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import hep.crest.data.exceptions.CdbBadRequestException;
import hep.crest.data.exceptions.CdbInternalException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.CdbServiceException;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.IovRepository;
import hep.crest.data.repositories.TagRepository;
import hep.crest.data.repositories.querydsl.IFilteringCriteria;
import hep.crest.data.repositories.querydsl.SearchCriteria;
import hep.crest.server.controllers.PageRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author rsipos
 *
 */
@Service
public class TagService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TagService.class);

    /**
     * Repository.
     */
    @Autowired
    private TagRepository tagRepository;

    /**
     * Repository.
     */
    @Autowired
    private IovRepository iovRepository;

    /**
     * Repository.
     */
    @Autowired
    private PayloadService payloadService;

    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;
    /**
     * Filtering.
     */
    @Autowired
    @Qualifier("iovFiltering")
    private IFilteringCriteria filtering;

    /**
     * @param tagname
     *            the String
     * @return boolean
     * @throws CdbServiceException
     *             If an Exception occurred
     */
    public boolean exists(String tagname) throws CdbInternalException {
        try {
            log.debug("Verify existence of Tag {}", tagname);
            return tagRepository.existsById(tagname);
        }
        catch (final IllegalArgumentException | InvalidDataAccessApiUsageException e) {
            throw new CdbInternalException("Wrong tagname " + tagname, e);
        }
    }

    /**
     * Count the total number of tags. Use with care and do not expose.
     * @return long
     */
    public long count() {
        log.debug("Search for tag count...");
        return tagRepository.count();
    }

    /**
     * @param id
     *            the String representing the Tag name
     * @return Tag
     * @throws CdbServiceException
     *             If object was not found
     */
    public Tag findOne(String id) throws CdbServiceException {
        log.debug("Search for tag by Id...{}", id);
        if (id == null) {
            throw new CdbBadRequestException("Wrong null argument");
        }
        final Tag entity = tagRepository.findById(id).orElseThrow(() -> new CdbNotFoundException(
                "Tag not found: " + id));
        return entity;
    }

    /**
     * @param ids
     *            the Iterable<String>
     * @return Iterable<Tag>
     */
    public Iterable<Tag> findAllTags(Iterable<String> ids) {
        log.debug("Search for all tags by Id list...");
        return tagRepository.findAllById(ids);
    }

    /**
     * @param qry
     *            the Predicate
     * @param req
     *            the Pageable
     * @return Iterable<Tag>
     */
    public Page<Tag> findAllTags(Predicate qry, Pageable req) {
        Page<Tag> entitylist = null;
        if (req == null) {
            req = PageRequest.of(0, 1000);
        }
        if (qry == null) {
            entitylist = tagRepository.findAll(req);
        }
        else {
            entitylist = tagRepository.findAll(qry, req);
        }
        return entitylist;
    }


    /**
     * @param entity
     *            the Tag
     * @return Tag
     * @throws ConflictException
     *             If an Exception occurred because pojo exists
     */
    @Transactional
    public Tag insertTag(Tag entity) throws ConflictException {
        log.debug("Create Tag from {}", entity);
        final Optional<Tag> tmpt = tagRepository.findById(entity.name());
        if (tmpt.isPresent()) {
            log.warn("Tag {} already exists.", tmpt.get());
            throw new ConflictException(
                    "Tag already exists for name " + entity.name());
        }
        final Tag saved = tagRepository.save(entity);
        log.debug("Saved entity: {}", saved);
        return saved;
    }

    /**
     * Update an existing tag.
     *
     * @param entity
     *            the Tag
     * @return TagDto of the updated entity.
     * @throws CdbServiceException
     *             If an Exception occurred
     */
    @Transactional
    public Tag updateTag(Tag entity) throws CdbServiceException {
        log.debug("Update tag from dto {}", entity);
        final Tag toupd = tagRepository.findById(entity.name()).orElseThrow(
                () -> new CdbNotFoundException("Tag does not exists for name " + entity.name()));
        toupd.description(entity.description()).objectType(entity.objectType())
                .synchronization(entity.synchronization()).endOfValidity(entity.endOfValidity())
                .lastValidatedTime(entity.lastValidatedTime())
                .timeType(entity.timeType());
        final Tag saved = tagRepository.save(toupd);
        log.debug("Updated entity: {}", saved);
        return saved;
    }

    /**
     * @param name
     *            the String
     */
    @Transactional
    public void removeTag(String name) {
        log.debug("Remove tag {} after checking if IOVs are present", name);
        List<SearchCriteria> criteriaList = prh.createMatcherCriteria("tagname:" + name);
        BooleanExpression bytag = prh.buildWhere(filtering, criteriaList);
        long niovs = iovRepository.count(bytag);
        if (niovs > 0) {
            List<Iov> iovlist = iovRepository.findByIdTagName(name);
            log.info("Delete {} payloads associated to iovs....", niovs);
            for (Iov iov : iovlist) {
                log.debug("Delete iov {}....", iov);
                iovRepository.delete(iov);
            }
            for (Iov iov : iovlist) {
                // Delete iov payloads one by one because we need to check the payload
                // It could belong as well to another tag, in that case we cannot remove it
                // but we can remove the iov.
                log.debug("Delete payload {}....", iov.payloadHash());
                String rem = payloadService.removePayload(name, iov.payloadHash());
                if (!rem.equals(iov.payloadHash())) {
                    log.warn("Skip removal of payload for hash {}", iov.payloadHash());
                }
            }
        }
        tagRepository.deleteById(name);
        log.debug("Removed entity: {}", name);
    }
}
