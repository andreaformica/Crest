/**
 *
 */
package hep.crest.server.services;

import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.exceptions.ConflictException;
import hep.crest.server.data.pojo.TagMeta;
import hep.crest.server.data.repositories.TagMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author rsipos
 *
 */
@Service
@Slf4j
public class TagMetaService {

    /**
     * Repository.
     */
    @Autowired
    private TagMetaRepository tagmetaRepository;

    /**
     * Find TagMeta.
     *
     * @param name
     * @return TagMeta
     * @throws AbstractCdbServiceException
     */
    public TagMeta find(String name) {
        log.debug("Search meta info for tag {}", name);
        return tagmetaRepository.findByTagName(name).orElseThrow(
                () -> new CdbNotFoundException("Cannot find meta info for tag " + name)
        );
    }
    /**
     * Insert new tag meta data.
     *
     * @param entity
     *            the TagMeta
     * @return TagMeta
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    public TagMeta insertTagMeta(TagMeta entity) {
        log.debug("Create tag meta data from entity {}", entity);
        final String name = entity.tagName();
        Optional<TagMeta> opt = tagmetaRepository.findByTagName(name);
        if (opt.isPresent()) {
            log.debug("Cannot store tag meta {} : resource already exists.. ", name);
            throw new ConflictException(
                "Tag meta already exists for name " + name);
        }
        final TagMeta saved = tagmetaRepository.save(entity);
        log.debug("Saved entity: {}", saved);
        return saved;
    }

    /**
     * Update an existing tag meta data.
     *
     * @param entity
     *            the TagMeta
     * @return TagMeta
     * @throws AbstractCdbServiceException If an exception occurred.
     */
    @Transactional
    public TagMeta updateTagMeta(TagMeta entity) {
        log.debug("Update tag meta from entity {}", entity);
        Optional<TagMeta> opt = tagmetaRepository.findByTagName(entity.tagName());
        if (opt.isEmpty()) {
            throw new CdbNotFoundException("Cannot find meta info for " + entity.tagName());
        }
        TagMeta stored = opt.get();
        log.debug("Updating existing tag meta {}", stored);
        stored.tagInfo(entity.tagInfo())
                .chansize(entity.chansize()).colsize(entity.colsize()).description(entity.description());
        final TagMeta saved = tagmetaRepository.save(stored);
        log.debug("Updated entity: {}", saved);
        return saved;
    }

    /**
     * Remote tag meta.
     *
     * @param name the name
     * @throws AbstractCdbServiceException the cdb service exception
     */
    @Transactional
    public void removeTagMeta(String name) {
        log.debug("Remove tag meta info for {}", name);
        Optional<TagMeta> opt = tagmetaRepository.findByTagName(name);
        if (opt.isEmpty()) {
            throw new CdbNotFoundException("Cannot find tag meta for tag name " + name);
        }
        tagmetaRepository.delete(opt.get());
        log.debug("Removed tag meta info for: {}", name);
    }
}
