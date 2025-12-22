package hep.crest.server.controllers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbInternalException;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JsonStreamProcessor {

    /**
     * Thee payload service.
     */
    private PayloadService payloadService;
    /**
     * Mapper.
     */
    private ObjectMapper jsonMapper;

    /**
     * Default ctor.
     *
     * @param payloadService
     * @param iovService
     * @param mapper
     */
    @Autowired
    public JsonStreamProcessor(PayloadService payloadService,
                               IovService iovService,
                               @Qualifier("jacksonMapper") ObjectMapper mapper) {
        this.payloadService = payloadService;
        this.jsonMapper = mapper;
    }

    /**
     * Process a JSON stream.
     *
     * @param jsonInputStream the input stream
     * @param objectType      the object type
     * @param version         the version
     * @param compressionType the compression type
     * @param tag             the tag
     * @return StoreSetDto
     * @throws AbstractCdbServiceException if processing fails
     */
    public StoreSetDto processJsonStream(BufferedInputStream jsonInputStream, String objectType,
                                         String version, String compressionType, String tag) {
        log.info("Start processing JSON stream");

        JsonFactory jsonFactory = configureJsonFactory(jsonMapper.getFactory());
        MemoryMonitor memory = new MemoryMonitor();
        memory.logCurrentUsage("Start parsing json stream");

        try (JsonParser parser = jsonFactory.createParser(jsonInputStream)) {
            skipDocumentPreamble(parser);
            return processMainObject(parser, objectType, version, compressionType, tag, memory);
        }
        catch (IOException | NoSuchAlgorithmException e) {
            log.error("JSON processing failed", e);
            throw new CdbInternalException("JSON processing failed", e);
        }
    }

    /**
     * Skip document preamble until START_OBJECT token is found.
     *
     * @param parser the JSON parser
     * @throws IOException if parsing fails
     */
    private void skipDocumentPreamble(JsonParser parser) throws IOException {
        JsonToken token;
        while ((token = parser.nextToken()) != null) {
            if (token == JsonToken.START_OBJECT) {
                break;
            }
        }
    }

    /**
     * Process the main object content.
     *
     * @param parser          the JSON parser
     * @param objectType      the object type
     * @param version         the version
     * @param compressionType the compression type
     * @param tag             the tag
     * @param memory          the memory monitor
     * @return StoreSetDto with processed data
     * @throws IOException              if parsing fails
     * @throws NoSuchAlgorithmException if algorithm fails
     */
    private StoreSetDto processMainObject(JsonParser parser, String objectType, String version,
                                          String compressionType, String tag, MemoryMonitor memory)
            throws IOException, NoSuchAlgorithmException {
        StoreSetDto setDto = new StoreSetDto();
        List<StoreDto> dtoList = new ArrayList<>();
        int nstored = 0;
        int size = 0;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();

            if ("resources".equals(fieldName)) {
                nstored = processResourcesArray(parser, objectType, version,
                        compressionType, tag, memory);
            }
            else if ("size".equals(fieldName)) {
                parser.nextToken();
                size = parser.getValueAsInt();
                log.info("Data size: {}", size);
            }
            else {
                skipUnknownField(parser);
            }
        }

        validateResults(nstored, size);
        populateResponse(setDto, dtoList, nstored);
        return setDto;
    }

    /**
     * Process resources array from the JSON.
     *
     * @param parser          the JSON parser
     * @param objectType      the object type
     * @param version         the version
     * @param compressionType the compression type
     * @param tag             the tag
     * @param memory          the memory monitor
     * @return number of items stored
     * @throws IOException              if parsing fails
     * @throws NoSuchAlgorithmException if algorithm fails
     */
    private int processResourcesArray(JsonParser parser, String objectType, String version,
                                      String compressionType, String tag,
                                      MemoryMonitor memory)
            throws IOException, NoSuchAlgorithmException {
        parser.nextToken(); // Move to START_ARRAY
        log.info("Processing resources array...");

        int nstored = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            processData(parser, objectType, version, compressionType, tag);
            nstored++;

            if (nstored % 100 == 0) {
                memory.logCurrentUsage("Processed " + nstored + " items");
                memory.checkMemoryUsage();
            }
        }
        return nstored;
    }

    /**
     * Skip unknown field in JSON.
     *
     * @param parser the JSON parser
     * @throws IOException if parsing fails
     */
    private void skipUnknownField(JsonParser parser) throws IOException {
        parser.nextToken();
        parser.skipChildren();
    }

    /**
     * Validate that the number of stored items matches expected size.
     *
     * @param nstored the number of items stored
     * @param size    the expected size
     */
    private void validateResults(int nstored, int size) {
        if (nstored != size) {
            log.warn("Stored {} items but expected {}", nstored, size);
        }
    }

    /**
     * Populate the response DTO.
     *
     * @param setDto  the response DTO
     * @param dtoList the list of stored DTOs
     * @param nstored the number of items stored
     */
    private void populateResponse(StoreSetDto setDto, List<StoreDto> dtoList, int nstored) {
        setDto.setResources(dtoList);
        setDto.size((long) nstored);
        setDto.format("StoreSetDto");
        setDto.datatype("payloads");
    }


    /**
     * Helper method to configure JsonFactory.
     * This method sets size limits and other configurations.
     * Package-private for testability.
     *
     * @param factory JsonFactory to configure
     * @return Configured JsonFactory
     */
    protected JsonFactory configureJsonFactory(JsonFactory factory) {
        // For Jackson 2.18+ (with StreamReadConstraints)
        try {
            factory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
            factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            factory.setStreamReadConstraints(
                    StreamReadConstraints.builder()
                            .maxStringLength(300_000_000) // 300MB
                            .maxNestingDepth(2000)
                            .build()
            );
        }
        catch (Exception e) {
            log.error("Cannot configure JsonFactory: ", e);
            throw new CdbInternalException("Cannot configure JsonFactory", e);
        }
        return factory;
    }

    /**
     * Processes JSON data stream into StoreDto objects with enhanced memory management.
     *
     * @param parser          Configured JsonParser positioned at START_OBJECT
     * @param objectType      Type of object being processed
     * @param version         Data version
     * @param compressionType Compression type if applicable
     * @param tag             Additional tagging information
     */
    protected void processData(JsonParser parser, String objectType, String version,
                               String compressionType, String tag)
            throws NoSuchAlgorithmException, IOException {

        // 1. Memory monitoring setup
        MemoryMonitor memory = new MemoryMonitor();
        memory.logCurrentUsage("Start processing");

        try {
            // 2. Validate parser state
            if (!parser.hasCurrentToken() || parser.currentToken() != JsonToken.START_OBJECT) {
                throw new JsonParseException(parser, "Invalid parser state. Expected START_OBJECT, found: "
                        + (parser.hasCurrentToken() ? parser.currentToken() : "NO_TOKEN"));
            }

            // 3. Create DTO builder for streaming
            if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                StoreDto dto = jsonMapper.readValue(parser, StoreDto.class);
                log.debug("Convert stream to StoreDto at since: {}", dto.getSince());
                payloadService.savePayloadIov(dto, objectType, version, compressionType, tag);
            }
            memory.logFinalUsage();
        }
        catch (JsonParseException e) {
            throw new IOException("JSON parsing failed: " + e.getMessage(), e);
        }
    }
}
