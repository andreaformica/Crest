/**
 *
 */
package hep.crest.server.services;

import hep.crest.data.exceptions.CdbServiceException;
import hep.crest.data.repositories.TagMetaDataBaseCustom;
import hep.crest.server.exceptions.AlreadyExistsPojoException;
import hep.crest.swagger.model.TagMetaDto;
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
     * @throws AlreadyExistsPojoException
     *             If an Exception occurred
     * @throws CdbServiceException
     *             If an Exception occurred
     */
    public TagMetaDto insertTagMeta(TagMetaDto dto) {
        log.debug("Create tag meta data from dto {}", dto);
        final TagMetaDto saved = tagmetaRepository.save(dto);
        log.info("Saved entity: {}", saved);
        return saved;
    }

    /**
     * Update an existing tag meta data.
     *
     * @param dto
     *            the TagMetaDto
     * @return TagMetaDto
     * @throws CdbServiceException If an exception occurred.
     */
    public TagMetaDto updateTagMeta(TagMetaDto dto) {
        log.debug("Update tag meta from dto {}", dto);
        final TagMetaDto saved = tagmetaRepository.update(dto);
        log.debug("Updated entity: {}", saved);
        return saved;
    }

    /**
     * @param id
     *            the String
     * @return TagMetaDto
     * @throws CdbServiceException
     *             If an Exception occurred
     */
    public TagMetaDto findMeta(String id) {
        log.debug("Search for tag meta data by Id...{}", id);
        final TagMetaDto tmpt = tagmetaRepository.find(id);
        return tmpt; // This will trigger a response 404 if it is null
    }

    /**
     * Remote tag meta.
     *
     * @param name the name
     * @throws CdbServiceException the cdb service exception
     */
    public void removeTagMeta(String name) {
        log.debug("Remove tag meta info for {}", name);
        tagmetaRepository.delete(name);
        log.debug("Removed tag meta info for: {}", name);
    }
}
