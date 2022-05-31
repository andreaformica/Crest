/**
 *
 */
package hep.crest.server.services;

import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.server.repositories.TagMetaDataBaseCustom;
import hep.crest.server.swagger.model.TagMetaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author rsipos
 *
 */
@Service
public class TagMetaService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TagMetaService.class);

    /**
     * Repository.
     */
    @Autowired
    @Qualifier("tagmetarepo")
    private TagMetaDataBaseCustom tagmetaRepository;

    /**
     * Insert new tag meta data.
     *
     * @param dto
     *            the TagMetaDto
     * @return TagMetaDto
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    public TagMetaDto insertTagMeta(TagMetaDto dto) {
        log.debug("Create tag meta data from dto {}", dto);
        try {
            final String name = dto.getTagName();
            final TagMetaDto tmpt = this.findMeta(name);
            if (tmpt != null) {
                log.debug("Cannot store tag meta {} : resource already exists.. ", name);
                throw new ConflictException(
                        "Tag meta already exists for name " + name);
            }
        }
        catch (CdbNotFoundException e) {
            log.warn("This is a new meta info...");
        }
        final TagMetaDto saved = tagmetaRepository.save(dto);
        log.debug("Saved entity: {}", saved);
        return saved;
    }

    /**
     * Update an existing tag meta data.
     *
     * @param dto
     *            the TagMetaDto
     * @return TagMetaDto
     * @throws AbstractCdbServiceException If an exception occurred.
     */
    public TagMetaDto updateTagMeta(TagMetaDto dto) {
        log.debug("Update tag meta from dto {}", dto);
        TagMetaDto toupd = this.findMeta(dto.getTagName());
        log.debug("Updating existing tag meta {}", toupd);
        final TagMetaDto saved = tagmetaRepository.update(dto);
        log.debug("Updated entity: {}", saved);
        return saved;
    }

    /**
     * @param id
     *            the String
     * @return TagMetaDto
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    public TagMetaDto findMeta(String id) {
        log.debug("Search for tag meta data by Id...{}", id);
        return tagmetaRepository.find(id);
    }

    /**
     * Remote tag meta.
     *
     * @param name the name
     * @throws AbstractCdbServiceException the cdb service exception
     */
    public void removeTagMeta(String name) {
        log.debug("Remove tag meta info for {}", name);
        tagmetaRepository.delete(name);
        log.debug("Removed tag meta info for: {}", name);
    }
}
