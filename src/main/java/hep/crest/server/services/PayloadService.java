/**
 *
 */
package hep.crest.server.services;

import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.PayloadInfoData;
import hep.crest.server.data.repositories.IovRepository;
import hep.crest.server.data.repositories.PayloadDataRepository;
import hep.crest.server.data.repositories.PayloadInfoDataRepository;
import hep.crest.server.data.repositories.PayloadRepository;
import hep.crest.server.data.repositories.TagRepository;
import hep.crest.server.data.repositories.args.PayloadQueryArgs;
import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbBadRequestException;
import hep.crest.server.exceptions.CdbInternalException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.exceptions.ConflictException;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author aformic
 */
@Service
@Slf4j
public class PayloadService {

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
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;

    /**
     * Repository.
     */
    @Autowired
    private PayloadRepository payloadRepository;
    /**
     * Repository.
     */
    @Autowired
    private PayloadDataRepository payloadDataRepository;
    /**
     * Repository.
     */
    @Autowired
    private PayloadInfoDataRepository payloadInfoDataRepository;

    /**
     * Select Payloads.
     *
     * @param args
     * @param preq
     * @return Page of Payload
     */
    public Page<Payload> selectPayloadList(PayloadQueryArgs args, Pageable preq) {
        Page<Payload> entitylist = null;
        if (preq == null) {
            String sort = "insertionTime:DESC";
            preq = prh.createPageRequest(0, 1000, sort);
        }
        entitylist = payloadRepository.findPayloadsList(args, preq);
        log.trace("Retrieved list of payloads {}", entitylist);
        return entitylist;
    }

    /**
     * @param tag  the String tag name
     * @param hash the String payload hash
     * @return String
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    @Transactional
    public String removePayload(String tag, String hash) throws AbstractCdbServiceException {
        final Optional<Payload> opt = payloadRepository.findById(hash);
        if (opt.isEmpty()) {
            throw new CdbNotFoundException("Cannot find payload for hash " + hash);
        }
        Boolean canremove = Boolean.TRUE;
        List<Iov> iovwithhash = iovRepository.findByPayloadHash(hash);
        Integer niovs = (iovwithhash != null) ? iovwithhash.size() : 0;
        log.debug("Found list of {} IOVs for hash {}", niovs, hash);
        if (niovs > 1) {
            log.debug("The hash {} is associated to more than one iov...remove only if tag name "
                      + "is the same", hash);
            for (Iov iov : iovwithhash) {
                if (!iov.id().tagName().equals(tag)) {
                    log.info("Cannot remove payload hash {}: found iov in tag {}", hash, iov.id());
                    canremove = Boolean.FALSE;
                }
            }
        }
        if (Boolean.TRUE.equals(canremove)) {
            log.info("Remove payload for hash {} in tag {}", hash, tag);
            payloadRepository.deleteById(hash);
            payloadDataRepository.deleteData(hash);
            payloadDataRepository.deleteById(hash);
            payloadInfoDataRepository.deleteById(hash);
            return hash;
        }
        return "skip";
    }


    /**
     * @param hash  the String
     * @param sinfo the String
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    @Transactional
    public void updatePayloadMetaInfo(String hash, String sinfo)
            throws AbstractCdbServiceException {
        PayloadInfoData entity = payloadInfoDataRepository.findById(hash).orElseThrow(
                () -> new CdbNotFoundException("Cannot find streamer info for hash " + hash)
        );
        entity.streamerInfo(sinfo.getBytes(StandardCharsets.UTF_8));
        payloadInfoDataRepository.save(entity);
    }

    /**
     * The return type for Lob Streams.
     */
    @Data
    public static final class LobStream implements AutoCloseable {
        /**
         * The key to the LOB.
         */
        private final String key;
        /**
         * The inputstream to read it.
         */
        private final InputStream inputStream;

        @Override
        public void close() throws IOException {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * @param hash   the String
     * @param source the LOB type
     * @return LobStream
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    public LobStream getLobData(String hash, String source) throws CdbNotFoundException {
        switch (source) {
            case "BLOB":
                return new LobStream(hash, payloadDataRepository.findData(hash));
            case "STREAMER":
                byte[] si = getPayloadStreamerInfo(hash);
                return new LobStream(hash, new ByteArrayInputStream(si));
            default:
                throw new CdbBadRequestException("Cannot process Lob data for source " + source);
        }
    }

    /**
     * @param hash the String
     * @return InputStream
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    @Transactional
    public Payload getPayload(String hash) throws CdbNotFoundException {
        return payloadRepository.findById(hash).orElseThrow(
                () -> new CdbNotFoundException("Cannot find payload for hash " + hash)
        );
    }

    /**
     * @param hash the String
     * @return Boolean
     */
    public Boolean exists(String hash) {
        return payloadRepository.findById(hash).isPresent();
    }

    /**
     * @param hash the String
     * @return InputStream
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    //@//Transactional
    public byte[] getPayloadStreamerInfo(String hash) throws CdbNotFoundException {
        PayloadInfoData entity = payloadInfoDataRepository.findById(hash).orElseThrow(
                () -> new CdbNotFoundException("Cannot find payload streamer info for hash " + hash)
        );
        return entity.streamerInfo();
    }

    /**
     * @param entity   the Payload
     * @param is       the input stream
     * @param streamer the streamer info object
     * @return Payload
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    public Payload insertPayload(Payload entity, InputStream is, PayloadInfoData streamer)
            throws AbstractCdbServiceException {
        log.debug("Save payload {}", entity);
        if (entity.size() == null) {
            throw new CdbBadRequestException("Cannot store payload without size being set");
        }
        // Check if exists
        Optional<Payload> exists = payloadRepository.findById(entity.hash());
        if (exists.isPresent()) {
            log.warn("Payload already exists for hash {}: send back the saved instance",
                    entity.hash());
            // Having the existing instance will allow to store IOVs.
            return exists.get();
        }
        // Store the payload dto
        final Payload saved = payloadRepository.save(entity);
        payloadDataRepository.saveData(entity.hash(), is, entity.size());
        payloadInfoDataRepository.save(streamer);
        log.debug("Saved payload and related entity: {}", saved);
        return saved;
    }

    /**
     * Save IOV and Payload in one request.
     *
     * @param dataList
     * @return StoreSetDto
     * @throws AbstractCdbServiceException
     */
    @Transactional(rollbackOn = {Exception.class})
    public StoreSetDto saveAll(List<StorableData> dataList)
            throws AbstractCdbServiceException {
        log.debug("Create iov and payload from list of {} elements", dataList.size());
        StoreSetDto setdto = new StoreSetDto();
        String tagname = null;
        for (StorableData data : dataList) {
            StoreDto dto = new StoreDto();
            // Store payload, data content and streamer info
            Iov iov = data.iov();
            log.debug("Entry for iov {}", iov);
            Payload entity = data.payload();
            PayloadInfoData streamer = data.payloadInfoData();
            Map<String, Object> info = data.streamsMap();
            // Get the name of the file where the data were temporarily stored.
            String uploadedFile = (String) info.get("uploadedFile");
            log.debug("Read stream from uploaded file : {}", uploadedFile);
            // Access file, set the length and open an input stream
            try (InputStream is = new FileInputStream(uploadedFile);
                 FileChannel tempchan = FileChannel.open(Paths.get(uploadedFile))) {
                // We set the size of the payload here.
                // In case this is null, the payload will not be stored.
                entity.size((int) tempchan.size());
                Payload saved = insertPayload(entity, is, streamer);
                log.debug("Payload saved is : {}", saved);
                // Now insert IOV. The method will perform many verifications.
                iov.payloadHash(saved.hash());
                if (tagname == null) {
                    tagname = iov.tag().name();
                }
                log.debug("Saving iov {} in tag {}", iov, tagname);
                Iov savedIov = iovService.storeIov(iov);
                dto.since(new BigDecimal(savedIov.id().since())).hash(savedIov.payloadHash());
                dto.data(saved.objectType() + "; " + saved.objectName());
                setdto.addResourcesItem(dto);
            }
            catch (final ConflictException e) {
                log.warn("Payload insertion problem for hash {}: {}", entity.hash(), e);
                throw new ConflictException("Payload hash already exists " + entity.hash() + " "
                                            + "since " + iov.id().since() + " in tag " + tagname);
            }
            catch (final IOException e) {
                log.warn("Payload insertion problem for hash {}: {}", entity.hash(), e);
                throw new CdbInternalException("Cannot read payload file " + uploadedFile);
            }
            finally {
                try {
                    if (uploadedFile != null) {
                        Files.deleteIfExists(Paths.get(uploadedFile));
                    }
                    log.debug("Removed temporary file");
                }
                catch (IOException e) {
                    log.error("Cannot delete temporary file " + uploadedFile);
                }
            }
        }
        return setdto;
    }
}
