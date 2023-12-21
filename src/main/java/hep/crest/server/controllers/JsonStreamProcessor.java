package hep.crest.server.controllers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
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
     * Thee iov service.
     */
    private IovService iovService;
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
        this.iovService = iovService;
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
     */
    @Transactional
    public StoreSetDto processJsonStream(InputStream jsonInputStream, String objectType,
                                         String version, String compressionType, String tag) {
        JsonFactory jsonFactory = jsonMapper.getFactory();
        StoreSetDto setDto = new StoreSetDto();
        List<StoreDto> dtoList = new ArrayList<>();
        log.info("Start processing JSON stream: {}", jsonInputStream);
        try (JsonParser parser = jsonFactory.createParser(jsonInputStream)) {
            // Start creating objects to store from JSON stream.
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String token = parser
                        .getCurrentName();
                log.info("token is: {}", token);
                if ("resources".equals(token)) {
                    parser.nextToken(); //next token contains value
                    log.info("loading resources array...");
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        StoreDto dto = processData(parser, objectType, version, compressionType, tag);
                        dtoList.add(dto);
                    }
                }
                if ("size".equals(token)) {
                    parser.nextToken();
                    Integer size = parser.getValueAsInt();
                    log.info("Data size : {}", size);
                }
            }
            parser.close();
            setDto.setResources(dtoList);
            setDto.size((long) dtoList.size());
            setDto.format("StoreSetDto");
            setDto.datatype("payloads");
        }
        catch (IOException e) {
            log.error("IOException while reading input JSON stream with payload: {}", e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return setDto;
    }

    /**
     * @param parser
     * @param objectType
     * @param version
     * @param compressionType
     * @param tag
     * @return StoreDto
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    protected StoreDto processData(JsonParser parser, String objectType, String version,
                                   String compressionType, String tag)
            throws NoSuchAlgorithmException,
            IOException {
        StoreDto outdto = new StoreDto();
        log.info("process data for token representing a storedto");
        long maxMemory = Runtime.getRuntime().maxMemory();
        long allocatedMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = allocatedMemory - freeMemory;
        log.info("Used / max memory is bytes: {} / {}", usedMemory, maxMemory);

        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            StoreDto dto = jsonMapper.readValue(parser, StoreDto.class);
            log.debug("Convert stream to StoreDto at since: {}", dto.getSince());
            outdto = payloadService.savePayloadIov(dto, objectType, version, compressionType, tag);
        }
        return outdto;
    }
}
