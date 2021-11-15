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
import hep.crest.data.repositories.PayloadDataBaseCustom;
import hep.crest.swagger.model.HTTPResponse;
import hep.crest.swagger.model.IovDto;
import hep.crest.swagger.model.PayloadDto;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author aformic
 *
 */
@Service
public class PayloadService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PayloadService.class);

    /**
     * Repository.
     */
    @Autowired
    private IovService iovService;
    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    /**
     * Repository.
     */
    @Autowired
    @Qualifier("payloaddatadbrepo")
    private PayloadDataBaseCustom payloaddataRepository;

    /**
     * @param hash
     *            the String
     * @return PayloadDto
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Transactional
    public PayloadDto getPayload(String hash) throws CdbNotFoundException {
        final PayloadDto pyld = payloaddataRepository.find(hash);
        if (pyld == null) {
            throw new CdbNotFoundException("Cannot find payload dto for hash " + hash);
        }
        return pyld;
    }

    /**
     * @param tag
     *            the String tag name
     * @param hash
     *            the String payload hash
     * @return String
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Transactional
    public String removePayload(String tag, String hash) throws AbstractCdbServiceException {
        final PayloadDto pyld = payloaddataRepository.find(hash);
        if (pyld == null) {
            throw new CdbNotFoundException("Cannot find payload dto for hash " + hash);
        }
        Boolean canremove = Boolean.TRUE;
        List<Iov> iovwithhash = iovService.findIovsWithHash(hash);
        if (iovwithhash.size() > 1) {
            log.debug("The hash {} is associated to more than one iov...remove only if tag name is the same", hash);
            for (Iov iov : iovwithhash) {
                if (!iov.id().tagName().equals(tag)) {
                    log.info("Cannot remove payload hash {}: found iov in tag {}", hash, iov.id());
                    canremove = Boolean.FALSE;
                }
            }
        }
        if (canremove) {
            log.info("Remove payload for hash {} in tag {}", hash, tag);
            payloaddataRepository.delete(hash);
            return hash;
        }
        return "skip";
    }


    /**
     * @param hash
     *            the String
     * @return PayloadDto
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Transactional
    public PayloadDto getPayloadMetaInfo(String hash) throws CdbNotFoundException {
        final PayloadDto pyld = payloaddataRepository.findMetaInfo(hash);
        if (pyld == null) {
            throw new CdbNotFoundException("Cannot find payload meta data for hash " + hash);
        }
        return pyld;
    }

    /**
     * @param hash
     *            the String
     * @param sinfo
     *            the String
     * @return int
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Transactional
    public int updatePayloadMetaInfo(String hash, String sinfo) throws AbstractCdbServiceException {
        int nrows = payloaddataRepository.updateMetaInfo(hash, sinfo);
        if (nrows <= 0) {
            throw new CdbNotFoundException("Cannot update payload meta data for hash " + hash);
        }
        else if (nrows > 1) {
            throw new CdbInternalException("Too many rows updated...rollback");
        }
        return nrows;
    }

    /**
     * @param hash
     *            the String
     * @return InputStream
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Transactional
    public InputStream getPayloadData(String hash) throws CdbNotFoundException {
        final InputStream is = payloaddataRepository.findData(hash);
        if (is == null) {
            throw new CdbNotFoundException("Cannot find payload data for hash " + hash);
        }
        return is;
    }

    /**
     * @param dto
     *            the PayloadDto
     * @return PayloadDto
     * @throws ConflictException
     *             If an Exception occurred
     */
    @Transactional
    public PayloadDto insertPayload(PayloadDto dto) throws ConflictException {
        log.debug("Save payload dto {}", dto);
        if (dto.getSize() == null) {
            dto.setSize(dto.getData().length);
        }
        // Store the payload dto
        final PayloadDto saved = payloaddataRepository.save(dto);
        log.debug("Saved entity: {}", saved);
        return saved;
    }

    /**
     * @param dto
     *            the PayloadDto
     * @param is
     *            the InputStream
     * @return PayloadDto
     * @throws ConflictException
     *             If an Exception occurred
     */
    public PayloadDto insertPayloadAndInputStream(PayloadDto dto, InputStream is) throws ConflictException {
        log.debug("Save payload {} creating blob from inputstream...", dto);
        final PayloadDto saved = payloaddataRepository.save(dto, is);
        log.debug("Saved entity: {}", saved);
        return saved;
    }


    /**
     * Save IOV and Payload in one request.
     *
     * @param dto
     * @param pdto
     * @param filename
     * @return HTTPResponse
     * @throws AbstractCdbServiceException
     */
    @Transactional(rollbackOn = {AbstractCdbServiceException.class})
    public HTTPResponse saveIovAndPayload(IovDto dto, PayloadDto pdto, String filename)
            throws AbstractCdbServiceException {
        log.debug("Create iov and payload with hash {}", dto.getPayloadHash());
        try {
            PayloadDto saved = null;
            if (filename != null && pdto != null) {
                saved = insertPayloadFromFile(filename, pdto, dto);
            }
            else if (pdto != null) {
                saved = insertPayloadFromDto(pdto, dto);
            }
            else {
                log.warn("Skip payload insertion, only Iov will be inserted for hash {}", dto.getPayloadHash());
            }
            String tagname = dto.getTagName();
            Iov entity = mapper.map(dto, Iov.class);
            entity.tag(new Tag().name(tagname));
            log.debug("Inserting iov {}", entity);
            final Iov savediov = iovService.insertIov(entity);
            IovDto saveddto = mapper.map(savediov, IovDto.class);
            saveddto.tagName(tagname);
            dto.tagName(tagname);
            log.debug("Saved iov {} ", saveddto);
            // Everything ok, so send back a "created" status code.
            log.debug("Created payload {} and iov {} ", saved, savediov);
            return new HTTPResponse().code(Response.Status.CREATED.getStatusCode())
                    .id(saveddto.getPayloadHash()).message("Iov created in tag "
                                                           + saveddto.getTagName() + " @ " + saveddto.getSince());
        }
        catch (IOException ex) {
            log.error("IO Exception in reading payload data: {}", ex.getMessage());
            throw new CdbInternalException("IO Exception in reading payload data for "
                                           + dto.getTagName() + " with hash " + dto.getPayloadHash());
        }
        catch (RuntimeException e) {
            log.error("Runtime exception in method saveIovAndPayload: {}", e.getMessage());
            throw e;
        }
        finally {
            log.debug("Clean up files when non null...");
            try {
                if (filename != null) {
                    Files.deleteIfExists(Paths.get(filename));
                }
            }
            catch (IOException e) {
                log.error("Cannot delete temporary file: {}", e.getMessage());
            }
            log.debug("Removed temporary file");
        }
    }

    /**
     *
     * @param filename
     * @param pdto
     * @param dto
     * @return PayloadDto
     * @throws IOException
     * @throws AbstractCdbServiceException
     */
    protected PayloadDto insertPayloadFromFile(String filename, PayloadDto pdto, IovDto dto)
            throws AbstractCdbServiceException, IOException {
        try (InputStream is = new FileInputStream(filename);
             FileChannel tempchan = FileChannel.open(Paths.get(filename));) {
            pdto.size((int) tempchan.size());
            return insertPayloadAndInputStream(pdto, is);
        }
        catch (final ConflictException | FileNotFoundException e) {
            log.warn("Payload hash duplication, will not store hash {}: {}", dto.getPayloadHash(), e);
            return getPayloadMetaInfo(dto.getPayloadHash());
        }
    }

    /**
     *
     * @param pdto
     * @param dto
     * @return PayloadDto
     * @throws ConflictException
     */
    protected PayloadDto insertPayloadFromDto(PayloadDto pdto, IovDto dto) throws ConflictException {
        // Set payload size.
        pdto.size(pdto.getData().length);
        return insertPayload(pdto);
    }
}
