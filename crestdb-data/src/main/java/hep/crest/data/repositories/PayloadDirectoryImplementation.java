/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.exceptions.CdbInternalException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.utils.DirectoryUtilities;
import hep.crest.swagger.model.PayloadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author formica
 *
 */
public class PayloadDirectoryImplementation {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PayloadDirectoryImplementation.class);

    /**
     * Directory tools.
     */
    private DirectoryUtilities dirtools = null;

    /**
     * Default ctor.
     */
    public PayloadDirectoryImplementation() {
        super();
    }

    /**
     * @param dutils
     *            the DirectoryUtilities
     */
    public PayloadDirectoryImplementation(DirectoryUtilities dutils) {
        super();
        this.dirtools = dutils;
    }

    /**
     * @param du
     *            the DirectoryUtilities
     */
    public void setDirtools(DirectoryUtilities du) {
        this.dirtools = du;
    }

    /**
     * @param hash
     *            the String
     * @return PayloadDto
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    public PayloadDto find(String hash) throws AbstractCdbServiceException {
        final Path payloadpath = dirtools.getPayloadPath();
        final String hashdir = dirtools.hashdir(hash);
        final Path payloadhashpath = Paths.get(payloadpath.toString(), hashdir);
        if (!payloadhashpath.toFile().exists()) {
            throw new CdbNotFoundException("Cannot find hash dir " + payloadhashpath.toString());
        }
        final String filename = hash + ".blob";
        final Path payloadfilepath = Paths.get(payloadhashpath.toString(), filename);
        if (!payloadfilepath.toFile().exists()) {
            throw new CdbNotFoundException("Cannot find file for " + payloadfilepath.toString());
        }

        final StringBuilder buf = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(payloadfilepath,
                dirtools.getCharset())) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug(line);
                buf.append(line);
            }
            final String jsonstring = buf.toString();
            if (jsonstring.isEmpty()) {
                return null;
            }
            return dirtools.getMapper().readValue(jsonstring, PayloadDto.class);
        }
        catch (final IOException x) {
            log.error("IO exception in loading payload for hash {}", hash);
            throw new CdbInternalException("IO error for hash " + hash, x);
        }
    }

    /**
     * @param dto
     *            the PayloadDto
     * @return String
     */
    public String save(PayloadDto dto) {

        try {
            final String hash = dto.getHash();
            final Path payloadpath = dirtools.getPayloadPath();

            final String hashdir = dirtools.hashdir(hash);
            final String payloadfilename = hash + ".blob";

            final Path payloadhashdir = Paths.get(payloadpath.toString(), hashdir);
            if (!payloadhashdir.toFile().exists()) {
                Files.createDirectories(payloadhashdir);
            }
            final Path payloadfilepath = Paths.get(payloadhashdir.toString(), payloadfilename);
            if (!payloadfilepath.toFile().exists()) {
                // The payload does not exists: create the new file.
                Files.createFile(payloadfilepath);
            }
            else {
                // The payload already exists. Return directly the hash.
                return hash;
            }
            final String jsonstr = dirtools.getMapper().writeValueAsString(dto);

            this.writeBuffer(jsonstr, payloadfilepath);
            return hash;
        }
        catch (final RuntimeException | IOException x) {
            log.error("Cannot save payload dto {}", dto);
            throw new CdbInternalException("IO error saving payload for hash " + dto.getHash(), x);
        }
    }

    /**
     * @param jsonstr
     *            the String
     * @param payloadfilepath
     *            the Path
     * @throws IOException
     *             If an Exception occurred
     */
    protected void writeBuffer(String jsonstr, Path payloadfilepath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(payloadfilepath,
                dirtools.getCharset())) {
            writer.write(jsonstr);
        }
        catch (final IOException x) {
            log.error("Cannot write string {} in {}", jsonstr, payloadfilepath);
            throw x;
        }
    }
}
