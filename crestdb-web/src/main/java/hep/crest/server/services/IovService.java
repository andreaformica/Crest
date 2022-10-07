/**
 *
 */
package hep.crest.server.services;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.IovRepository;
import hep.crest.data.repositories.TagRepository;
import hep.crest.data.repositories.args.IovQueryArgs;
import hep.crest.server.annotations.ProfileAndLog;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.repositories.IovGroupsCustom;
import hep.crest.server.repositories.PayloadDataBaseCustom;
import hep.crest.server.swagger.model.CrestBaseResponse;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovPayloadDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.TagSummaryDto;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author formica
 *
 */
@Service
public class IovService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(IovService.class);

    /**
     * Repository.
     */
    @Autowired
    private IovRepository iovRepository;
    /**
     * Repository.
     */
    @Autowired
    private TagRepository tagRepository;
    /**
     * Repository.
     */
    @Autowired
    @Qualifier("payloaddatadbrepo")
    private PayloadDataBaseCustom payloaddataRepository;

    /**
     * Repository.
     */
    @Autowired
    @Qualifier("iovgroupsrepo")
    private IovGroupsCustom iovgroupsrepo;
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;

    /**
     * @param tagname
     *            the String
     * @param snapshot
     *            the Date
     * @param groupsize
     *            the Long
     * @return List<BigDecimal>
     */
    public List<BigDecimal> selectGroupsByTagNameAndSnapshotTime(String tagname, Date snapshot,
                                                                 Long groupsize) {
        log.debug("Search for iovs groups by tag name {} and snapshot time {}", tagname, snapshot);
        List<BigDecimal> minsincelist = null;
        if (snapshot == null) {
            minsincelist = iovgroupsrepo.selectGroups(tagname, groupsize);
        }
        else {
            minsincelist = iovgroupsrepo.selectSnapshotGroups(tagname, snapshot, groupsize);
        }
        if (minsincelist == null) {
            minsincelist = new ArrayList<>();
        }
        return minsincelist;
    }

    /**
     * @param tagname
     *            the String
     * @param snapshot
     *            the Date
     * @param groupsize
     *            the Long
     * @return CrestBaseResponse
     */
    @ProfileAndLog
    public CrestBaseResponse selectGroupDtoByTagNameAndSnapshotTime(String tagname, Date snapshot,
                                                                    Long groupsize) {
        final List<BigDecimal> minsincelist = selectGroupsByTagNameAndSnapshotTime(tagname,
                snapshot, groupsize);
        final List<IovDto> iovlist = minsincelist.stream().map(s -> new IovDto().since(s).tagName(tagname))
                .collect(Collectors.toList());
        return new IovSetDto().resources(iovlist).size((long) iovlist.size());
    }

    /**
     * Select Iovs.
     *
     * @param args
     * @param preq
     * @return Page of Iov
     */
    public Page<Iov> selectIovList(IovQueryArgs args, Pageable preq) {
        Page<Iov> entitylist = null;
        if (preq == null) {
            String sort = "id.since:ASC,id.insertionTime:DESC";
            preq = prh.createPageRequest(0, 1000, sort);
        }
        entitylist = iovRepository.findIovList(args, preq);
        log.trace("Retrieved list of iovs {}", entitylist);
        return entitylist;
    }

    /**
     * @param tagname
     *            the String
     * @param since
     *            the BigDecimal
     * @param until
     *            the BigDecimal
     * @param snapshot
     *            the Date
     * @return List<IovPayloadDto>
     */
    public List<IovPayloadDto> selectIovPayloadsByTagRangeSnapshot(String tagname, BigDecimal since,
                                                                   BigDecimal until, Date snapshot) {
        log.debug("Search for iovs by tag name {}  and range time {} -> {} using snapshot {}",
                tagname, since, until, snapshot);
        List<IovPayloadDto> entities = null;
        if (snapshot == null || snapshot.getTime() == 0) {
            snapshot = Instant.now().toDate(); // Use now for the snapshot
        }
        entities = payloaddataRepository.getRangeIovPayloadInfo(tagname, since, until, snapshot);

        if (entities == null) {
            log.warn("Cannot find iovpayloads for tag {} using ranges {} {} and snapshot {}",
                    tagname, since, until, snapshot);
            return new ArrayList<>();
        }
        return entities;
    }

    /**
     * @param tagname
     *            the String
     * @return Long
     */
    public Long getSizeByTag(String tagname) {
        log.debug("Count number of iovs by tag name {}", tagname);
        return iovgroupsrepo.getSize(tagname);
    }

    /**
     * @param tagname
     *            the String
     * @param snapshot
     *            the Date
     * @return Long
     */
    public Long getSizeByTagAndSnapshot(String tagname, Date snapshot) {
        log.debug("Count number of iovs by tag name {} and snapshot {}", tagname, snapshot);
        return iovgroupsrepo.getSizeBySnapshot(tagname, snapshot);
    }

    /**
     * @param tagname
     *            the String
     * @return List<TagSummaryDto>
     */
    public List<TagSummaryDto> getTagSummaryInfo(String tagname) {
        log.debug("Tag summary by tag name {}", tagname);
        List<TagSummaryDto> entitylist = iovgroupsrepo.getTagSummaryInfo(tagname);
        if (entitylist == null) {
            entitylist = new ArrayList<>();
        }
        return entitylist;
    }

    /**
     * Return the last iov of a tag.
     * @param tagname
     * @return Iov
     */
    public Iov latest(String tagname) {
        String sort = "id.since:DESC,id.insertionTime:DESC";
        Pageable preq = prh.createPageRequest(0, 1, sort);
        Page<Iov> lastiovlist = iovRepository.findByIdTagName(tagname, preq);
        if (lastiovlist == null || lastiovlist.getSize() == 0) {
            throw new CdbNotFoundException("Cannot find last iov for tag " + tagname);
        }
        return lastiovlist.toList().get(0);
    }

    /**
     * @param tagname
     *            the String
     * @param since
     *            the BigDecimal
     * @param hash
     *            the String
     * @return boolean
     */
    public boolean existsIov(String tagname, BigDecimal since, String hash) {
        log.debug("Verify if the same IOV is already stored with the same hash....");
        final Iov tmpiov = iovRepository.exists(tagname, since, hash);
        return tmpiov != null;
    }

    /**
     * @param entity
     *            the IovDto
     * @return Iov
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     * @throws DataIntegrityViolationException If an sql exception occurred.
     */
    @Transactional(rollbackOn = {AbstractCdbServiceException.class})
    public Iov insertIov(Iov entity) throws AbstractCdbServiceException {
        log.debug("Create iov from {}", entity);
        final String tagname = entity.tag().name();
        // The IOV is not yet stored. Verify that the tag exists before inserting it.
        final Optional<Tag> tg = tagRepository.findById(tagname);
        if (!tg.isPresent()) {
            throw new CdbNotFoundException("Tag " + tagname + " not found: cannot insert IOV.");
        }
        final Tag t = tg.get();
        t.modificationTime(Instant.now().toDate());
        // Check if iov exists
        if (existsIov(t.name(), entity.id().since(), entity.payloadHash())) {
            log.warn("Iov already exists [tag,since,hash]: {}", entity);
            throw new ConflictException("Iov already exists [tag,since,hash]: " + entity.toString());
        }
        // Update the tag modification time
        final Tag updtag = tagRepository.save(t);
        entity.tag(updtag);
        entity.id().tagName(updtag.name());
        log.debug("Storing iov entity {} in tag {}", entity, updtag);
        final Iov saved = iovRepository.save(entity);
        log.debug("Saved entity: {}", saved);
        return saved;
    }
}
