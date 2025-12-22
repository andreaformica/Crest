package hep.crest.server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.controllers.JsonStreamProcessor;
import hep.crest.server.exceptions.CdbBadRequestException;
import hep.crest.server.exceptions.CdbInternalException;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import hep.crest.server.swagger.model.StoreSetDto;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@Slf4j
public class TestJsonStreamProcessor {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JsonStreamProcessor jsonStreamProcessor;

    @Autowired
    private PayloadService payloadService;

    @Autowired
    private IovService iovService;

    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper mapper;

    private static final RandomGenerator rnd = new RandomGenerator();
    private static final String TEST_TAG_NAME = "test-json-stream-tag";

    @BeforeEach
    public void setUp() {
        // Create a tag for testing if it doesn't exist
        try {
            initializeTag(TEST_TAG_NAME);
        } catch (Exception e) {
            log.debug("Tag may already exist: {}", e.getMessage());
        }
    }

    private void initializeTag(String tagName) {
        TagDto dto = (TagDto) rnd.generate(TagDto.class);
        dto.name(tagName);
        dto.status(TagDto.StatusEnum.UNLOCKED);
        dto.synchronization(TagDto.SynchronizationEnum.ALL);
        log.info("Store tag : {} ", dto);
        final ResponseEntity<TagDto> response = testRestTemplate
                .postForEntity("/crestapi/tags", dto, TagDto.class);
        log.info("Received response: {}", response);
        assertThat(response.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.CONFLICT);
    }

    @Test
    public void testProcessJsonStreamWithSingleResource() {
        log.info("=======> testProcessJsonStreamWithSingleResource ");

        // Create a valid JSON stream with one resource
        String jsonContent = """
            {
                "resources": [
                    {
                        "hash": "testhash123",
                        "since": 1000,
                        "data": "dGVzdCBkYXRhIGNvbnRlbnQ=",
                        "streamerInfo": "test-streamer"
                    }
                ],
                "size": 1
            }
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        log.info("Result: {}", result);
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getFormat()).isEqualTo("StoreSetDto");
        assertThat(result.getDatatype()).isEqualTo("payloads");
    }

    @Test
    public void testProcessJsonStreamWithMultipleResources() {
        log.info("=======> testProcessJsonStreamWithMultipleResources ");

        // Create a valid JSON stream with multiple resources
        String jsonContent = """
            {
                "resources": [
                    {
                        "hash": "hash1",
                        "since": 1000,
                        "data": "ZGF0YTE=",
                        "streamerInfo": "streamer1"
                    },
                    {
                        "hash": "hash2",
                        "since": 2000,
                        "data": "ZGF0YTI=",
                        "streamerInfo": "streamer2"
                    },
                    {
                        "hash": "hash3",
                        "since": 3000,
                        "data": "ZGF0YTM=",
                        "streamerInfo": "streamer3"
                    }
                ],
                "size": 3
            }
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        log.info("Result: {}", result);
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(3);
    }

    @Test
    public void testProcessJsonStreamWithEmptyResources() {
        log.info("=======> testProcessJsonStreamWithEmptyResources ");

        // Create a valid JSON stream with empty resources array
        String jsonContent = """
            {
                "resources": [],
                "size": 0
            }
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        log.info("Result: {}", result);
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(0);
    }

    @Test
    public void testProcessJsonStreamWithExtraFields() {
        log.info("=======> testProcessJsonStreamWithExtraFields ");

        // Create a JSON stream with extra fields that should be skipped
        String jsonContent = """
            {
                "extraField": "shouldBeIgnored",
                "resources": [
                    {
                        "hash": "hashextra",
                        "since": 5000,
                        "data": "ZXh0cmE=",
                        "streamerInfo": "extra-streamer"
                    }
                ],
                "anotherExtra": {"nested": "value"},
                "size": 1
            }
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        log.info("Result: {}", result);
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(1);
    }

    @Test
    public void testProcessJsonStreamWithInvalidJson() {
        log.info("=======> testProcessJsonStreamWithInvalidJson ");

        // Create an invalid JSON stream
        String jsonContent = "{ invalid json content }}}";

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        assertThatThrownBy(() ->
            jsonStreamProcessor.processJsonStream(
                    inputStream, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(CdbInternalException.class)
         .hasMessageContaining("JSON processing failed");
    }

    @Test
    public void testProcessJsonStreamWithMismatchedSize() {
        log.info("=======> testProcessJsonStreamWithMismatchedSize ");

        // Create a JSON stream where size doesn't match actual resources count
        // The processor should still work but log a warning
        String jsonContent = """
            {
                "resources": [
                    {
                        "hash": "mismatchhash",
                        "since": 6000,
                        "data": "bWlzbWF0Y2g=",
                        "streamerInfo": "mismatch-streamer"
                    }
                ],
                "size": 5
            }
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        log.info("Result with mismatched size: {}", result);
        assertThat(result).isNotNull();
        // The actual count should be 1, even though size field says 5
        assertThat(result.getSize()).isEqualTo(1);
    }

    @Test
    public void testProcessJsonStreamWithDifferentCompressionTypes() {
        log.info("=======> testProcessJsonStreamWithDifferentCompressionTypes ");

        String jsonContent = """
            {
                "resources": [
                    {
                        "hash": "compressionhash",
                        "since": 7000,
                        "data": "Y29tcHJlc3Npb24=",
                        "streamerInfo": "compression-streamer"
                    }
                ],
                "size": 1
            }
            """;

        // Test with gzip compression type
        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);
        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "gzip", TEST_TAG_NAME);

        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(1);

        // Test with none compression type
        inputStream = createBufferedInputStream(jsonContent.replace("compressionhash", "compressionhash2")
                .replace("7000", "7001"));
        result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(1);
    }

    @Test
    public void testProcessJsonStreamWithLargeData() {
        log.info("=======> testProcessJsonStreamWithLargeData ");

        // Create a larger base64 encoded data string
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeData.append("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo="); // "abcdefghijklmnopqrstuvwxyz" repeated
        }

        String jsonContent = String.format("""
            {
                "resources": [
                    {
                        "hash": "largehash",
                        "since": 8000,
                        "data": "%s",
                        "streamerInfo": "large-streamer"
                    }
                ],
                "size": 1
            }
            """, largeData.toString());

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        log.info("Result with large data: {}", result);
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(1);
    }

    @Test
    public void testProcessJsonStreamWithNullStreamerInfoThrowsException() {
        log.info("=======> testProcessJsonStreamWithNullStreamerInfoThrowsException ");

        // JSON without streamerInfo field - this should cause an exception
        // because PayloadService.savePayloadIov requires streamerInfo to be non-null
        String jsonContent = """
            {
                "resources": [
                    {
                        "hash": "nullstreamerhash",
                        "since": 9000,
                        "data": "bnVsbHN0cmVhbWVy"
                    }
                ],
                "size": 1
            }
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        // Expect a CdbBadRequestException because streamerInfo is required
        assertThatThrownBy(() ->
            jsonStreamProcessor.processJsonStream(
                    inputStream, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(CdbBadRequestException.class)
         .hasMessageContaining("streamerInfo is required");
    }

    @Test
    public void testProcessJsonStreamWithWhitespaceAndNewlines() {
        log.info("=======> testProcessJsonStreamWithWhitespaceAndNewlines ");

        // JSON with lots of whitespace and newlines
        String jsonContent = """
            
            
            {
            
                "resources"   :   [
                    {
                        "hash"   :   "whitespacehash"   ,
                        "since"  :   10000   ,
                        "data"   :   "d2hpdGVzcGFjZQ=="   ,
                        "streamerInfo"   :   "whitespace-streamer"
                    }
                ]   ,
                
                "size"   :   1
                
            }
            
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "testType", "1.0", "none", TEST_TAG_NAME);

        log.info("Result with whitespace: {}", result);
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(1);
    }

    @Test
    public void testProcessJsonStreamPopulatesCorrectFields() {
        log.info("=======> testProcessJsonStreamPopulatesCorrectFields ");

        String jsonContent = """
            {
                "resources": [
                    {
                        "hash": "fieldhash",
                        "since": 11000,
                        "data": "ZmllbGRz",
                        "streamerInfo": "field-streamer"
                    }
                ],
                "size": 1
            }
            """;

        BufferedInputStream inputStream = createBufferedInputStream(jsonContent);

        StoreSetDto result = jsonStreamProcessor.processJsonStream(
                inputStream, "myObjectType", "2.0", "none", TEST_TAG_NAME);

        log.info("Result fields: format={}, datatype={}, size={}",
                result.getFormat(), result.getDatatype(), result.getSize());

        assertThat(result).isNotNull();
        assertThat(result.getFormat()).isEqualTo("StoreSetDto");
        assertThat(result.getDatatype()).isEqualTo("payloads");
        assertThat(result.getSize()).isEqualTo(1);
        // Resources list is empty in the response (data is stored, not returned)
        assertThat(result.getResources()).isNotNull();
    }

    /**
     * Helper method to create a BufferedInputStream from a string.
     */
    private BufferedInputStream createBufferedInputStream(String content) {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return new BufferedInputStream(byteArrayInputStream);
    }

    /**
     * Test that processData throws IOException when parser has no current token.
     * We use a testable subclass to access the protected method.
     */
    @Test
    public void testProcessDataWithNoCurrentToken() {
        log.info("=======> testProcessDataWithNoCurrentToken ");

        // Create a mock JsonParser that has no current token
        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.hasCurrentToken()).thenReturn(false);

        // Create a testable subclass to access the protected method
        TestableJsonStreamProcessor testableProcessor = new TestableJsonStreamProcessor(
                payloadService, iovService, mapper);

        // Expect IOException wrapped due to invalid parser state
        assertThatThrownBy(() ->
            testableProcessor.callProcessData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("Invalid parser state")
         .hasMessageContaining("NO_TOKEN");
    }

    /**
     * Test that processData throws IOException when parser token is not START_OBJECT.
     */
    @Test
    public void testProcessDataWithWrongTokenType() {
        log.info("=======> testProcessDataWithWrongTokenType ");

        // Create a mock JsonParser that has a wrong token type
        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.hasCurrentToken()).thenReturn(true);
        when(mockParser.currentToken()).thenReturn(JsonToken.START_ARRAY);

        TestableJsonStreamProcessor testableProcessor = new TestableJsonStreamProcessor(
                payloadService, iovService, mapper);

        // Expect IOException due to wrong token type
        assertThatThrownBy(() ->
            testableProcessor.callProcessData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("Invalid parser state")
         .hasMessageContaining("Expected START_OBJECT")
         .hasMessageContaining("START_ARRAY");
    }

    /**
     * Test that processData throws IOException when parser token is VALUE_NULL.
     */
    @Test
    public void testProcessDataWithNullToken() {
        log.info("=======> testProcessDataWithNullToken ");

        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.hasCurrentToken()).thenReturn(true);
        when(mockParser.currentToken()).thenReturn(JsonToken.VALUE_NULL);

        TestableJsonStreamProcessor testableProcessor = new TestableJsonStreamProcessor(
                payloadService, iovService, mapper);

        assertThatThrownBy(() ->
            testableProcessor.callProcessData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("Invalid parser state")
         .hasMessageContaining("VALUE_NULL");
    }

    /**
     * Test that processData throws IOException when parser token is END_OBJECT.
     */
    @Test
    public void testProcessDataWithEndObjectToken() {
        log.info("=======> testProcessDataWithEndObjectToken ");

        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.hasCurrentToken()).thenReturn(true);
        when(mockParser.currentToken()).thenReturn(JsonToken.END_OBJECT);

        TestableJsonStreamProcessor testableProcessor = new TestableJsonStreamProcessor(
                payloadService, iovService, mapper);

        assertThatThrownBy(() ->
            testableProcessor.callProcessData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("Invalid parser state")
         .hasMessageContaining("END_OBJECT");
    }

    /**
     * Testable subclass that exposes the protected processData method for testing.
     */
    private static class TestableJsonStreamProcessor extends JsonStreamProcessor {

        public TestableJsonStreamProcessor(PayloadService payloadService,
                                           IovService iovService,
                                           ObjectMapper mapper) {
            super(payloadService, iovService, mapper);
        }

        /**
         * Public method to call the protected processData for testing.
         */
        public void callProcessData(JsonParser parser, String objectType, String version,
                                    String compressionType, String tag)
                throws NoSuchAlgorithmException, IOException {
            processData(parser, objectType, version, compressionType, tag);
        }
    }
}

