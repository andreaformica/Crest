package hep.crest.server.controllers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
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
     * @param jsonInputStream
     * @param objectType
     * @param version
     * @param compressionType
     * @param tag
     * @return StoreSetDto
     * @throws hep.crest.server.exceptions.AbstractCdbServiceException
     */
    public StoreSetDto processJsonStream(BufferedInputStream jsonInputStream, String objectType,
                                         String version, String compressionType, String tag) {
        // 0. Configure JsonFactory with proper size limits
        JsonFactory jsonFactory = configureJsonFactory(jsonMapper.getFactory());
        // 1. Memory monitoring setup
        MemoryMonitor memory = new MemoryMonitor();
        memory.logCurrentUsage("Start parsing json stream");

        StoreSetDto setDto = new StoreSetDto();
        List<StoreDto> dtoList = new ArrayList<>();
        int nstored = 0;
        int size = 0;

        log.info("Start processing JSON stream");

        try (JsonParser parser = jsonFactory.createParser(jsonInputStream)) {
            // 2. Skip document preamble until we find the START_OBJECT token
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                if (token == JsonToken.START_OBJECT) {
                    break; // Found the start of our main object
                }
                // Alternative: Skip other preamble tokens if needed
                log.trace("Skipping token: {}", token);
            }

            // 3. Process the main object content
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.currentName();

                if ("resources".equals(fieldName)) {
                    // 4. Process resources array
                    parser.nextToken(); // Move to START_ARRAY
                    log.info("Processing resources array...");

                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        // Option A: for complex objects we should read a tree.
                        // Option B: For simple objects (more memory efficient).
                        processData(parser, objectType, version, compressionType, tag);
                        nstored++;
                        // Periodic memory check
                        if (nstored % 100 == 0) {
                            memory.logCurrentUsage("Processed " + nstored + " items");
                            memory.checkMemoryUsage();
                        }
                    }
                }
                else if ("size".equals(fieldName)) {
                    parser.nextToken();
                    size = parser.getValueAsInt();
                    log.info("Data size: {}", size);
                }
                else {
                    // Skip unknown fields
                    parser.nextToken();
                    parser.skipChildren();
                }
            }
            // 5. Validate results
            if (nstored != size) {
                log.warn("Stored {} items but expected {}", nstored, size);
            }

            setDto.setResources(dtoList);
            setDto.size((long) nstored);
            setDto.format("StoreSetDto");
            setDto.datatype("payloads");
        }
        catch (IOException | NoSuchAlgorithmException e) {
            log.error("Failed after processing {} items. Size: {}", nstored, size, e);
            throw new CdbInternalException("JSON processing failed at item " + nstored, e);
        }
        return setDto;
    }

    /**
     * Helper method to configure JsonFactory.
     * This method sets size limits and other configurations.
     *
     * @param factory JsonFactory to configure
     * @return Configured JsonFactory
     */
    private JsonFactory configureJsonFactory(JsonFactory factory) {
        // For Jackson 2.18+ (with StreamReadConstraints)
        try {
            factory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
            factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            factory.setStreamReadConstraints(
                    StreamReadConstraints.builder()
                            .maxStringLength(300_000_000) // 100MB
                            .maxNestingDepth(2000)
                            .build()
            );
        }
        catch (Exception e) {
            // Fallback for older Jackson versions
            factory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
            factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

            // Alternative size limits for older versions
            try {
                factory.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                factory.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            }
            catch (Exception ignored) {
            }
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
                throw new IOException("Invalid parser state. Expected START_OBJECT, found: "
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
            throw new IOException("JSON parsing failed : ", e);
        }
    }
}
