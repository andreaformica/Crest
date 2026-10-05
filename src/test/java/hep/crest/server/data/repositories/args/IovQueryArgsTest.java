package hep.crest.server.data.repositories.args;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for IovQueryArgs POJO.
 * Tests getters, setters (via Lombok fluent API), and the checkArgsNull validation method.
 */
class IovQueryArgsTest {

    private IovQueryArgs args;

    @BeforeEach
    void setUp() {
        args = new IovQueryArgs();
    }

    // ========== Getter/Setter Tests ==========

    @Test
    void testTagName_getterAndSetter() {
        // given
        String tagName = "test-tag";

        // when
        args.tagName(tagName);

        // then
        assertThat(args.tagName()).isEqualTo(tagName);
    }

    @Test
    void testSince_getterAndSetter() {
        // given
        BigInteger since = BigInteger.valueOf(1000L);

        // when
        args.since(since);

        // then
        assertThat(args.since()).isEqualTo(since);
    }

    @Test
    void testUntil_getterAndSetter() {
        // given
        BigInteger until = BigInteger.valueOf(2000L);

        // when
        args.until(until);

        // then
        assertThat(args.until()).isEqualTo(until);
    }

    @Test
    void testSnapshot_getterAndSetter() {
        // given
        Timestamp snapshot = new Timestamp(System.currentTimeMillis());

        // when
        args.snapshot(snapshot);

        // then
        assertThat(args.snapshot()).isEqualTo(snapshot);
    }

    @Test
    void testMode_getterAndSetter() {
        // given
        IovModeEnum mode = IovModeEnum.IOVS;

        // when
        args.mode(mode);

        // then
        assertThat(args.mode()).isEqualTo(mode);
    }

    @Test
    void testHash_getterAndSetter() {
        // given
        String hash = "abc123hash";

        // when
        args.hash(hash);

        // then
        assertThat(args.hash()).isEqualTo(hash);
    }

    @Test
    void testTimeformat_getterAndSetter() {
        // given
        String timeformat = "run-lumi";

        // when
        args.timeformat(timeformat);

        // then
        assertThat(args.timeformat()).isEqualTo(timeformat);
    }

    @Test
    void testFluentAPI_chainingSetters() {
        // when - chain multiple setters
        IovQueryArgs result = args
                .tagName("tag1")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L))
                .mode(IovModeEnum.RANGES)
                .hash("hash123")
                .timeformat("time");

        // then - verify chaining returns the same object
        assertThat(result).isSameAs(args);
        assertThat(args.tagName()).isEqualTo("tag1");
        assertThat(args.since()).isEqualTo(BigInteger.valueOf(100L));
        assertThat(args.until()).isEqualTo(BigInteger.valueOf(200L));
        assertThat(args.mode()).isEqualTo(IovModeEnum.RANGES);
        assertThat(args.hash()).isEqualTo("hash123");
        assertThat(args.timeformat()).isEqualTo("time");
    }

    // ========== checkArgsNull Tests ==========

    @Test
    void testCheckArgsNull_withValidTagAndBothSinceUntil_shouldReturnFalse() {
        // given
        args.tagName("test-tag")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isFalse();
        assertThat(args.mode()).isNull(); // mode should not be changed
    }

    @Test
    void testCheckArgsNull_withValidTagAndOnlySince_shouldReturnFalseAndSetModeToAT() {
        // given
        args.tagName("test-tag")
                .since(BigInteger.valueOf(100L));

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isFalse();
        assertThat(args.mode()).isEqualTo(IovModeEnum.AT); // mode should be set to AT
    }

    @Test
    void testCheckArgsNull_withNullTagName_shouldReturnTrue() {
        // given
        args.since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));
        // tagName is null

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testCheckArgsNull_withTagContainingPercent_shouldReturnTrue() {
        // given
        args.tagName("test%tag")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testCheckArgsNull_withMonitorMethod_andTagContainingPercent_shouldReturnFalse() {
        // given - MONITOR method allows wildcards
        args.tagName("test%tag")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("MONITOR");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testCheckArgsNull_withMonitorMethod_caseInsensitive() {
        // given
        args.tagName("test%tag")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("monitor");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testCheckArgsNull_withNullTagAndNullHash_shouldReturnTrue() {
        // given - both tagName and hash are null
        args.since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testCheckArgsNull_withNullTagButValidHash_shouldReturnTrue() {
        // given - tagName is null but hash is present
        // Note: the first check in checkArgsNull fails if tagName is null (unless method is MONITOR)
        args.hash("abc123")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isTrue(); // fails because tagName is null
    }

    @Test
    void testCheckArgsNull_withNullSinceAndUntil_shouldReturnTrue() {
        // given
        args.tagName("test-tag");
        // since and until are null

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testCheckArgsNull_withOnlyUntil_shouldReturnTrue() {
        // given
        args.tagName("test-tag")
                .until(BigInteger.valueOf(200L));
        // since is null

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testCheckArgsNull_withNullMethod_shouldWorkNormally() {
        // given
        args.tagName("test-tag")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testCheckArgsNull_modePreservation_whenAlreadySet() {
        // given - mode already set
        args.tagName("test-tag")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L))
                .mode(IovModeEnum.RANGES);

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isFalse();
        assertThat(args.mode()).isEqualTo(IovModeEnum.RANGES); // mode should be preserved
    }

    @Test
    void testCheckArgsNull_modeOverwrite_whenOnlySinceProvided() {
        // given - mode already set, but only since is provided
        args.tagName("test-tag")
                .since(BigInteger.valueOf(100L))
                .mode(IovModeEnum.RANGES);

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isFalse();
        assertThat(args.mode()).isEqualTo(IovModeEnum.AT); // mode should be overwritten to AT
    }

    @Test
    void testCheckArgsNull_withHashAndOnlySince_shouldReturnTrue() {
        // given - hash instead of tagName, but tagName is null
        // Note: checkArgsNull fails on first check when tagName is null (unless method is MONITOR)
        args.hash("hash123")
                .since(BigInteger.valueOf(100L));

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isTrue(); // fails because tagName is null
    }

    @Test
    void testCheckArgsNull_withHashAndMonitorMethod_shouldReturnFalse() {
        // given - hash with MONITOR method allows null tagName
        args.hash("hash123")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("MONITOR");

        // then
        assertThat(result).isFalse(); // passes because MONITOR allows null tagName
    }

    @Test
    void testCheckArgsNull_withHashMonitorAndOnlySince_shouldReturnFalseAndSetModeToAT() {
        // given - hash with MONITOR method and only since
        args.hash("hash123")
                .since(BigInteger.valueOf(100L));

        // when
        boolean result = args.checkArgsNull("MONITOR");

        // then
        assertThat(result).isFalse();
        assertThat(args.mode()).isEqualTo(IovModeEnum.AT); // mode should be set to AT
    }

    @Test
    void testCheckArgsNull_withBothTagAndHash_preferTag() {
        // given - both tag and hash are present
        args.tagName("test-tag")
                .hash("hash123")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // when
        boolean result = args.checkArgsNull("GET");

        // then
        assertThat(result).isFalse(); // should pass because tagName is valid
    }

    @Test
    void testSerializable_instanceCreation() {
        // given
        args.tagName("tag1")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L))
                .mode(IovModeEnum.IOVS);

        // then - verify object is created and fields are accessible
        assertThat(args).isNotNull();
        assertThat(args).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    void testEqualsAndHashCode_withSameValues() {
        // given
        IovQueryArgs args1 = new IovQueryArgs()
                .tagName("tag1")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        IovQueryArgs args2 = new IovQueryArgs()
                .tagName("tag1")
                .since(BigInteger.valueOf(100L))
                .until(BigInteger.valueOf(200L));

        // then - Lombok @Data generates equals and hashCode
        assertThat(args1).isEqualTo(args2);
        assertThat(args1.hashCode()).isEqualTo(args2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_withDifferentValues() {
        // given
        IovQueryArgs args1 = new IovQueryArgs()
                .tagName("tag1")
                .since(BigInteger.valueOf(100L));

        IovQueryArgs args2 = new IovQueryArgs()
                .tagName("tag2")
                .since(BigInteger.valueOf(100L));

        // then
        assertThat(args1).isNotEqualTo(args2);
        assertThat(args1.hashCode()).isNotEqualTo(args2.hashCode());
    }

    @Test
    void testToString_containsFieldValues() {
        // given
        args.tagName("test-tag")
                .since(BigInteger.valueOf(100L))
                .mode(IovModeEnum.IOVS);

        // when
        String toString = args.toString();

        // then - Lombok @Data generates toString
        assertThat(toString).contains("test-tag");
        assertThat(toString).contains("100");
        assertThat(toString).contains("IOVS");
    }
}

