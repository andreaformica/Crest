package hep.crest.server.controllers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.exceptions.CdbInternalException;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for JsonStreamProcessor.processData method.
 * This class is in the same package as JsonStreamProcessor to access package-private methods.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@Slf4j
public class TestJsonStreamProcessorProcessData {

    @Autowired
    private PayloadService payloadService;

    @Autowired
    private IovService iovService;

    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper mapper;

    private static final String TEST_TAG_NAME = "test-json-stream-tag";

    /**
     * Test that processData throws IOException when parser has no current token.
     * The JsonParseException is caught and wrapped in IOException.
     */
    @Test
    public void testProcessDataWithNoCurrentToken() {
        log.info("=======> testProcessDataWithNoCurrentToken ");

        // Create a mock JsonParser that has no current token
        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.hasCurrentToken()).thenReturn(false);

        // Create processor instance
        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        // Expect IOException wrapping JsonParseException due to invalid parser state
        assertThatThrownBy(() ->
            processor.processData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("JSON parsing failed")
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

        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        // Expect IOException wrapping JsonParseException due to wrong token type
        assertThatThrownBy(() ->
            processor.processData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("JSON parsing failed")
         .hasMessageContaining("Invalid parser state")
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

        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        assertThatThrownBy(() ->
            processor.processData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("JSON parsing failed")
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

        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        assertThatThrownBy(() ->
            processor.processData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("JSON parsing failed")
         .hasMessageContaining("END_OBJECT");
    }

    /**
     * Test that processData throws IOException when parser token is FIELD_NAME.
     */
    @Test
    public void testProcessDataWithFieldNameToken() {
        log.info("=======> testProcessDataWithFieldNameToken ");

        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.hasCurrentToken()).thenReturn(true);
        when(mockParser.currentToken()).thenReturn(JsonToken.FIELD_NAME);

        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        assertThatThrownBy(() ->
            processor.processData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("JSON parsing failed")
         .hasMessageContaining("FIELD_NAME");
    }

    /**
     * Test that processData throws IOException when parser token is VALUE_STRING.
     */
    @Test
    public void testProcessDataWithValueStringToken() {
        log.info("=======> testProcessDataWithValueStringToken ");

        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.hasCurrentToken()).thenReturn(true);
        when(mockParser.currentToken()).thenReturn(JsonToken.VALUE_STRING);

        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        assertThatThrownBy(() ->
            processor.processData(mockParser, "testType", "1.0", "none", TEST_TAG_NAME)
        ).isInstanceOf(IOException.class)
         .hasMessageContaining("JSON parsing failed")
         .hasMessageContaining("VALUE_STRING");
    }

    /**
     * Test that configureJsonFactory throws CdbInternalException when configuration fails.
     */
    @Test
    public void testConfigureJsonFactoryThrowsException() {
        log.info("=======> testConfigureJsonFactoryThrowsException ");

        // Create a mock JsonFactory that throws an exception when configured
        JsonFactory mockFactory = mock(JsonFactory.class);
        when(mockFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false))
                .thenThrow(new RuntimeException("Configuration failed"));

        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        // Expect CdbInternalException when configureJsonFactory fails
        assertThatThrownBy(() ->
            processor.configureJsonFactory(mockFactory)
        ).isInstanceOf(CdbInternalException.class)
         .hasMessageContaining("Cannot configure JsonFactory");
    }

    /**
     * Test that configureJsonFactory succeeds with a valid JsonFactory.
     */
    @Test
    public void testConfigureJsonFactorySuccess() {
        log.info("=======> testConfigureJsonFactorySuccess ");

        // Use a real JsonFactory
        JsonFactory realFactory = new JsonFactory();

        JsonStreamProcessor processor = new JsonStreamProcessor(payloadService, iovService, mapper);

        // Should not throw and return the configured factory
        JsonFactory result = processor.configureJsonFactory(realFactory);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(realFactory);
    }
}

