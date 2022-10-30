package hep.crest.server.swagger.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.data.config.CrestProperties;
import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbBadRequestException;
import hep.crest.data.exceptions.CdbInternalException;
import hep.crest.data.exceptions.PayloadEncodingException;
import hep.crest.data.handlers.PayloadHandler;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.PayloadData;
import hep.crest.data.pojo.PayloadInfoData;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.PayloadDataRepository;
import hep.crest.data.repositories.PayloadInfoDataRepository;
import hep.crest.data.repositories.PayloadRepository;
import hep.crest.server.caching.CachingPolicyService;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.controllers.SimpleLobStreamerProvider;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import hep.crest.server.services.StorableData;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.server.swagger.api.PayloadsApiService;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.PayloadDto;
import hep.crest.server.swagger.model.PayloadSetDto;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rest endpoint for payloads.
 *
 * @author formica
 */
@Component
@Slf4j
public class PayloadsApiServiceImpl extends PayloadsApiService {

    /**
     * Set separator.
     */
    private static final String SLASH = File.pathSeparator.equals(":") ? "/" : File.pathSeparator;
    /**
     * Maximum number of files.
     */
    private static final int MAX_FILE_UPLOAD = 1000;
    /**
     * The list of payload types for download.
     */
    private static final List<String> payloadlist = Arrays.asList("png", "svg", "json", "xml", "csv", "txt", "tgz",
            "gz", "pdf");
    /**
     * Service.
     */
    @Autowired
    private PayloadService payloadService;
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
     * Service.
     */
    @Autowired
    private IovService iovService;
    /**
     * Service.
     */
    @Autowired
    private TagService tagService;
    /**
     * Service.
     */
    @Autowired
    private CachingPolicyService cachesvc;
    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;
    /**
     * Mapper.
     */
    @Inject
    private ObjectMapper jacksonMapper;

    /**
     * Helper.
     */
    @Autowired
    private EntityDtoHelper edh;

    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    //     @CacheControlCdb("public, max-age=604800") : this has to be set on the API class itself.
    //     For the moment we decide to use the cachecontrol filter (if active) via the method
    //     name definition, by looking for the annotation @Path
    @Override
    public Response getPayload(String hash, String format, SecurityContext securityContext,
                               UriInfo info) {
        log.info(
                "PayloadRestController processing request to download payload {} using format {}",
                hash, format);
        // Get only metadata from the payload.
        final Payload entity = payloadService.getPayload(hash);
        final String ptype = entity.objectType();
        log.debug("Found metadata {}", entity);
        // Get the media type. It utilize the objectType field.
        final MediaType media_type = getMediaType(ptype);

        // Set caching policy depending on snapshot argument
        // this is filling a mag-age parameter in the header
        final CacheControl cc = cachesvc.getPayloadCacheControl();
        StreamingOutput streamingOutput = edh.makeStreamingOutputFromLob(
                new SimpleLobStreamerProvider(hash, format) {
                    @Override
                    public InputStream getInputStream() {
                        PayloadService.LobStream lob = payloadService.getLobData(hash, format);
                        return lob.getInputStream();
                    }
                }
        );
        log.debug("Send back the stream....");
        // Get media type
        final String rettype = media_type.toString();
        // Get extension
        final String ext = getExtension(ptype);
        final String fname = hash + "." + ext;
        // Set the content type in the response, and the file name as well.
        return Response.ok(streamingOutput) /// MediaType.APPLICATION_JSON_TYPE)
                .header("Content-type", rettype)
                .header("Content-Disposition", "Inline; filename=\"" + fname + "\"")
                // .header("Content-Length", new
                // Long(f.length()).toString())
                .cacheControl(cc)
                .build();
    }

    @Override
    public Response storePayloadBatch(String tag, String jsonstoreset, String xCrestPayloadFormat,
                                      List<FormDataBodyPart> filesBodypart, String objectType, String compressionType,
                                      String version, BigDecimal endtime, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        this.log.info(
                "PayloadRestController processing request to store payload batch in tag {} with multi-iov ",
                tag);
        try {
            // Read input FormData as an IovSet object.
            if (tag == null || jsonstoreset == null) {
                throw new CdbBadRequestException(
                        "Cannot upload payload in batch mode : form is missing a field, " + tag + " - " + jsonstoreset);
            }
            StoreSetDto storeset = jacksonMapper.readValue(jsonstoreset, StoreSetDto.class);
            log.info("Batch insertion of {} iovs", storeset.getSize());
            // use to send back a NotFound if the tag does not exists.
            tagService.findOne(tag);
            // Add object type.
            if (objectType == null) {
                objectType = storeset.getDatatype();
            }
            // Add version.
            if (version == null) {
                version = "default";
            }
            // Set default for payload format.
            if (xCrestPayloadFormat == null && filesBodypart != null) {
                xCrestPayloadFormat = "FILE";
            }
            StoreSetDto outdto = null;
            if ("FILE".equalsIgnoreCase(xCrestPayloadFormat)) {
                // Check that number of files is not too much.
                if (filesBodypart == null) {
                    throw new CdbBadRequestException("Cannot use header FILE with empty list of files");
                }
                if (filesBodypart.size() > MAX_FILE_UPLOAD) {
                    final String msg = "Too many files attached to the request...> MAX_FILE_UPLOAD = "
                                       + MAX_FILE_UPLOAD;
                    throw new CdbBadRequestException("Too many files uploaded : more than " + MAX_FILE_UPLOAD);
                }
                // Only the payload format FILE is allowed here.
                // This was created to eventually merge with other methods later on.
                outdto = storeData(storeset, tag, objectType, version, filesBodypart);
            }
            else if ("JSON".equalsIgnoreCase(xCrestPayloadFormat)) {
                outdto = storeData(storeset, tag, objectType, version, null);
            }
            else {
                throw new CdbBadRequestException("Bad header parameter: " + xCrestPayloadFormat);
            }
            // Change the end time in the tag.
            Tag tagEntity = tagService.findOne(tag);
            tagEntity.endOfValidity((endtime != null) ? endtime.toBigInteger() : BigInteger.ZERO);
            tagEntity.modificationTime(Instant.now().toDate());
            tagService.updateTag(tagEntity);

            return Response.status(Response.Status.CREATED).entity(outdto).build();
        }
        catch (RuntimeException | IOException e) {
            log.error("Runtime exception while storing iovs and payloads....");
            throw new CdbInternalException(e);
        }
    }


    @Override
    public Response storePayloadOne(String tag, String jsonstore, String xCrestPayloadFormat,
                                    List<FormDataBodyPart> filesBodypart, String objectType, String compressionType,
                                    String version, BigDecimal endtime, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        log.info(
                "PayloadRestController processing request to store one payload in tag {} ",
                tag);
        try {
            StoreDto store = jacksonMapper.readValue(jsonstore, StoreDto.class);
            StoreSetDto storeset = new StoreSetDto();
            storeset.addResourcesItem(store);
            String jsonstoreset = jacksonMapper.writeValueAsString(storeset);
            log.info("Using batch method with set: {}", jsonstoreset);
            return storePayloadBatch(tag, jsonstoreset, xCrestPayloadFormat, filesBodypart, objectType, compressionType,
                    version,
                    endtime, securityContext, info);
        }
        catch (RuntimeException | IOException e) {
            throw new CdbInternalException("Exception occurred in one payload insertion");
        }
    }

    /**
     * Store iovs and payload files.
     *
     * @param dtoset         the StoreSetDto
     * @param tag            the String
     * @param objectType     the object type
     * @param version        the version
     * @param filesbodyparts the List<FormDataBodyPart>
     * @return StoreSetDto
     * @throws PayloadEncodingException    If an Exception occurred
     * @throws IOException                 If an Exception occurred
     * @throws AbstractCdbServiceException if an exception occurred in insertion.
     */
    protected StoreSetDto storeData(StoreSetDto dtoset, String tag, String objectType,
                                    String version, List<FormDataBodyPart> filesbodyparts)
            throws IOException, AbstractCdbServiceException {
        final List<StoreDto> iovlist = dtoset.getResources();
        // Loop over iovs found in the Set.
        List<StorableData> storableDataList = new ArrayList<>();
        for (final StoreDto piovDto : iovlist) {
            String filename = null;
            log.info("Store data for entry: {}", piovDto);
            final Map<String, String> sinfomap = new HashMap<>();
            sinfomap.put("format", dtoset.getDatatype());
            sinfomap.put("insertionDate", new Date().toString());
            sinfomap.put("streamerInfo", piovDto.getStreamerInfo());

            // Here we generate objectType and version. We should probably allow for input arguments.
            Payload entity = new Payload().objectType(objectType).hash("none").version(version);
            entity.compressionType("none");
            entity.size(0);
            Iov iov = new Iov();
            IovId iovId = new IovId();
            iovId.since(piovDto.getSince().toBigInteger());
            iovId.tagName(tag);
            iov.tag(new Tag().name(tag));
            iov.id(iovId);
            PayloadData content = new PayloadData();
            PayloadInfoData sinfodata = new PayloadInfoData();
            StorableData data = new StorableData();
            if (filesbodyparts == null) {
                // There are no attached files, so the data field should represent the payload
                byte[] paylodContent = piovDto.getData().getBytes(StandardCharsets.UTF_8);
                log.debug("Use the data string, it represents the payload : length is {}",
                        paylodContent.length);
                entity.size(paylodContent.length);
                String outFilename = generateUploadFilename("inline", tag, iov.id().since());
                log.info("Dump data in file {} to compute the hash", outFilename);
                final String hash = getHash(
                        new ByteArrayInputStream(paylodContent), outFilename);
                iov.payloadHash(hash);
                entity.hash(hash);
                content.hash(hash);
                sinfodata.hash(hash);
                sinfodata.streamerInfo(jacksonMapper.writeValueAsBytes(sinfomap));
                final Map<String, Object> retmap = new HashMap<>();
                retmap.put("uploadedFile", outFilename);
                data.payload(entity).payloadData(content).payloadInfoData(sinfodata);
                data.streamsMap(retmap);
                data.iov(iov);
            }
            else {
                // If there are attached files, then the payload will be loaded from filename.
                log.debug("Use attached file : {}", piovDto.getData());
                final Map<String, Object> retmap = getDocumentStream(piovDto, filesbodyparts);
                filename = (String) retmap.get("file");
                String outFilename = generateUploadFilename(filename, tag, iov.id().since());
                final String hash = getHash((InputStream) retmap.get("stream"), outFilename);
                retmap.put("uploadedFile", outFilename);
                sinfomap.put("filename", filename);
                iov.payloadHash(hash);
                entity.hash(hash);
                content.hash(hash);
                sinfodata.hash(hash);
                sinfodata.streamerInfo(jacksonMapper.writeValueAsBytes(sinfomap));
                data.payload(entity).payloadData(content).payloadInfoData(sinfodata);
                data.streamsMap(retmap);
                data.iov(iov);
            }
            storableDataList.add(data);
        }
        // Store all data
        log.debug("Save IOV and Payload from list of data of size {}", storableDataList.size());
        StoreSetDto storedset = payloadService.saveAll(storableDataList);
        return storedset;
    }

    /**
     * Generate the filename for the upload.
     *
     * @param fileName
     * @param tag
     * @param since
     * @return String
     */
    protected String generateUploadFilename(String fileName, String tag, BigInteger since) {
        String fdetailsname = fileName;
        if (fdetailsname == null || fdetailsname.isEmpty()) {
            // Generate a fake filename.
            fdetailsname = ".blob";
        }
        else {
            // Get the filename from the input request.
            final Path p = Paths.get(fdetailsname);
            fdetailsname = "_" + p.getFileName().toString();
        }
        // Create a temporary file name from tag name and time of validity.
        String genname = cprops.getDumpdir() + SLASH + tag + "_" + since
                         + fdetailsname;
        return genname;
    }

    /**
     * @param fileInputStream the InputStream
     * @param filename        the String
     * @return String. The computed hash from the byte stream.
     * @throws PayloadEncodingException If an Exception occurred
     * @throws IOException              If an Exception occurred
     */
    protected String getHash(InputStream fileInputStream, String filename)
            throws PayloadEncodingException, IOException {

        try (BufferedInputStream bis = new BufferedInputStream(fileInputStream)) {
            if (filename.equals("none")) {
                return PayloadHandler.getHashFromStream(bis);
            }
            return PayloadHandler.saveToFileGetHash(bis, filename);
        }
    }

    @Override
    public Response updatePayload(String hash, Map<String, String> body, SecurityContext securityContext,
                                  UriInfo info) {
        log.info(
                "PayloadRestController processing request for update payload meta information for {}",
                hash);
        // Send a bad request if body is null.
        if (body == null) {
            throw new CdbBadRequestException("Cannot update payload with null body");
        }
        // Search payload.
        Payload entity = payloadService.getPayload(hash);
        String sinfo = null;
        // Loop over map body keys.
        for (final String key : body.keySet()) {
            if ("streamerInfo".equals(key)) {
                // Update description.
                sinfo = body.get(key);
            }
            else {
                log.warn("Ignored key {} in updatePayload: field does not exists", key);
            }
        }
        payloadService.updatePayloadMetaInfo(hash, sinfo);
        PayloadDto dto = mapper.map(entity, PayloadDto.class);
        final PayloadSetDto psetdto = buildSet(dto, hash);
        return Response.ok()
                .header("Content-type", MediaType.APPLICATION_JSON_TYPE.toString())
                .entity(psetdto).build();
    }

    /**
     * @param mddto     the IovDto
     * @param bodyParts the List<FormDataBodyPart>
     * @return Map<String, Object>
     * @throws PayloadEncodingException If an Exception occurred
     */
    protected Map<String, Object> getDocumentStream(StoreDto mddto, List<FormDataBodyPart> bodyParts)
            throws PayloadEncodingException {
        log.debug("Extracting document BLOB for file {}", mddto.getData());
        final Map<String, Object> retmap = new HashMap<>();
        String dtofname = mddto.getData();
        if (dtofname.startsWith("file://")) {
            dtofname = mddto.getData().split("://")[1];
        }
        for (int i = 0; i < bodyParts.size(); i++) {
            final BodyPartEntity test = (BodyPartEntity) bodyParts.get(i).getEntity();
            final String fileName = bodyParts.get(i).getContentDisposition().getFileName();
            log.debug("Search for file {} in input store set", fileName);
            if (dtofname.contains(fileName)) {
                retmap.put("file", fileName);
                retmap.put("stream", test.getInputStream());
            }
        }
        if (retmap.isEmpty()) {
            throw new PayloadEncodingException(
                    "Cannot find file content in form data. File name = " + mddto.getData());
        }
        return retmap;
    }

    /**
     * Utility class to better download payload data using the type.
     *
     * @param ptype the String
     * @return MediaType
     */
    protected MediaType getMediaType(String ptype) {
        MediaType media_type = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        final String comp = ptype.toLowerCase();
        switch (comp) {
            case "png":
                media_type = new MediaType("image", "png");
                break;
            case "svg":
                media_type = MediaType.APPLICATION_SVG_XML_TYPE;
                break;
            case "json":
                media_type = MediaType.APPLICATION_JSON_TYPE;
                break;
            case "xml":
                media_type = MediaType.APPLICATION_XML_TYPE;
                break;
            case "csv":
                media_type = new MediaType("text", "csv");
                break;
            case "txt":
                media_type = MediaType.TEXT_PLAIN_TYPE;
                break;
            case "tgz":
                media_type = new MediaType("application", "x-gtar-compressed");
                break;
            case "gz":
                media_type = new MediaType("application", "gzip");
                break;
            case "pdf":
                media_type = new MediaType("application", "pdf");
                break;
            default:
                break;
        }
        return media_type;
    }

    /**
     * Set file extension in dowload.
     *
     * @param ptype the String
     * @return String
     */
    protected String getExtension(String ptype) {
        String extension = "blob";
        final String comp = ptype.toLowerCase();
        final boolean match = payloadlist.stream().anyMatch(comp::contains);
        if (match) {
            extension = comp;
        }
        return extension;
    }

    /**
     * @param entity the PayloadDto
     * @param hash   the String
     * @return PayloadSetDto
     */
    protected PayloadSetDto buildSet(PayloadDto entity, String hash) {
        final GenericMap map = new GenericMap();
        map.put("hash", hash);
        final PayloadSetDto psetdto = new PayloadSetDto().addResourcesItem(entity);
        psetdto.datatype(entity.getObjectType()).filter(map).size(1L);
        return psetdto;
    }

}
