package hep.crest.server.data.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for RunIovConverter utility.
 * Tests conversions between COOL time, run/lumi block, and milliseconds.
 */
class RunIovConverterTest {

    @Test
    void testGetRun_withValidBigInteger() {
        // given - run 100, lumi 0 => 100 << 32
        BigInteger coolTime = BigInteger.valueOf(100L).shiftLeft(32);

        // when
        Long run = RunIovConverter.getRun(coolTime);

        // then
        assertThat(run).isEqualTo(100L);
    }

    @Test
    void testGetRun_withRunAndLumiBlock() {
        // given - run 250, lumi 50
        BigInteger run = BigInteger.valueOf(250L).shiftLeft(32);
        BigInteger lumi = BigInteger.valueOf(50L);
        BigInteger coolTime = run.or(lumi);

        // when
        Long extractedRun = RunIovConverter.getRun(coolTime);

        // then
        assertThat(extractedRun).isEqualTo(250L);
    }

    @Test
    void testGetRun_withNull() {
        // when
        Long run = RunIovConverter.getRun((BigInteger) null);

        // then
        assertThat(run).isNull();
    }

    @Test
    void testGetRun_withMaxDate() {
        // given
        BigInteger maxDate = BigInteger.valueOf(RunIovConverter.COOL_MAX_DATE);

        // when
        Long run = RunIovConverter.getRun(maxDate);

        // then
        assertThat(run).isEqualTo(RunIovConverter.COOL_MAX_DATE);
    }

    @Test
    void testGetRun_withLongParameter() {
        // given
        Long coolTime = BigInteger.valueOf(100L).shiftLeft(32).longValue();

        // when
        Long run = RunIovConverter.getRun(coolTime);

        // then
        assertThat(run).isEqualTo(100L);
    }

    @Test
    void testGetRun_withNullLong() {
        // when
        Long run = RunIovConverter.getRun((Long) null);

        // then
        assertThat(run).isNull();
    }

    @Test
    void testGetCoolRun_withValidRunNumber() {
        // given
        String runNumber = "100";

        // when
        BigDecimal coolRun = RunIovConverter.getCoolRun(runNumber);

        // then
        BigDecimal expected = new BigDecimal(BigInteger.valueOf(100L).shiftLeft(32));
        assertThat(coolRun).isEqualByComparingTo(expected);
    }

    @Test
    void testGetCoolRun_withInf() {
        // when
        BigDecimal coolRun = RunIovConverter.getCoolRun("inf");

        // then
        assertThat(coolRun).isEqualByComparingTo(new BigDecimal(RunIovConverter.COOL_MAX_DATE));
    }

    @Test
    void testGetCoolRun_withINF() {
        // when
        BigDecimal coolRun = RunIovConverter.getCoolRun("INF");

        // then
        assertThat(coolRun).isEqualByComparingTo(new BigDecimal(RunIovConverter.COOL_MAX_DATE));
    }

    @Test
    void testGetCoolRun_withNull() {
        // when
        BigDecimal coolRun = RunIovConverter.getCoolRun(null);

        // then
        assertThat(coolRun).isNull();
    }

    @Test
    void testGetLumi_withValidBigInteger() {
        // given - run 100, lumi 50
        BigInteger run = BigInteger.valueOf(100L).shiftLeft(32);
        BigInteger lumi = BigInteger.valueOf(50L);
        BigInteger coolTime = run.or(lumi);

        // when
        Long extractedLumi = RunIovConverter.getLumi(coolTime);

        // then
        assertThat(extractedLumi).isEqualTo(50L);
    }

    @Test
    void testGetLumi_withZeroLumi() {
        // given - run 100, lumi 0
        BigInteger coolTime = BigInteger.valueOf(100L).shiftLeft(32);

        // when
        Long lumi = RunIovConverter.getLumi(coolTime);

        // then
        assertThat(lumi).isEqualTo(0L);
    }

    @Test
    void testGetLumi_withNull() {
        // when
        Long lumi = RunIovConverter.getLumi((BigInteger) null);

        // then
        assertThat(lumi).isNull();
    }

    @Test
    void testGetLumi_withMaxDate() {
        // given
        BigInteger maxDate = BigInteger.valueOf(RunIovConverter.COOL_MAX_DATE);

        // when
        Long lumi = RunIovConverter.getLumi(maxDate);

        // then
        assertThat(lumi).isEqualTo(0L);
    }

    @Test
    void testGetLumi_withLongParameter() {
        // given
        BigInteger run = BigInteger.valueOf(100L).shiftLeft(32);
        BigInteger lumiBlock = BigInteger.valueOf(75L);
        Long coolTime = run.or(lumiBlock).longValue();

        // when
        Long lumi = RunIovConverter.getLumi(coolTime);

        // then
        assertThat(lumi).isEqualTo(75L);
    }

    @Test
    void testGetLumi_withNullLong() {
        // when
        Long lumi = RunIovConverter.getLumi((Long) null);

        // then
        assertThat(lumi).isNull();
    }

    @Test
    void testGetCoolRunLumi_withStringParameters() {
        // given
        String run = "100";
        String lumi = "50";

        // when
        BigDecimal coolTime = RunIovConverter.getCoolRunLumi(run, lumi);

        // then
        BigInteger expectedRun = BigInteger.valueOf(100L).shiftLeft(32);
        BigInteger expectedLumi = BigInteger.valueOf(50L);
        BigDecimal expected = new BigDecimal(expectedRun.or(expectedLumi));
        assertThat(coolTime).isEqualByComparingTo(expected);
    }

    @Test
    void testGetCoolRunLumi_withInfString() {
        // when
        BigDecimal coolTime = RunIovConverter.getCoolRunLumi("inf", "0");

        // then
        BigInteger expectedRun = BigInteger.valueOf(RunIovConverter.COOL_MAX_RUN).shiftLeft(32);
        BigInteger expectedLumi = BigInteger.valueOf(RunIovConverter.COOL_MAX_LUMIBLOCK);
        BigDecimal expected = new BigDecimal(expectedRun.or(expectedLumi));
        assertThat(coolTime).isEqualByComparingTo(expected);
    }

    @Test
    void testGetCoolRunLumi_withNullString() {
        // when
        BigDecimal coolTime = RunIovConverter.getCoolRunLumi(null, "0");

        // then
        assertThat(coolTime).isNull();
    }

    @Test
    void testGetCoolRunLumi_withLongParameters() {
        // given
        Long run = 200L;
        Long lumi = 100L;

        // when
        BigDecimal coolTime = RunIovConverter.getCoolRunLumi(run, lumi);

        // then
        BigInteger expectedRun = BigInteger.valueOf(200L).shiftLeft(32);
        BigInteger expectedLumi = BigInteger.valueOf(100L);
        BigDecimal expected = new BigDecimal(expectedRun.or(expectedLumi));
        assertThat(coolTime).isEqualByComparingTo(expected);
    }

    @Test
    void testGetCoolRunLumi_withNullLumi() {
        // given
        Long run = 200L;

        // when
        BigDecimal coolTime = RunIovConverter.getCoolRunLumi(run, null);

        // then
        BigInteger expectedRun = BigInteger.valueOf(200L).shiftLeft(32);
        BigDecimal expected = new BigDecimal(expectedRun);
        assertThat(coolTime).isEqualByComparingTo(expected);
    }

    @Test
    void testGetCoolRunLumi_withNullRun() {
        // when
        BigDecimal coolTime = RunIovConverter.getCoolRunLumi(null, 100L);

        // then
        assertThat(coolTime).isNull();
    }

    @Test
    void testGetTime_withValidBigInteger() {
        // given - time in nanoseconds
        BigInteger nanoTime = BigInteger.valueOf(1000000000L); // 1 second in nanoseconds

        // when
        Long milliTime = RunIovConverter.getTime(nanoTime);

        // then
        assertThat(milliTime).isEqualTo(1000L); // 1 second in milliseconds
    }

    @Test
    void testGetTime_withNull() {
        // when
        Long time = RunIovConverter.getTime(null);

        // then
        assertThat(time).isNull();
    }

    @Test
    void testGetTime_withMaxDate() {
        // given
        BigInteger maxDate = BigInteger.valueOf(RunIovConverter.COOL_MAX_DATE);

        // when
        Long time = RunIovConverter.getTime(maxDate);

        // then
        assertThat(time).isEqualTo(RunIovConverter.COOL_MAX_DATE);
    }

    @Test
    void testGetCoolTimeString_withTimeBase() {
        // given
        Long time = 1000L; // 1 second in milliseconds

        // when
        String timeString = RunIovConverter.getCoolTimeString(time, "time");

        // then
        assertThat(timeString).isNotNull();
        assertThat(timeString).isNotEmpty();
    }

    @Test
    void testGetCoolTimeString_withZeroTime() {
        // when
        String timeString = RunIovConverter.getCoolTimeString(0L, "time");

        // then
        assertThat(timeString).isEqualTo("0");
    }

    @Test
    void testGetCoolTimeString_withMaxDate() {
        // when
        String timeString = RunIovConverter.getCoolTimeString(RunIovConverter.COOL_MAX_DATE, "time");

        // then
        assertThat(timeString).isEqualTo("Inf");
    }

    @Test
    void testGetCoolTimeString_withRunBase() {
        // when
        String timeString = RunIovConverter.getCoolTimeString(1000L, "run-lumi");

        // then
        assertThat(timeString).isNull();
    }

    @Test
    void testGetCoolTimeRunLumiString_withRunLumiBase() {
        // given - run 100, lumi 50
        BigInteger run = BigInteger.valueOf(100L).shiftLeft(32);
        BigInteger lumi = BigInteger.valueOf(50L);
        Long coolTime = run.or(lumi).longValue();

        // when
        String timeString = RunIovConverter.getCoolTimeRunLumiString(coolTime, "run-lumi");

        // then
        assertThat(timeString).contains("100");
        assertThat(timeString).contains("50");
        assertThat(timeString).contains("-");
    }

    @Test
    void testGetCoolTimeRunLumiString_withMaxLumiBlock() {
        // given - run 100, max lumi
        BigInteger run = BigInteger.valueOf(100L).shiftLeft(32);
        BigInteger lumi = BigInteger.valueOf(RunIovConverter.COOL_MAX_LUMIBLOCK);
        Long coolTime = run.or(lumi).longValue();

        // when
        String timeString = RunIovConverter.getCoolTimeRunLumiString(coolTime, "run-lumi");

        // then
        assertThat(timeString).contains("100");
        assertThat(timeString).contains("maxlb");
    }

    @Test
    void testGetCoolTimeRunLumiString_withTimeBase() {
        // given
        Long timeInNano = 1000000000L; // 1 second in nanoseconds

        // when
        String timeString = RunIovConverter.getCoolTimeRunLumiString(timeInNano, "time");

        // then
        assertThat(timeString).isNotNull();
        assertThat(timeString).isNotEmpty();
    }

    @Test
    void testGetCoolTimeRunLumiString_withZero() {
        // when
        String timeString = RunIovConverter.getCoolTimeRunLumiString(0L, "time");

        // then
        assertThat(timeString).isEqualTo("0");
    }

    @Test
    void testGetCoolTimeRunLumiString_withInf() {
        // when
        String timeString = RunIovConverter.getCoolTimeRunLumiString(RunIovConverter.COOL_MAX_DATE, "time");

        // then
        assertThat(timeString).isEqualTo("Inf");
    }

    @Test
    void testGetCoolTimeRunLumiString_withMaxDateRunBase() {
        // when
        String timeString = RunIovConverter.getCoolTimeRunLumiString(RunIovConverter.COOL_MAX_DATE, "run-lumi");

        // then
        assertThat(timeString).isEqualTo("Inf");
    }

    @Test
    void testGetCoolTime_withRunBase() {
        // given
        Long run = 100L;

        // when
        BigDecimal coolTime = RunIovConverter.getCoolTime(run, "run-lumi");

        // then
        BigDecimal expected = RunIovConverter.getCoolRunLumi(run, 0L);
        assertThat(coolTime).isEqualByComparingTo(expected);
    }

    @Test
    void testGetCoolTime_withTimeBase() {
        // given
        Long timeInMilli = 1000L; // 1 second

        // when
        BigDecimal coolTime = RunIovConverter.getCoolTime(timeInMilli, "time");

        // then
        BigDecimal expected = new BigDecimal(timeInMilli * RunIovConverter.TO_NANOSECONDS);
        assertThat(coolTime).isEqualByComparingTo(expected);
    }

    @Test
    void testGetCoolTime_withUnknownBase() {
        // when
        BigDecimal coolTime = RunIovConverter.getCoolTime(1000L, "unknown");

        // then
        assertThat(coolTime).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testConstants() {
        // verify important constants are set correctly
        assertThat(RunIovConverter.TO_NANOSECONDS).isEqualTo(1000000L);
        assertThat(RunIovConverter.TO_MILLISECONDS).isEqualTo(1000L);
        assertThat(RunIovConverter.COOL_MAX_DATE).isEqualTo(9223372036854775807L);
        assertThat(RunIovConverter.COOL_MAX_RUN).isEqualTo(2147483647L);
        assertThat(RunIovConverter.COOL_MAX_LUMIBLOCK).isEqualTo(4294967295L);
        assertThat(RunIovConverter.COOL_IOV_LUMI_MASK).isEqualTo(new BigInteger("00000000FFFFFFFF", 16));
    }

    @Test
    void testRoundTripConversion_RunLumiToCoolAndBack() {
        // given
        Long originalRun = 12345L;
        Long originalLumi = 678L;

        // when - convert to COOL time and back
        BigDecimal coolTime = RunIovConverter.getCoolRunLumi(originalRun, originalLumi);
        Long extractedRun = RunIovConverter.getRun(coolTime.toBigInteger());
        Long extractedLumi = RunIovConverter.getLumi(coolTime.toBigInteger());

        // then
        assertThat(extractedRun).isEqualTo(originalRun);
        assertThat(extractedLumi).isEqualTo(originalLumi);
    }

    @Test
    void testRoundTripConversion_TimeToNanoAndBack() {
        // given
        Long originalTimeInNano = 123456789000000L;

        // when
        Long timeInMilli = RunIovConverter.getTime(BigInteger.valueOf(originalTimeInNano));
        BigDecimal backToNano = new BigDecimal(timeInMilli).multiply(RunIovConverter.TO_NANOSECOND);

        // then
        assertThat(backToNano.toBigInteger()).isEqualTo(BigInteger.valueOf(originalTimeInNano));
    }
}

