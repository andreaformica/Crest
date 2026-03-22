package hep.crest.server;


import hep.crest.server.caching.CachingProperties;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.PayloadInfoData;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.IovRepository;
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

import java.math.BigInteger;
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
    private IovRepository iovRepository;

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
        assertThat(groupsize).isEqualTo(1000L);
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

    @Test
    public void testRemovePayloadWithIovInAnotherTag() throws Exception {
        log.info("=======> testRemovePayloadWithIovInAnotherTag");

        // Create two tags
        String tag1Name = "ServiceTests-Tag-Remove-1-" + System.currentTimeMillis();
        String tag2Name = "ServiceTests-Tag-Remove-2-" + System.currentTimeMillis();

        Tag tag1 = new Tag();
        tag1.setName(tag1Name);
        tag1.setDescription("Test tag 1 for remove payload test");
        tag1.setTimeType("time");
        tag1.setObjectType("test");
        tag1.setSynchronization("all");
        tagService.insertTag(tag1);

        Tag tag2 = new Tag();
        tag2.setName(tag2Name);
        tag2.setDescription("Test tag 2 for remove payload test");
        tag2.setTimeType("time");
        tag2.setObjectType("test");
        tag2.setSynchronization("all");
        tagService.insertTag(tag2);

        // Create a payload
        String testHash = "test-shared-hash-" + System.currentTimeMillis();

        Payload payload = new Payload();
        payload.setHash(testHash);
        payload.setObjectType("test");
        payload.setVersion("1.0");
        payload.setCompressionType("none");
        payload.setSize(100);
        payloadRepository.save(payload);

        // Create IOV in tag1 with this hash
        Iov iov1 = new Iov();
        IovId iovId1 = new IovId();
        iovId1.setTagId(tag1.getTagId());
        iovId1.setSince(BigInteger.valueOf(1000L));
        iov1.setId(iovId1);
        iov1.setTag(tag1);
        iov1.setPayloadHash(testHash);
        iovService.insertIov(iov1);

        // Create IOV in tag2 with the SAME hash
        Iov iov2 = new Iov();
        IovId iovId2 = new IovId();
        iovId2.setTagId(tag2.getTagId());
        iovId2.setSince(BigInteger.valueOf(2000L));
        iov2.setId(iovId2);
        iov2.setTag(tag2);
        iov2.setPayloadHash(testHash);
        iovService.insertIov(iov2);

        log.info("Created payload {} with IOVs in tags {} and {}", testHash, tag1Name, tag2Name);

        // Try to remove payload for tag1 - should NOT remove because tag2 still uses it
        String result = payloadService.removePayload(tag1Name, testHash, false);

        log.info("Remove payload result (should be hash, not 'removed'): {}", result);

        // The payload should NOT be removed - result should be the hash, not "removed"
        assertThat(result).isEqualTo(testHash);

        // Verify payload still exists
        assertThat(payloadRepository.findById(testHash)).isPresent();
        log.info("Payload {} still exists as expected (used by another tag)", testHash);
    }

    @Test
    public void testRemovePayloadWithIovInSameTagOnly() throws Exception {
        log.info("=======> testRemovePayloadWithIovInSameTagOnly");

        // Create a tag
        String tagName = "ServiceTests-Tag-Remove-Same-" + System.currentTimeMillis();

        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setDescription("Test tag for remove payload test");
        tag.setTimeType("time");
        tag.setObjectType("test");
        tag.setSynchronization("all");
        tagService.insertTag(tag);

        // Create a payload
        String testHash = "test-single-tag-hash-" + System.currentTimeMillis();

        Payload payload = new Payload();
        payload.setHash(testHash);
        payload.setObjectType("test");
        payload.setVersion("1.0");
        payload.setCompressionType("none");
        payload.setSize(100);
        payloadRepository.save(payload);

        // Create IOV in the same tag with this hash
        Iov iov = new Iov();
        IovId iovId = new IovId();
        iovId.setTagId(tag.getTagId());
        iovId.setSince(BigInteger.valueOf(1000L));
        iov.setId(iovId);
        iov.setTag(tag);
        iov.setPayloadHash(testHash);
        iovService.insertIov(iov);

        log.info("Created payload {} with IOV in tag {}", testHash, tagName);

        // First remove the IOV using the repository
        iovRepository.deleteByTagIdAndHash(tag.getTagId(), testHash);
        log.info("Removed IOV from tag {}", tagName);

        // Now remove payload for this tag - should succeed because no other tag uses it
        String result = payloadService.removePayload(tagName, testHash, false);

        log.info("Remove payload result (should be 'removed'): {}", result);

        // The payload should be removed
        assertThat(result).isEqualTo("removed");

        // Verify payload is gone
        assertThat(payloadRepository.findById(testHash)).isEmpty();
        log.info("Payload {} removed successfully", testHash);
    }
}
