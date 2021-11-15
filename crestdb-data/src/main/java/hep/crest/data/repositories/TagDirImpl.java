package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbInternalException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.pojo.Tag;
import hep.crest.data.utils.DirectoryUtilities;
import hep.crest.swagger.model.TagDto;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TagDirImpl implements ITagCrud {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TagDirImpl.class);

    /**
     * Directory utilities.
     */
    private DirectoryUtilities dirtools = null;

    /**
     * Mapper.
     */
    private MapperFacade mapper;

    /**
     * @param dutils the DirectoryUtilities
     * @param mapper
     */
    public TagDirImpl(DirectoryUtilities dutils, MapperFacade mapper) {
        this.dirtools = dutils;
        this.mapper = mapper;
    }

    /**
     * @param du the DirectoryUtilities
     */
    public void setDirtools(DirectoryUtilities du) {
        this.dirtools = du;
    }


    /**
     * @param entity the Tag
     * @return Tag
     */
    @Override
    public Tag save(Tag entity) {
        final String tagname = entity.name();
        try {
            // Create the tag directory if it does not exists.
            final Path tagpath = dirtools.createIfNotexistsTag(tagname);
            if (tagpath != null) {
                // Remove tag file if exists.
                final Path filepath = Paths.get(tagpath.toString(), dirtools.getTagfile());
                Files.deleteIfExists(filepath);
                // Create tag file if does not exists.
                if (!filepath.toFile().exists()) {
                    Files.createFile(filepath);
                }
                // Map to dto.
                TagDto dto = mapper.map(entity, TagDto.class);
                final String jsonstr = dirtools.getMapper().writeValueAsString(dto);
                // Write the tag file using the json serialization.
                writeTagFile(jsonstr, filepath);
                return entity;
            }
        }
        catch (final RuntimeException | IOException x) {
            log.error("Cannot save tag {}", entity);
            throw new CdbSQLException("Cannot save tag entity " + entity.name(), x);
        }
        return null;
    }

    /**
     * The method does not access blob data.
     *
     * @param name the String
     * @return The tag metadata or null.
     */
    @Override
    public Tag findByName(String name) {
        Path tagfilepath;
        try {
            tagfilepath = dirtools.getTagFilePath(name);
            log.debug("findByName uses tag file path {}", tagfilepath);
            return readTagFile(tagfilepath);
        }
        catch (AbstractCdbServiceException e) {
            log.error("Error in findByName using tag {}", name);
            throw new CdbNotFoundException("Error in finding tag by name " + name, e);
        }
    }

    /**
     * @param id the String
     */
    @Override
    public void deleteById(String id) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove an iov using the entity.
     *
     * @param entity
     */
    @Override
    public void delete(Tag entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param name the String
     * @return List<Tag>
     */
    @Override
    public List<Tag> findByNameLike(String name) {
        final List<String> filteredByNameList;
        // Search in the tag directories for names matching the input string.
        filteredByNameList = dirtools.getTagDirectories().stream().filter(x -> x.matches(name))
                .collect(Collectors.toList());
        return filteredByNameList.stream().map(this::findOne).collect(Collectors.toList());
    }

    /**
     * Find all tags in the backend.
     *
     * @return List of tags.
     */
    @Override
    public List<Tag> findAll() {
        List<String> tagnames;
        tagnames = dirtools.getTagDirectories();
        return tagnames.stream().map(this::findOne).collect(Collectors.toList());
    }

    /**
     * @param id the String
     * @return Tag
     */
    @Override
    public Tag findOne(String id) {
        return findByName(id);
    }

    /**
     * @param tagfilepath the Path
     * @return TagDto
     */
    protected Tag readTagFile(Path tagfilepath) {
        final StringBuilder buf = new StringBuilder();
        // Open a buffered reader and start reading tag file.
        try (BufferedReader reader = Files.newBufferedReader(tagfilepath, dirtools.getCharset())) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("Reading line from file {}", line);
                buf.append(line);
            }
            final String jsonstring = buf.toString();
            final TagDto readValue = dirtools.getMapper().readValue(jsonstring, TagDto.class);
            // Create an entity from a Dto.
            final Tag entity = mapper.map(readValue, Tag.class);
            log.debug("Parsed json to get tag object {} ", entity);
            return entity;
        }
        catch (final IOException e) {
            log.error("Error in reading tag file from path {}", tagfilepath);
            throw new CdbInternalException("Error reading tag file " + tagfilepath, e);
        }
    }

    /**
     * @param jsonstr  the String
     * @param filepath the Path
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    protected void writeTagFile(String jsonstr, Path filepath) throws AbstractCdbServiceException {
        try (BufferedWriter writer = Files.newBufferedWriter(filepath, dirtools.getCharset())) {
            writer.write(jsonstr);
        }
        catch (final IOException x) {
            throw new CdbInternalException("Cannot write " + jsonstr + " in JSON file", x);
        }
    }
}
