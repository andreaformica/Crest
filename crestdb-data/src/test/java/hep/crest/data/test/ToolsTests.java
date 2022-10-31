/**
 *
 */
package hep.crest.data.test;

import hep.crest.data.config.CrestProperties;
import hep.crest.data.config.CrestTableNames;
import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbInternalException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.PayloadEncodingException;
import hep.crest.data.handlers.DateFormatterHandler;
import hep.crest.data.handlers.HashGenerator;
import hep.crest.data.handlers.PayloadHandler;
import hep.crest.data.repositories.DataGeneral;
import hep.crest.data.repositories.externals.SqlRequests;
import hep.crest.data.test.tools.DataGenerator;
import hep.crest.data.utils.RunIovConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author formica
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ToolsTests {

    private static final Logger log = LoggerFactory.getLogger(ToolsTests.class);

    @BeforeAll
    public static void setUp() {
        final Path bpath = Paths.get("/tmp/cdms");
        if (!bpath.toFile().exists()) {
            try {
                Files.createDirectories(bpath);
            }
            catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRunIovTools() throws Exception {
        final BigInteger nullbi = null;
        final long coolmaxdate = RunIovConverter.COOL_MAX_DATE;
        final long coolmaxrun = RunIovConverter.COOL_MAX_RUN;
        final BigDecimal cmdmaxdata = new BigDecimal(coolmaxdate);
        final BigDecimal cmdmaxrun = new BigDecimal(coolmaxrun);
        final BigDecimal runlumi = RunIovConverter.getCoolRunLumi("222222", "100");

        assertThat(RunIovConverter.getTime(nullbi)).isNull();
        // Test getRun(BigInteger)
        assertThat(RunIovConverter.getRun(nullbi)).isNull();
        assertThat(RunIovConverter.getRun(cmdmaxdata.toBigInteger())).isPositive();
        assertThat(RunIovConverter.getRun(cmdmaxrun.toBigInteger())).isPositive();
        assertThat(RunIovConverter.getRun(runlumi.toBigInteger())).isEqualTo(222222L);

        // Test getRun(Long)
        assertThat(RunIovConverter.getRun((Long) null)).isNull();
        assertThat(RunIovConverter.getRun(coolmaxrun)).isPositive();
        assertThat(RunIovConverter.getRun(coolmaxdate)).isPositive();
        // getCoolRun
        assertThat(RunIovConverter.getCoolRun("222222")).isGreaterThan(new BigDecimal(222222L));
        assertThat(RunIovConverter.getCoolRun("INF")).isGreaterThan(new BigDecimal(222222L));
        assertThat(RunIovConverter.getCoolRun(null)).isNull();

        // getLumi(BigInteger)
        assertThat(RunIovConverter.getLumi(nullbi)).isNull();
        assertThat(RunIovConverter.getLumi(cmdmaxdata.toBigInteger())).isGreaterThanOrEqualTo(0);
        assertThat(RunIovConverter.getLumi(runlumi.toBigInteger())).isEqualTo(100L);

        // getLumi(Long)
        assertThat(RunIovConverter.getLumi((Long) null)).isNull();

        // getTime(BigInteger)
        assertThat(RunIovConverter.getTime(cmdmaxdata.toBigInteger())).isPositive();

        // getCoolRunLumi(String, String)
        assertThat(RunIovConverter.getCoolRunLumi(null, "100")).isNull();
        assertThat(RunIovConverter.getCoolRunLumi("1000", "100"))
                .isGreaterThan(new BigDecimal(1000000L));
        // getCoolRunLumi(Long, Long)
        assertThat(RunIovConverter.getCoolRunLumi(100L, null))
                .isGreaterThanOrEqualTo(new BigDecimal(100L));

        // getCoolTimeString(Long, iovbase)
        final String cooltimestr = RunIovConverter.getCoolTimeString(runlumi.longValue(), "run-lb");
        assertThat(cooltimestr).isNull();
        final Long nowms = Instant.now().toEpochMilli();
        assertThat(RunIovConverter.getCoolTimeString(nowms, "time")).isNotNull();

        final String coolstr = RunIovConverter.getCoolTimeRunLumiString(runlumi.longValue(),
                "run-lb");
        assertThat(coolstr.startsWith("222")).isTrue();
        log.info("Created cool run string : {}", coolstr);
        final BigDecimal since = RunIovConverter.getCoolTime(1500000000L, "time");
        final String cooltstr = RunIovConverter.getCoolTimeRunLumiString(since.longValue(), "time");
        log.info("Created cool time string : {}", cooltstr);
        assertThat(cooltstr.length()).isPositive();

        final String coolotherstr = RunIovConverter.getCoolTimeRunLumiString(since.longValue(),
                "event");
        log.info("Created cool time string : {}", coolotherstr);
        assertThat(coolotherstr.length()).isPositive();

    }

    @Test
    public void testHandlerTools() throws Exception {
        DataGenerator.generatePayloadData("/tmp/cdms/payloaddatahash.blob", "now for hashing");
        final File f = new File("/tmp/cdms/payloaddatahash.blob");
        final BufferedInputStream ds = new BufferedInputStream(new FileInputStream(f));
        final String hash = HashGenerator.hash(ds);
        assertThat(hash).isNotNull();

        final byte[] barr = new String("Testing some byte array").getBytes();
        final String hash2 = HashGenerator.md5Java(barr);
        assertThat(hash2).isNotNull();

        final String hash3 = HashGenerator.md5Java("Testing some byte array");
        assertThat(hash3).isEqualTo(hash2);

        final OutputStream out = new FileOutputStream(
                new File("/tmp/cdms/payloaddatahash.blob.copy"));
        final BufferedInputStream ds2 = new BufferedInputStream(new FileInputStream(f));
        final String hash4 = HashGenerator.hashoutstream(ds2, out);
        assertThat(hash).isEqualTo(hash4);

        final String hash5 = HashGenerator.shaJava(barr);
        assertThat(hash5).isNotNull();

        final String hash6 = HashGenerator.md5Spring(barr);
        assertThat(hash5).isNotNull();

        final String hash7 = HashGenerator.md5Spring("Testing some byte array");
        assertThat(hash5).isNotNull();

        // Date format handler, used in serialization classes
        final DateFormatterHandler dh = new DateFormatterHandler();
        final DateTimeFormatter dtformat = dh.getLocformatter();
        final Timestamp ts = dh.format("2011-12-03T10:15:30+01:00");
        final Long tsmsec = ts.getTime();
        final String adate = dh.format(ts);
        log.info("Received date as string {}", adate);
        assertThat(adate.equals("2011-12-03T09:15:30Z")).isTrue(); // The date is in GMT this time.
        assertThat(tsmsec).isPositive();
        assertThat(dtformat).isNotNull();

        final DateFormatterHandler dfh = new DateFormatterHandler();
        dfh.setDatePATTERN("ISO_LOCAL_DATE_TIME");
        final DateTimeFormatter df = dfh.getLocformatter();
        assertThat(df).isNotNull();
        dfh.setDatePATTERN("ISO_DATE_TIME");
        final DateTimeFormatter df1 = dfh.getLocformatter();
        assertThat(df1).isNotNull();
    }

    @Test
    public void propertyTest() {

        final CrestProperties props = new CrestProperties();
        props.setApiname("api");
        props.setAuthenticationtype("BASIC");
        props.setDumpdir("/tmp");
        props.setSchemaname("CREST");
        props.setSecurity("none");
        props.setSynchro("all");
        props.setWebstaticdir("/tmp");
        assertThat(props.toString().length()).isPositive();
        assertThat(props.getApiname()).isEqualTo("api");
        assertThat(props.getAuthenticationtype()).isEqualTo("BASIC");
        assertThat(props.getDumpdir()).isEqualTo("/tmp");
        assertThat(props.getSchemaname()).isEqualTo("CREST");
        assertThat(props.getSecurity()).isEqualTo("none");
        assertThat(props.getSynchro()).isEqualTo("all");
        assertThat(props.getWebstaticdir()).isEqualTo("/tmp");
    }


    @Test
    public void exceptionTest() {
        final NullPointerException np = new NullPointerException("null");
        final AbstractCdbServiceException es = new CdbInternalException("message");
        assertThat(es.getMessage()).contains("message");
        final AbstractCdbServiceException ees = new CdbNotFoundException("message not found", np);
        assertThat(ees.getCause()).isNotNull();
        final PayloadEncodingException e = new PayloadEncodingException("message");
        assertThat(e.getMessage()).contains("message");
        final PayloadEncodingException ee = new PayloadEncodingException(np);
        assertThat(ee.getCause()).isNotNull();
    }

    @Test
    public void payloadHandlerTest() {
        try {
            Path filepath = Paths.get("/tmp/cdms/file.blob");
            Files.write(filepath, "Some fake data".getBytes(StandardCharsets.UTF_8));
            InputStream is = new FileInputStream(filepath.toFile());
            byte[] lob = PayloadHandler.getBytesFromInputStream(is);
            assertThat(lob.length).isPositive();
            is.close();

            Path filepath2 = Paths.get("/tmp/cdms/file2.blob");
            Files.write(filepath2, "Some other fake data".getBytes(StandardCharsets.UTF_8));
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filepath2.toFile()));
            String hash = PayloadHandler.getHashFromStream(bis);
            assertThat(hash.length()).isPositive();
            bis.close();

            Path filepath2a = Paths.get("/tmp/cdms/file2a.blob");
            Files.write(filepath2a, "Some fake data".getBytes(StandardCharsets.UTF_8));
            InputStream iis = new FileInputStream(filepath2a.toFile());
            PayloadHandler.saveStreamToFile(iis, "/tmp/cdms/tofill.lob");
            Path filepath2b = Paths.get("/tmp/cdms/tofill.lob");
            assertThat(filepath2b.toFile().length()).isPositive();

            Path filepath3 = Paths.get("/tmp/cdms/file3.blob");
            Files.write(filepath3, "Some new fake data".getBytes(StandardCharsets.UTF_8));
            BufferedInputStream bis3 = new BufferedInputStream(new FileInputStream(filepath3.toFile()));
            String hash3 = PayloadHandler.saveToFileGetHash(bis3, "/tmp/cdms/tofillhash.lob");
            assertThat(hash3.length()).isPositive();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tableNamesTest() {
        final CrestTableNames tn = new CrestTableNames();
        tn.setDefaultTablename("CREST");
        assertThat(tn.getDefaultTablename()).isEqualTo("CREST");
        assertThat(tn.getIovTableName()).contains("IOV");
        assertThat(tn.getPayloadTableName()).contains("PAYLOAD");
        assertThat(tn.getTagTableName()).contains("TAG");
        assertThat(tn.getTagMetaTableName()).contains("TAG");
        assertThat(tn.tablename("GlobalTag")).contains("GLOBAL");
        assertThat(tn.tablename("Pippo")).isEmpty();
    }

    @Test
    public void dataGeneralTest() {
        final CrestTableNames tn = new CrestTableNames();
        final DataGeneral dg = new DataGeneral(null);
        dg.setCrestTableNames(tn);
        assertThat(dg).isNotNull();
    }

    @Test
    public void staticTest() {
        String sql = SqlRequests.getDataQuery("PAYLOAD");
        assertTrue(sql.contains("DATA"));
        sql = SqlRequests.getUpdateInfoQuery("PAYLOAD_STREAMER_DATA");
        assertTrue(sql.contains("UPDATE"));
        sql = SqlRequests.getRangeIovPayloadQuery("IOV", "PAYLOAD");
        assertTrue(sql.contains("SELECT"));
        sql = SqlRequests.getDeleteQuery("PAYLOAD");
        assertTrue(sql.contains("DELETE"));
        sql = SqlRequests.getInfoDataQuery("PAYLOAD_STREAMER_DATA");
        assertTrue(sql.contains("STREAMER_INFO"));

    }
}
