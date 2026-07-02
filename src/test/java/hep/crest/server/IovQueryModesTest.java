package hep.crest.server;

import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.IovRepository;
import hep.crest.server.data.repositories.PayloadRepository;
import hep.crest.server.data.repositories.args.IovModeEnum;
import hep.crest.server.data.repositories.args.IovQueryArgs;
import hep.crest.server.services.IovService;
import hep.crest.server.services.TagService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive tests for IOV query modes (AT, RANGES, GROUPS).
 * Tests the proper implementation of the max(pi.id.since) logic in IovRepositoryImpl.
 *
 * @author formica
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class IovQueryModesTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private IovService iovService;

    @Autowired
    private IovRepository iovRepository;

    @Autowired
    private PayloadRepository payloadRepository;

    private static final String TEST_TAG_BASE_NAME = "IovQueryModesTest-Tag";
    private String testTagName;
    
    // Test data: IOVs at since values 100, 130, 170, 200, 220
    private static final BigInteger[] IOV_SINCE_VALUES = {
            BigInteger.valueOf(100),
            BigInteger.valueOf(130),
            BigInteger.valueOf(170),
            BigInteger.valueOf(200),
            BigInteger.valueOf(220),
            BigInteger.valueOf(304),
            BigInteger.valueOf(500),
            BigInteger.valueOf(520),
            BigInteger.valueOf(600),
            BigInteger.valueOf(660),
            BigInteger.valueOf(700),
            BigInteger.valueOf(770),
            BigInteger.valueOf(800),

    };

    private Tag testTag;

    /**
     * Set up test data before each test.
     * Creates a tag and inserts IOVs at predefined since values.
     */
    @BeforeEach
    public void setUp() throws Exception {
        log.info("====> Setting up test data for IovQueryModesTest");
        
        // Create unique tag name to avoid conflicts between test runs
        testTagName = TEST_TAG_BASE_NAME + "-" + System.currentTimeMillis();
        
        // Create test tag
        testTag = new Tag();
        testTag.setName(testTagName);
        testTag.setDescription("Test tag for IOV query mode testing");
        testTag.setTimeType("time");
        testTag.setObjectType("test");
        testTag.setSynchronization("all");
        testTag = tagService.insertTag(testTag);
        log.info("Created test tag: {} with ID: {}", testTagName, testTag.getTagId());

        // Create IOVs at predefined since values: 100, 130, 170, 200, 220
        for (int i = 0; i < IOV_SINCE_VALUES.length; i++) {
            createIovWithPayload(testTag, IOV_SINCE_VALUES[i], "payload-data-" + i);
        }
        
        log.info("Created {} IOVs with since values: {}", IOV_SINCE_VALUES.length, IOV_SINCE_VALUES);
    }

    /**
     * Helper method to create an IOV with a fake payload.
     *
     * @param tag         the tag to associate the IOV with
     * @param since       the since value for the IOV
     * @param payloadData the payload data string
     */
    private void createIovWithPayload(Tag tag, BigInteger since, String payloadData) {
        // Create fake payload
        String hash = "test-hash-" + since.toString();
        Payload payload = new Payload();
        payload.setHash(hash);
        payload.setObjectType("test");
        payload.setVersion("1.0");
        payload.setCompressionType("none");
        payload.setSize(payloadData.length());
        payload.setInsertionTime(new Date());
        payloadRepository.save(payload);

        // Create IOV
        Iov iov = new Iov();
        IovId iovId = new IovId();
        iovId.setTagId(tag.getTagId());
        iovId.setSince(since);
        iovId.setInsertionTime(new Date());
        iov.setId(iovId);
        iov.setTag(tag);
        iov.setPayloadHash(hash);
        iovRepository.save(iov);
        
        log.debug("Created IOV at since={} with hash={}", since, hash);
    }

    // ========== AT Mode Tests ==========

    /**
     * Test AT mode: Query at exact IOV boundary (since=100).
     * Should return the IOV at 100.
     */
    @Test
    public void testAtMode_ExactBoundary() {
        log.info("====> testAtMode_ExactBoundary");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(100))
                .mode(IovModeEnum.AT);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId().getSince()).isEqualTo(BigInteger.valueOf(100));
        
        log.info("AT mode at 100 returned: {}", result.getContent().get(0).getId().getSince());
    }

    /**
     * Test AT mode: Query in the middle of an IOV range (since=150).
     * Should return the IOV at 130 (the one valid at time 150).
     */
    @Test
    public void testAtMode_MiddleOfRange() {
        log.info("====> testAtMode_MiddleOfRange");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(150))
                .mode(IovModeEnum.AT);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId().getSince()).isEqualTo(BigInteger.valueOf(130));
        
        log.info("AT mode at 150 returned IOV starting at: {}", 
                result.getContent().get(0).getId().getSince());
    }

    /**
     * Test AT mode: Query before the first IOV (since=50).
     * Should return no results.
     */
    @Test
    public void testAtMode_BeforeFirstIov() {
        log.info("====> testAtMode_BeforeFirstIov");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(50))
                .mode(IovModeEnum.AT);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        
        log.info("AT mode at 50 (before first IOV) returned: {} results", result.getContent().size());
    }

    /**
     * Test AT mode: Query after the last IOV (since=300).
     * Should return the last IOV at 220.
     */
    @Test
    public void testAtMode_AfterLastIov() {
        log.info("====> testAtMode_AfterLastIov");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(300))
                .mode(IovModeEnum.AT);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId().getSince()).isEqualTo(BigInteger.valueOf(220));
        
        log.info("AT mode at 300 returned IOV starting at: {}", 
                result.getContent().get(0).getId().getSince());
    }

    // ========== RANGES Mode Tests ==========

    /**
     * Test RANGES mode: Query from exact boundary to end (since=100, until=300).
     * Should return all IOVs: 100, 130, 170, 200, 220.
     */
    @Test
    public void testRangesMode_FromExactBoundary() {
        log.info("====> testRangesMode_FromExactBoundary");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(100))
                .until(BigInteger.valueOf(300))
                .mode(IovModeEnum.RANGES);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5);
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        assertThat(sinceValues).containsExactly(
                BigInteger.valueOf(100),
                BigInteger.valueOf(130),
                BigInteger.valueOf(170),
                BigInteger.valueOf(200),
                BigInteger.valueOf(220)
        );
        
        log.info("RANGES mode [100, 300] returned {} IOVs: {}", 
                result.getContent().size(), sinceValues);
    }

    /**
     * Test RANGES mode: Critical test for the bug fix!
     * Query in the middle of an IOV range (since=150, until=250).
     * 
     * This tests the max(pi.id.since) logic:
     * - The IOV at 130 is valid at time 150 (it starts before 150)
     * - RANGES mode should include this IOV even though 130 < 150
     * - Should return: 130, 170, 200, 220
     */
    @Test
    public void testRangesMode_MiddleOfRange_IncludesFirstIov() {
        log.info("====> testRangesMode_MiddleOfRange_IncludesFirstIov (CRITICAL BUG FIX TEST)");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(150))
                .until(BigInteger.valueOf(250))
                .mode(IovModeEnum.RANGES);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // CRITICAL: The first IOV should be 130, NOT 170!
        // This verifies the max(pi.id.since) logic is working correctly
        assertThat(sinceValues).containsExactly(
                BigInteger.valueOf(130),  // This IOV starts BEFORE 150 but is valid at 150
                BigInteger.valueOf(170),
                BigInteger.valueOf(200),
                BigInteger.valueOf(220)
        );
        
        log.info("RANGES mode [150, 250] correctly returned {} IOVs starting from 130: {}", 
                result.getContent().size(), sinceValues);
        log.info("✓ Bug fix verified: IOV at 130 (before query since=150) is correctly included");
    }

    /**
     * Test RANGES mode: Query a limited range (since=115, until=165).
     * Should include IOV at 100 (valid at 115) and IOV at 130.
     */
    @Test
    public void testRangesMode_LimitedRange() {
        log.info("====> testRangesMode_LimitedRange");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(115))
                .until(BigInteger.valueOf(165))
                .mode(IovModeEnum.RANGES);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        assertThat(sinceValues).containsExactly(
                BigInteger.valueOf(100),  // Valid at 115
                BigInteger.valueOf(130)
        );
        
        log.info("RANGES mode [115, 165] returned {} IOVs: {}", 
                result.getContent().size(), sinceValues);
    }

    /**
     * Test RANGES mode: Query before first IOV (since=50, until=80).
     * Should return no results.
     */
    @Test
    public void testRangesMode_BeforeFirstIov() {
        log.info("====> testRangesMode_BeforeFirstIov");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(50))
                .until(BigInteger.valueOf(80))
                .mode(IovModeEnum.RANGES);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        
        log.info("RANGES mode [50, 80] (before first IOV) returned: {} results", 
                result.getContent().size());
    }

    /**
     * Test RANGES mode: Query spanning from middle to after last IOV (since=185, until=300).
     * Should include IOV at 170 (valid at 185) and IOVs at 200, 220.
     */
    @Test
    public void testRangesMode_ToEnd() {
        log.info("====> testRangesMode_ToEnd");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(185))
                .until(BigInteger.valueOf(300))
                .mode(IovModeEnum.RANGES);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        assertThat(sinceValues).containsExactly(
                BigInteger.valueOf(170),  // Valid at 185
                BigInteger.valueOf(200),
                BigInteger.valueOf(220)
        );
        
        log.info("RANGES mode [185, 300] returned {} IOVs: {}", 
                result.getContent().size(), sinceValues);
    }

    // ========== IOVS Mode Tests ==========

    /**
     * Test IOVS mode: Simple range query from exact boundary (since=100, until=300).
     * IOVS mode should return all IOVs where since >= 100 and since < 300.
     * Should return: 100, 130, 170, 200, 220.
     */
    @Test
    public void testIovsMode_FromExactBoundary() {
        log.info("====> testIovsMode_FromExactBoundary");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(100))
                .until(BigInteger.valueOf(300))
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5);
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        assertThat(sinceValues).containsExactly(
                BigInteger.valueOf(100),
                BigInteger.valueOf(130),
                BigInteger.valueOf(170),
                BigInteger.valueOf(200),
                BigInteger.valueOf(220)
        );
        
        log.info("IOVS mode [100, 300) returned {} IOVs: {}", 
                result.getContent().size(), sinceValues);
    }

    /**
     * Test IOVS mode: Query in the middle of IOV range (since=150, until=250).
     * Unlike RANGES mode, IOVS mode does NOT include the IOV at 130.
     * Should return only: 170, 200, 220.
     */
    @Test
    public void testIovsMode_MiddleOfRange_ExcludesFirstIov() {
        log.info("====> testIovsMode_MiddleOfRange_ExcludesFirstIov");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(150))
                .until(BigInteger.valueOf(250))
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // IOVS mode uses simple since >= 150, so IOV at 130 is excluded
        assertThat(sinceValues).containsExactly(
                BigInteger.valueOf(170),
                BigInteger.valueOf(200),
                BigInteger.valueOf(220)
        );
        
        // This is different from RANGES mode which would include 130
        assertThat(sinceValues).doesNotContain(BigInteger.valueOf(130));
        
        log.info("IOVS mode [150, 250) returned {} IOVs starting from 170: {}", 
                result.getContent().size(), sinceValues);
        log.info("✓ IOVS mode correctly excludes IOV at 130 (simple range query)");
    }

    /**
     * Test IOVS mode: Query with limited range (since=115, until=165).
     * Should only include IOV at 130 (100 is excluded because 100 < 115).
     */
    @Test
    public void testIovsMode_LimitedRange() {
        log.info("====> testIovsMode_LimitedRange");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(115))
                .until(BigInteger.valueOf(165))
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        assertThat(sinceValues).containsExactly(BigInteger.valueOf(130));
        
        log.info("IOVS mode [115, 165) returned {} IOVs: {}", 
                result.getContent().size(), sinceValues);
    }

    /**
     * Test IOVS mode: Query before first IOV (since=50, until=80).
     * Should return no results.
     */
    @Test
    public void testIovsMode_BeforeFirstIov() {
        log.info("====> testIovsMode_BeforeFirstIov");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(50))
                .until(BigInteger.valueOf(80))
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        
        log.info("IOVS mode [50, 80) returned: {} results", result.getContent().size());
    }

    /**
     * Test IOVS mode: Query from middle to end (since=185, until=900).
     * Should return IOVs where since >= 185: 200, 220, 304, 500, 520, 600, 660, 700, 770, 800.
     */
    @Test
    public void testIovsMode_ToEnd() {
        log.info("====> testIovsMode_ToEnd");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(185))
                .until(BigInteger.valueOf(900))
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // Should NOT include 170
        assertThat(sinceValues).doesNotContain(BigInteger.valueOf(170));
        
        // Should start from 200
        assertThat(sinceValues.get(0)).isEqualTo(BigInteger.valueOf(200));
        
        log.info("IOVS mode [185, 900) returned {} IOVs: {}", 
                result.getContent().size(), sinceValues);
    }

    /**
     * Test IOVS mode: Single parameter query (only since, no until).
     * Should return all IOVs from since onwards.
     */
    @Test
    public void testIovsMode_OnlySinceParameter() {
        log.info("====> testIovsMode_OnlySinceParameter");
        
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(500))
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // All IOVs should have since >= 500
        sinceValues.forEach(since -> 
            assertThat(since).isGreaterThanOrEqualTo(BigInteger.valueOf(500))
        );
        
        log.info("IOVS mode with since=500 (no until) returned {} IOVs: {}", 
                result.getContent().size(), sinceValues);
    }

    /**
     * Test IOVS mode with snapshot filtering.
     * Verifies that snapshot time works correctly with IOVS mode.
     */
    @Test
    public void testIovsMode_WithSnapshot() throws Exception {
        log.info("====> testIovsMode_WithSnapshot");
        
        Timestamp snapshotTime = new Timestamp(System.currentTimeMillis());
        Thread.sleep(100);
        
        // Create a new IOV with insertion time after snapshot
        Date futureTime = new Date(System.currentTimeMillis() + 10000);
        
        Iov futureIov = new Iov();
        IovId futureIovId = new IovId();
        futureIovId.setTagId(testTag.getTagId());
        futureIovId.setSince(BigInteger.valueOf(350));
        futureIovId.setInsertionTime(futureTime);
        futureIov.setId(futureIovId);
        futureIov.setTag(testTag);
        futureIov.setPayloadHash("future-hash-350");
        iovRepository.save(futureIov);
        
        // Query with snapshot before the future IOV
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(300))
                .until(BigInteger.valueOf(400))
                .snapshot(snapshotTime)
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // Should not include the future IOV at 350
        assertThat(sinceValues).doesNotContain(BigInteger.valueOf(350));
        
        log.info("IOVS mode with snapshot correctly excluded future IOV at 350");
        log.info("Returned IOVs: {}", sinceValues);
    }

    // ========== GROUPS Mode Tests ==========

    /**
     * Test GROUPS mode: Query with a small group size.
     * Groups mode returns the first IOV in each group based on mod(rownum, groupsize) = 1.
     * Note: groupsize must be > 10 to be used, otherwise defaults to 100.
     */
    @Test
    public void testGroupsMode_SmallGroupSize() {
        log.info("====> testGroupsMode_SmallGroupSize");
        
        // Use groupSize > 10 so it's actually used (not defaulted to 100)
        Long groupSize = 2L;  // Will be defaulted to 100, so we get 1 group
        List<BigInteger> groups = iovService.selectGroupsByTagNameAndSnapshotTime(
                testTagName, 
                new Date(), 
                groupSize
        );
        
        assertThat(groups).isNotNull();
        assertThat(groups).isNotEmpty();
        
        // With groupSize=2 (which defaults to 100 since <= 10), we expect 1 group with 5 IOVs
        log.info("GROUPS mode with groupSize={} returned {} groups: {}", 
                groupSize, groups.size(), groups);
        
        // Verify groups are sorted
        for (int i = 1; i < groups.size(); i++) {
            assertThat(groups.get(i)).isGreaterThan(groups.get(i - 1));
        }
    }

    /**
     * Test GROUPS mode: Query with a larger group size (> 10 so it's actually used).
     * With groupsize > 10, the actual groupsize is used instead of the default 100.
     */
    @Test
    public void testGroupsMode_LargerGroupSize() {
        log.info("====> testGroupsMode_LargerGroupSize");
        
        // Use groupSize > 10 to bypass the default 100 behavior
        Long groupSize = 11L;
        List<BigInteger> groups = iovService.selectGroupsByTagNameAndSnapshotTime(
                testTagName, 
                new Date(), 
                groupSize
        );
        
        assertThat(groups).isNotNull();
        assertThat(groups).isNotEmpty();
        
        // With 5 IOVs and groupSize=11, we expect 1 group (first IOV where mod(rownum, 11) = 1)
        log.info("GROUPS mode with groupSize={} returned {} groups: {}", 
                groupSize, groups.size(), groups);
        
        // Verify groups are sorted
        for (int i = 1; i < groups.size(); i++) {
            assertThat(groups.get(i)).isGreaterThan(groups.get(i - 1));
        }
    }

    // ========== Edge Cases and Combinations ==========

    /**
     * Comparison test: IOVS vs RANGES mode with same query parameters.
     * This test demonstrates the key difference between the two modes.
     * 
     * Querying [150, 250]:
     * - IOVS mode: Returns IOVs where 150 <= since < 250 (simple range)
     * - RANGES mode: Finds IOV valid at 150 (which is 130), then returns all from 130 to 250
     */
    @Test
    public void testCompareIovsVsRangesMode() {
        log.info("====> testCompareIovsVsRangesMode");
        
        BigInteger since = BigInteger.valueOf(150);
        BigInteger until = BigInteger.valueOf(250);
        
        // Query with IOVS mode
        IovQueryArgs iovsArgs = new IovQueryArgs()
                .tagName(testTagName)
                .since(since)
                .until(until)
                .mode(IovModeEnum.IOVS);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> iovsResult = iovService.selectIovList(iovsArgs, pageable);
        
        List<BigInteger> iovsSinceValues = iovsResult.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // Query with RANGES mode
        IovQueryArgs rangesArgs = new IovQueryArgs()
                .tagName(testTagName)
                .since(since)
                .until(until)
                .mode(IovModeEnum.RANGES);
        
        Page<Iov> rangesResult = iovService.selectIovList(rangesArgs, pageable);
        
        List<BigInteger> rangesSinceValues = rangesResult.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // IOVS returns: 170, 200, 220 (simple since >= 150)
        assertThat(iovsSinceValues).containsExactly(
                BigInteger.valueOf(170),
                BigInteger.valueOf(200),
                BigInteger.valueOf(220)
        );
        
        // RANGES returns: 130, 170, 200, 220 (includes IOV valid at 150)
        assertThat(rangesSinceValues).containsExactly(
                BigInteger.valueOf(130),
                BigInteger.valueOf(170),
                BigInteger.valueOf(200),
                BigInteger.valueOf(220)
        );
        
        // The key difference: RANGES includes IOV at 130
        assertThat(iovsSinceValues).doesNotContain(BigInteger.valueOf(130));
        assertThat(rangesSinceValues).contains(BigInteger.valueOf(130));
        
        log.info("IOVS mode [150, 250) returned {} IOVs: {}", 
                iovsSinceValues.size(), iovsSinceValues);
        log.info("RANGES mode [150, 250] returned {} IOVs: {}", 
                rangesSinceValues.size(), rangesSinceValues);
        log.info("✓ Difference verified: RANGES includes IOV at 130, IOVS does not");
    }

    /**
     * Test AT mode with snapshot in the past.
     * Verifies that snapshot time filtering works correctly.
     */
    @Test
    public void testAtMode_WithSnapshot() throws Exception {
        log.info("====> testAtMode_WithSnapshot");
        
        // Create a new IOV with a future insertion time
        Thread.sleep(100); // Small delay to ensure different insertion time
        Date futureTime = new Date(System.currentTimeMillis() + 10000);
        
        Iov futureIov = new Iov();
        IovId futureIovId = new IovId();
        futureIovId.setTagId(testTag.getTagId());
        futureIovId.setSince(BigInteger.valueOf(250));
        futureIovId.setInsertionTime(futureTime);
        futureIov.setId(futureIovId);
        futureIov.setTag(testTag);
        futureIov.setPayloadHash("future-hash");
        iovRepository.save(futureIov);
        
        // Query with a snapshot before the future IOV's insertion time
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(260))
                .snapshot(new Timestamp(System.currentTimeMillis())) // Current time, before future IOV
                .mode(IovModeEnum.AT);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        // Should return IOV at 220, not the future one at 250
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId().getSince()).isEqualTo(BigInteger.valueOf(220));
        
        log.info("AT mode with snapshot correctly excluded future IOV");
    }

    /**
     * Test RANGES mode with snapshot.
     */
    @Test
    public void testRangesMode_WithSnapshot() throws Exception {
        log.info("====> testRangesMode_WithSnapshot");
        
        Timestamp snapshotTime = new Timestamp(System.currentTimeMillis());
        Thread.sleep(100);
        
        // Create a new IOV with insertion time after snapshot
        Date futureTime = new Date(System.currentTimeMillis() + 10000);
        
        Iov futureIov = new Iov();
        IovId futureIovId = new IovId();
        futureIovId.setTagId(testTag.getTagId());
        futureIovId.setSince(BigInteger.valueOf(140));
        futureIovId.setInsertionTime(futureTime);
        futureIov.setId(futureIovId);
        futureIov.setTag(testTag);
        futureIov.setPayloadHash("future-hash-140");
        iovRepository.save(futureIov);
        
        // Query with snapshot before the future IOV
        IovQueryArgs args = new IovQueryArgs()
                .tagName(testTagName)
                .since(BigInteger.valueOf(120))
                .until(BigInteger.valueOf(200))
                .snapshot(snapshotTime)
                .mode(IovModeEnum.RANGES);
        
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id.since").ascending());
        Page<Iov> result = iovService.selectIovList(args, pageable);
        
        assertThat(result).isNotNull();
        List<BigInteger> sinceValues = result.getContent().stream()
                .map(iov -> iov.getId().getSince())
                .toList();
        
        // Should not include the future IOV at 140
        assertThat(sinceValues).doesNotContain(BigInteger.valueOf(140));
        
        log.info("RANGES mode with snapshot correctly excluded future IOV at 140");
        log.info("Returned IOVs: {}", sinceValues);
    }
}






