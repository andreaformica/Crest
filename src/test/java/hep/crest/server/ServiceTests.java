package hep.crest.server;


import hep.crest.server.caching.CachingProperties;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.PayloadInfoData;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.PayloadInfoDataRepository;
import hep.crest.server.data.repositories.PayloadRepository;
import hep.crest.server.exceptions.CdbInternalException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import hep.crest.server.services.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ServiceTests {

    private static final Logger log = LoggerFactory.getLogger(ServiceTests.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private IovService iovService;

    @Autowired
    private PayloadService payloadService;

    @Autowired
    private PayloadRepository payloadRepository;

    @Autowired
    private PayloadInfoDataRepository payloadInfoDataRepository;

    @Autowired
    private CachingProperties cprops;

    private static final String TEST_TAG_NAME = "ServiceTests-Tag";
    private static final String TEST_PAYLOAD_HASH = "servicetests-payload-hash-001";

    @BeforeEach
    public void setUp() {
        // Create a test tag if it doesn't exist
        try {
            tagService.findOne(TEST_TAG_NAME);
        } catch (CdbNotFoundException e) {
            Tag tag = new Tag();
            tag.setName(TEST_TAG_NAME);
            tag.setDescription("Test tag for ServiceTests");
            tag.setTimeType("time");
            tag.setObjectType("test");
            tag.setSynchronization("all");
            tagService.insertTag(tag);
            log.info("Created test tag: {}", TEST_TAG_NAME);
        }
    }

    @Test
    public void testGroupSize() throws Exception {
        Long groupsize = iovService.getOptimalGroupSize("run");
        assertThat(groupsize).isEqualTo(Long.valueOf(cprops.getRuntypeGroupsize()));

        groupsize = iovService.getOptimalGroupSize("run-lumi");
        assertThat(groupsize).isEqualTo(Long.valueOf(cprops.getRuntypeGroupsize()));

        groupsize = iovService.getOptimalGroupSize("cool");
        assertThat(groupsize).isEqualTo(10L);
    }

    @Test
    public void testUpdatePayloadMetaInfoNotFound() {
        log.info("=======> testUpdatePayloadMetaInfoNotFound");

        // Try to update meta info for a non-existent payload
        assertThatThrownBy(() ->
            payloadService.updatePayloadMetaInfo("non-existent-hash", "new-streamer-info")
        ).isInstanceOf(CdbNotFoundException.class)
         .hasMessageContaining("Cannot find streamer info for hash");
    }

    @Test
    public void testUpdatePayloadMetaInfoSuccess() throws Exception {
        log.info("=======> testUpdatePayloadMetaInfoSuccess");

        // First, create a payload with streamer info
        String testHash = "test-meta-info-hash-" + System.currentTimeMillis();

        // Create payload entity
        Payload payload = new Payload();
        payload.setHash(testHash);
        payload.setObjectType("test");
        payload.setVersion("1.0");
        payload.setCompressionType("none");
        payload.setSize(100);

        // Create streamer info with valid JSON
        PayloadInfoData infoData = new PayloadInfoData();
        String initialStreamerInfo = "{\"hash\":\"" + testHash + "\",\"streamerInfo\":\"initial\"}";
        infoData.hash(testHash);
        infoData.streamerInfo(initialStreamerInfo.getBytes(StandardCharsets.UTF_8));

        // Save payload and info
        payloadRepository.save(payload);
        payloadInfoDataRepository.save(infoData);

        // Update the streamer info
        payloadService.updatePayloadMetaInfo(testHash, "updated-streamer-info");

        // Verify the update
        PayloadInfoData updatedInfo = payloadInfoDataRepository.findById(testHash).orElse(null);
        assertThat(updatedInfo).isNotNull();
        String updatedStreamerInfo = new String(updatedInfo.streamerInfo(), StandardCharsets.UTF_8);
        log.info("Updated streamer info: {}", updatedStreamerInfo);
        assertThat(updatedStreamerInfo).contains("updated-streamer-info");

        // Cleanup
        payloadInfoDataRepository.deleteById(testHash);
        payloadRepository.deleteById(testHash);
    }

    @Test
    public void testUpdatePayloadMetaInfoInvalidJson() throws Exception {
        log.info("=======> testUpdatePayloadMetaInfoInvalidJson");

        // Create a payload with invalid JSON in streamer info
        String testHash = "test-invalid-json-hash-" + System.currentTimeMillis();

        Payload payload = new Payload();
        payload.setHash(testHash);
        payload.setObjectType("test");
        payload.setVersion("1.0");
        payload.setCompressionType("none");
        payload.setSize(100);

        PayloadInfoData infoData = new PayloadInfoData();
        infoData.hash(testHash);
        // Set invalid JSON
        infoData.streamerInfo("not-valid-json".getBytes(StandardCharsets.UTF_8));

        payloadRepository.save(payload);
        payloadInfoDataRepository.save(infoData);

        // Try to update - should throw CdbInternalException due to JSON parsing error
        assertThatThrownBy(() ->
            payloadService.updatePayloadMetaInfo(testHash, "new-info")
        ).isInstanceOf(CdbInternalException.class)
         .hasMessageContaining("Cannot update streamer info for hash");

        // Cleanup
        payloadInfoDataRepository.deleteById(testHash);
        payloadRepository.deleteById(testHash);
    }

    @Test
    public void testRemovePayloadNotFound() {
        log.info("=======> testRemovePayloadNotFound");

        // Try to remove a non-existent payload
        assertThatThrownBy(() ->
            payloadService.removePayload(TEST_TAG_NAME, "non-existent-hash", false)
        ).isInstanceOf(CdbNotFoundException.class)
         .hasMessageContaining("Cannot find payload for hash");
    }

    @Test
    public void testRemovePayloadSuccess() throws Exception {
        log.info("=======> testRemovePayloadSuccess");

        // Create a payload that can be removed
        String testHash = "test-remove-hash-" + System.currentTimeMillis();

        Payload payload = new Payload();
        payload.setHash(testHash);
        payload.setObjectType("test");
        payload.setVersion("1.0");
        payload.setCompressionType("none");
        payload.setSize(100);

        payloadRepository.save(payload);

        // Remove the payload (no IOVs associated, so it should succeed)
        String result = payloadService.removePayload(TEST_TAG_NAME, testHash, false);

        log.info("Remove payload result: {}", result);
        assertThat(result).isEqualTo("removed");

        // Verify it's gone
        assertThat(payloadRepository.findById(testHash)).isEmpty();
    }
}
