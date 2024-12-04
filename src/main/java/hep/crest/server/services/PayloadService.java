/**
 *
 */
package hep.crest.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.converters.HashGenerator;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.PayloadInfoData;
import hep.crest.server.data.pojo.Tag;
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
import hep.crest.server.repositories.triggerdb.ITriggerDb;
import hep.crest.server.repositories.triggerdb.UrlComponents;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
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
     * Repository.
     */
    @Autowired
    private ITriggerDb triggerDbService;

    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper jsonMapper;

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
        if (niovs >= 1) {
            log.debug("The hash {} is associated to more than one iov...remove only if tag name "
                      + "is the same", hash);
            for (Iov iov : iovwithhash) {
                if (!iov.getId().getTagName().equals(tag)) {
                    canremove = Boolean.FALSE;
                    break;
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
        try {
            Map<String, String> jsonmap =
                    jsonMapper.readValue(entity.streamerInfo(), Map.class);
            log.info("Retrieved streamer info for hash {}: {}", hash, jsonmap);
            jsonmap.put("streamerInfo", sinfo);
            String replacement = jsonMapper.writeValueAsString(jsonmap);
            log.info("Update streamer info for hash {}: {}", hash, replacement);
            entity.streamerInfo(replacement.getBytes(StandardCharsets.UTF_8));
            payloadInfoDataRepository.save(entity);
        }
        catch (IOException e) {
            log.error("Cannot update streamer info for hash {}: sinfo={} message={}",
                    hash, sinfo, e.getMessage());
            throw new CdbInternalException("Cannot update streamer info for hash " + hash, e);
        }
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
            case "triggerdb":
                return new LobStream(hash, getTriggerData(hash));
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
        if (hash.startsWith("triggerdb")) {
            return new Payload().setHash(hash).setObjectType("triggerdb").setObjectName(
                    "triggerdb");
        }
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
     * Load trigger data using dedicated repository.
     * @param hash
     * @return byte[]
     */
    public InputStream getTriggerData(String hash) {
        UrlComponents components = triggerDbService.parseUrl(hash);
        log.info("Parsed triggerdb url components: {}", components);
        return triggerDbService.getTriggerDBData(components);
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
        if (entity.getSize() == null) {
            throw new CdbBadRequestException("Cannot store payload without size being set");
        }
        // Check if exists
        Optional<Payload> exists = payloadRepository.findById(entity.getHash());
        if (exists.isPresent()) {
            log.warn("Payload already exists for hash {}: send back the saved instance",
                    entity.getHash());
            // Having the existing instance will allow to store IOVs.
            return exists.get();
        }
        // Store the payload dto
        final Payload saved = payloadRepository.save(entity);
        payloadDataRepository.saveData(entity.getHash(), is, entity.getSize());
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
                entity.setSize((int) tempchan.size());
                Payload saved = insertPayload(entity, is, streamer);
                log.debug("Payload saved is : {}", saved);
                // Now insert IOV. The method will perform many verifications.
                iov.setPayloadHash(saved.getHash());
                if (tagname == null) {
                    tagname = iov.getTag().getName();
                }
                log.debug("Saving iov {} in tag {}", iov, tagname);
                Iov savedIov = iovService.storeIov(iov);
                dto.since((savedIov.getId().getSince()).longValue())
                        .setHash(savedIov.getPayloadHash());
                dto.data(saved.getObjectName() + "; " + saved.getObjectName());
                dto.streamerInfo("none");
                setdto.addresourcesItem(dto);
            }
            catch (final ConflictException e) {
                String msg  = "Payload hash already exists " + entity.getHash() + " "
                              + "since " + iov.getId().getSince() + " in tag " + tagname;
                log.warn("Payload insertion conflict: {}", msg);
                throw new ConflictException(msg);
            }
            catch (final IOException e) {
                log.error("Payload insertion problem for hash {}: {}", entity.getHash(), e);
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
        setdto.size((long) setdto.getResources().size());
        setdto.format("StoreSetDto");
        return setdto;
    }


    /**
     * Save IOV and Payload in one request.
     *
     * @param dto the StoreDto
     * @param objectType the object type
     * @param version the version
     * @param compressionType the compression type
     * @param tag the tag name
     *
     * @return StoreDto the store dto of inserted iov.
     * @throws AbstractCdbServiceException
     *              if a CREST exception occurred.
     * @throws NoSuchAlgorithmException
     *              if an Hashing problem occurred.
     * @throws IOException
     *              if a IO exception occurred.
     */
    @Transactional(rollbackOn = {Exception.class})
    public StoreDto savePayloadIov(StoreDto dto, String objectType, String version,
                                   String compressionType, String tag)
            throws AbstractCdbServiceException, NoSuchAlgorithmException, IOException {
        log.debug("Create iov and payload for time {}", dto.getSince());
        // Here we generate objectType and version. We should probably allow for input
        // arguments.
        // Start filling the payload data.
        Payload entity = new Payload()
                .setObjectType(objectType).setHash("none")
                .setCompressionType(compressionType).setVersion(version);
        entity.setSize(0);
        PayloadInfoData sinfodata = new PayloadInfoData();
        sinfodata.streamerInfo(dto.getStreamerInfo().getBytes(StandardCharsets.UTF_8));
        // Initialize the iov entity from the DTO.
        Iov iov = new Iov();
        IovId iovId = new IovId();
        iovId.setSince(BigInteger.valueOf(dto.getSince())).setTagName(tag);
        iov.setId(iovId).setTag(new Tag().setName(tag));
        // Generate the hash from the payload.
        byte[] paylodContent = dto.getData().getBytes(StandardCharsets.UTF_8);
        log.debug("Use the data string, it represents the payload : length is {}",
                paylodContent.length);
        entity.setSize(paylodContent.length);
        final String phash = HashGenerator.sha256Hash(paylodContent);
        iov.setPayloadHash(phash);
        entity.setHash(phash);
        sinfodata.hash(phash);
        InputStream is = new ByteArrayInputStream(paylodContent);
        // Persist the entity using JPA
        this.insertPayload(entity, is, sinfodata);
        Iov savedIov = iovService.storeIov(iov);
        // Close stream
        is.close();
        // Fill summary info to return.
        // This is a "fake" object containing only some meta information.
        StoreDto outDto = new StoreDto();
        outDto.since(savedIov.getId().getSince().longValue());
        outDto.setHash(phash);
        outDto.data("payload_length: " + paylodContent.length);
        outDto.streamerInfo("none");
        return outDto;
    }

}
