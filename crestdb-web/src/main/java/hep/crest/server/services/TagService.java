/**
 *
 */
package hep.crest.server.services;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbInternalException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.data.pojo.TagMeta;
import hep.crest.data.repositories.IovGroupsCustom;
import hep.crest.data.repositories.IovRepository;
import hep.crest.data.repositories.TagMetaRepository;
import hep.crest.data.repositories.TagRepository;
import hep.crest.data.repositories.args.TagQueryArgs;
import hep.crest.server.controllers.PageRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author rsipos
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
    private IovService iovService;
    /**
     * Repository.
     */
    @Autowired
    private PayloadService payloadService;
    /**
     * Service.
     */
    @Autowired
    private TagMetaService tagMetaService;

    /**
     * Repository.
     */
    @Autowired
    private TagMetaRepository tagMetaRepository;

    /**
     * Repository.
     */
    @Autowired
    private IovGroupsCustom iovGroupsCustom;

    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;

    /**
     * @param tagname the String
     * @return boolean
     * @throws AbstractCdbServiceException If an Exception occurred
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
     * @param id the String representing the Tag name
     * @return Tag
     * @throws AbstractCdbServiceException If object was not found
     */
    public Tag findOne(String id) throws AbstractCdbServiceException {
        log.debug("Search for tag by Id...{}", id);
        return tagRepository.findById(id).orElseThrow(() -> new CdbNotFoundException(
                "Tag not found: " + id));
    }

    /**
     * Select Tags.
     *
     * @param args
     * @param preq
     * @return Page of Tag
     */
    public Page<Tag> selectTagList(TagQueryArgs args, Pageable preq) {
        Page<Tag> entitylist = null;
        if (preq == null) {
            String sort = "id.since:ASC,id.insertionTime:DESC";
            preq = prh.createPageRequest(0, 1000, sort);
        }
        entitylist = tagRepository.findTagList(args, preq);
        log.trace("Retrieved list of tags {}", entitylist);
        return entitylist;
    }


    /**
     * @param entity the Tag
     * @return Tag
     * @throws ConflictException If an Exception occurred because pojo exists
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
     * @param entity the Tag
     * @return TagDto of the updated entity.
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    @Transactional
    public Tag updateTag(Tag entity) throws AbstractCdbServiceException {
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
     * @param name the String
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    @Transactional
    public void removeTag(String name) throws AbstractCdbServiceException {
        log.debug("Remove tag {} after checking if IOVs are present", name);
        Tag remTag = tagRepository.findById(name).orElseThrow(
                () -> new CdbNotFoundException("Tag does not exists for name " + name));
        // Remove meta information associated with the tag.
        log.debug("Removing meta info on tag {}", remTag);
        Optional<TagMeta> opt = tagMetaRepository.findByTagName(name);
        if (opt.isPresent()) {
            tagMetaService.removeTagMeta(name);
        }

        log.debug("Removing tag {}", remTag);
        long niovs = iovGroupsCustom.getSize(name);
        if (niovs > 0) {
            String sort = "id.since:ASC,id.insertionTime:DESC";
            Pageable preq = prh.createPageRequest(0, 1000, sort);
            Page<Iov> iovspage = iovRepository.findByIdTagName(name, preq);
            for (int ip = 0; ip < iovspage.getTotalPages(); ip++) {
                List<Iov> iovlist = iovspage.getContent();
                log.info("Delete {} payloads associated to iovs....", niovs);
                List<String> hashList = this.removeIovList(iovlist);
                for (String hash : hashList) {
                    log.debug("Delete payload {}....", hash);
                    // Delete iov payloads one by one because we need to check the payload
                    // It could belong as well to another tag, in that case we cannot remove it
                    // but we can remove the iov.
                    String rem = payloadService.removePayload(name, hash);
                    if (!rem.equals(hash)) {
                        log.warn("Skip removal of payload for hash {}", hash);
                    }
                }
            }
        }
        tagRepository.deleteById(name);
        log.debug("Removed entity: {}", name);
    }

    /**
     * Remove a list of iovs, send back the hash of payloads.
     *
     * @param iovList
     * @return List<String>
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected List<String> removeIovList(List<Iov> iovList) {
        List<String> hashList = new ArrayList<>();
        for (Iov iov : iovList) {
            log.debug("Delete iov {}....", iov);
            hashList.add(iov.payloadHash());
            iovRepository.delete(iov);
        }
        return hashList;
    }
}
