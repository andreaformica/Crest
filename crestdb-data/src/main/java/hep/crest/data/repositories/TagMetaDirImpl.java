package hep.crest.data.repositories;

import hep.crest.data.exceptions.CdbServiceException;
import hep.crest.data.utils.DirectoryUtilities;
import hep.crest.swagger.model.TagMetaDto;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class TagMetaDirImpl implements TagMetaDataBaseCustom {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TagMetaDirImpl.class);

    /**
     * Directory utilities.
     */
    private DirectoryUtilities dirtools = null;

    /**
     * Mapper.
     */
    private MapperFacade mapper;

    /**
     * @param dutils
     *            the DirectoryUtilities
     * @param mapper
     */
    public TagMetaDirImpl(DirectoryUtilities dutils, MapperFacade mapper) {
        this.dirtools = dutils;
        this.mapper = mapper;
    }

    /**
     * @param du
     *            the DirectoryUtilities
     */
    public void setDirtools(DirectoryUtilities du) {
        this.dirtools = du;
    }


    /**
     * @param id the String
     * @return TagMetaDto
     */
    @Override
    public TagMetaDto find(String id) {
        Path tagfilepath;
        try {
            tagfilepath = dirtools.getTagMetaFilePath(id);
            log.debug("find uses tag meta file path {}", tagfilepath);
            return dirtools.readTagMetaFile(tagfilepath);
        }
        catch (final CdbServiceException e) {
            log.error("Cannot find tag {} : {}", id, e.getMessage());
        }
        return null;
    }

    /**
     * The method does not access blob data. In the context of the directory based we
     * do not make the difference.
     *
     * @param id the String
     * @return The tag metadata or null.
     */
    @Override
    public TagMetaDto findMetaInfo(String id) {
        return find(id);
    }

    /**
     * @param entity the TagMetaDto
     * @return Either the entity which has been saved or null.
     */
    @Override
    public TagMetaDto save(TagMetaDto entity) {
        return null;
    }

    /**
     * @param entity the TagMetaDto
     * @return Either the entity which has been updated or null.
     */
    @Override
    public TagMetaDto update(TagMetaDto entity) {
        return null;
    }

    /**
     * @param id the String
     * @return
     */
    @Override
    public void delete(String id) {

    }
}
