package hep.crest.server;

import hep.crest.server.repositories.triggerdb.ITriggerDb;
import hep.crest.server.repositories.triggerdb.TriggerDb;
import hep.crest.server.repositories.triggerdb.UrlComponents;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for TriggerDb functionality using an embedded H2 database.
 * This tests the TriggerDb class without requiring an actual Oracle connection.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@Slf4j
public class TestTriggerDb {

    private EmbeddedDatabase dataSource;
    private ITriggerDb triggerDb;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Create an embedded H2 database with a unique name for each test
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .build();

        jdbcTemplate = new JdbcTemplate(dataSource);

        // Create the schema that mimics the Oracle TriggerDB structure
        createTriggerDbSchema();

        // Insert test data
        insertTestData();

        // Create the TriggerDb instance
        triggerDb = new TriggerDb(dataSource);
    }

    @AfterEach
    public void tearDown() {
        // Shutdown the embedded database after each test
        if (dataSource != null) {
            dataSource.shutdown();
        }
    }

    /**
     * Create the schema tables needed for TriggerDb tests.
     */
    private void createTriggerDbSchema() {
        // Create schema for ATLAS_CONF_TRIGGER_RUN3 tables
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3");

        // Create L1_PRESCALE_SET table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.L1_PRESCALE_SET (
                L1PS_ID BIGINT PRIMARY KEY,
                L1PS_DATA BLOB
            )
        """);

        // Create HLT_PRESCALE_SET table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.HLT_PRESCALE_SET (
                HPS_ID BIGINT PRIMARY KEY,
                HPS_DATA BLOB
            )
        """);

        // Create L1_BUNCH_GROUP_SET table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.L1_BUNCH_GROUP_SET (
                L1BGS_ID BIGINT PRIMARY KEY,
                L1BGS_DATA BLOB
            )
        """);

        // Create SUPER_MASTER_TABLE
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.SUPER_MASTER_TABLE (
                SMT_ID BIGINT PRIMARY KEY,
                SMT_HLT_MENU_ID BIGINT,
                SMT_L1_MENU_ID BIGINT,
                SMT_HLT_JOBOPTIONS_ID BIGINT
            )
        """);

        // Create HLT_MENU table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.HLT_MENU (
                HTM_ID BIGINT PRIMARY KEY,
                HTM_DATA BLOB
            )
        """);

        // Create L1_MENU table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.L1_MENU (
                L1TM_ID BIGINT PRIMARY KEY,
                L1TM_DATA BLOB
            )
        """);

        // Create HLT_MONITORING_GROUPS table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.HLT_MONITORING_GROUPS (
                HMG_ID BIGINT PRIMARY KEY,
                HMG_DATA BLOB,
                HMG_IN_USE INT,
                HMG_HLT_MENU_ID BIGINT
            )
        """);

        // Create HLT_JOBOPTIONS table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ATLAS_CONF_TRIGGER_RUN3.HLT_JOBOPTIONS (
                HJO_ID BIGINT PRIMARY KEY,
                HJO_DATA BLOB
            )
        """);
    }

    /**
     * Insert test data into the mock database.
     */
    private void insertTestData() {
        // Insert L1 Prescale Set data
        byte[] l1psData = "L1 Prescale Set Test Data".getBytes(StandardCharsets.UTF_8);
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.L1_PRESCALE_SET (L1PS_ID, L1PS_DATA) VALUES (?, ?)",
                1001L, l1psData);

        // Insert HLT Prescale Set data
        byte[] hpsData = "HLT Prescale Set Test Data".getBytes(StandardCharsets.UTF_8);
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.HLT_PRESCALE_SET (HPS_ID, HPS_DATA) VALUES (?, ?)",
                2001L, hpsData);

        // Insert L1 Bunch Group Set data
        byte[] bgsData = "L1 Bunch Group Set Test Data".getBytes(StandardCharsets.UTF_8);
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.L1_BUNCH_GROUP_SET (L1BGS_ID, L1BGS_DATA) VALUES (?, ?)",
                3001L, bgsData);

        // Insert HLT Menu data
        byte[] htmData = "HLT Menu Test Data".getBytes(StandardCharsets.UTF_8);
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.HLT_MENU (HTM_ID, HTM_DATA) VALUES (?, ?)",
                4001L, htmData);

        // Insert L1 Menu data
        byte[] l1mData = "L1 Menu Test Data".getBytes(StandardCharsets.UTF_8);
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.L1_MENU (L1TM_ID, L1TM_DATA) VALUES (?, ?)",
                5001L, l1mData);

        // Insert Super Master Table entry linking menus
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.SUPER_MASTER_TABLE "
                + "(SMT_ID, SMT_HLT_MENU_ID, SMT_L1_MENU_ID, SMT_HLT_JOBOPTIONS_ID) VALUES (?, ?, ?, ?)",
                100L, 4001L, 5001L, 6001L);

        // Insert HLT Monitoring Groups data
        byte[] hmgData = "HLT Monitoring Groups Test Data".getBytes(StandardCharsets.UTF_8);
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.HLT_MONITORING_GROUPS "
                + "(HMG_ID, HMG_DATA, HMG_IN_USE, HMG_HLT_MENU_ID) VALUES (?, ?, ?, ?)",
                7001L, hmgData, 1, 4001L);

        // Insert HLT JobOptions data
        byte[] hjoData = "HLT JobOptions Test Data".getBytes(StandardCharsets.UTF_8);
        jdbcTemplate.update(
                "INSERT INTO ATLAS_CONF_TRIGGER_RUN3.HLT_JOBOPTIONS (HJO_ID, HJO_DATA) VALUES (?, ?)",
                6001L, hjoData);
    }

    @Test
    public void testParseValidUrl() {
        log.info("=======> testParseValidUrl");

        String url = "triggerdb://CONF_DATA_RUN3/L1PS/1001";
        UrlComponents components = triggerDb.parseUrl(url);

        assertThat(components).isNotNull();
        assertThat(components.getSchema()).isEqualTo("CONF_DATA_RUN3");
        assertThat(components.getTable()).isEqualTo("L1PS");
        assertThat(components.getId()).isEqualTo(1001L);
    }

    @Test
    public void testParseInvalidUrl() {
        log.info("=======> testParseInvalidUrl");

        String invalidUrl = "invalid://url/format";

        assertThatThrownBy(() -> triggerDb.parseUrl(invalidUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid URL");
    }

    @Test
    public void testParseUrlWithDifferentSchemas() {
        log.info("=======> testParseUrlWithDifferentSchemas");

        // Test various valid schemas
        String[] schemas = {"CONF_DATA_RUN3", "CONF_MC_RUN3", "CONF_REPR_RUN3",
                            "CONF_DATA_RUN4", "CONF_MC_RUN4", "CONF_DATA_RUN2"};

        for (String schema : schemas) {
            String url = String.format("triggerdb://%s/L1PS/123", schema);
            UrlComponents components = triggerDb.parseUrl(url);

            assertThat(components.getSchema()).isEqualTo(schema);
            log.info("Successfully parsed URL with schema: {}", schema);
        }
    }

    @Test
    public void testGetTriggerDBDataL1PrescaleSet() throws IOException {
        log.info("=======> testGetTriggerDBDataL1PrescaleSet");

        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "L1PS", 1001L);
        InputStream result = triggerDb.getTriggerDBData(components);

        assertThat(result).isNotNull();
        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("L1 Prescale Set Test Data");
        result.close();
    }

    @Test
    public void testGetTriggerDBDataHLTPrescaleSet() throws IOException {
        log.info("=======> testGetTriggerDBDataHLTPrescaleSet");

        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "HLTPS", 2001L);
        InputStream result = triggerDb.getTriggerDBData(components);

        assertThat(result).isNotNull();
        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("HLT Prescale Set Test Data");
        result.close();
    }

    @Test
    public void testGetTriggerDBDataBunchGroupSet() throws IOException {
        log.info("=======> testGetTriggerDBDataBunchGroupSet");

        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "BGS", 3001L);
        InputStream result = triggerDb.getTriggerDBData(components);

        assertThat(result).isNotNull();
        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("L1 Bunch Group Set Test Data");
        result.close();
    }

    @Test
    public void testGetTriggerDBDataHLTMenu() throws IOException {
        log.info("=======> testGetTriggerDBDataHLTMenu");

        // HLTM uses a join with SUPER_MASTER_TABLE, so we query using SMT_ID
        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "HLTM", 100L);
        InputStream result = triggerDb.getTriggerDBData(components);

        assertThat(result).isNotNull();
        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("HLT Menu Test Data");
        result.close();
    }

    @Test
    public void testGetTriggerDBDataL1Menu() throws IOException {
        log.info("=======> testGetTriggerDBDataL1Menu");

        // L1M uses a join with SUPER_MASTER_TABLE, so we query using SMT_ID
        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "L1M", 100L);
        InputStream result = triggerDb.getTriggerDBData(components);

        assertThat(result).isNotNull();
        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("L1 Menu Test Data");
        result.close();
    }

    @Test
    public void testGetTriggerDBDataMonitoringGroups() throws IOException {
        log.info("=======> testGetTriggerDBDataMonitoringGroups");

        // MGS uses a join with SUPER_MASTER_TABLE
        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "MGS", 100L);
        InputStream result = triggerDb.getTriggerDBData(components);

        assertThat(result).isNotNull();
        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("HLT Monitoring Groups Test Data");
        result.close();
    }

    @Test
    public void testGetTriggerDBDataJobOptions() throws IOException {
        log.info("=======> testGetTriggerDBDataJobOptions");

        // JO uses a join with SUPER_MASTER_TABLE
        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "JO", 100L);
        InputStream result = triggerDb.getTriggerDBData(components);

        assertThat(result).isNotNull();
        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("HLT JobOptions Test Data");
        result.close();
    }

    @Test
    public void testGetTriggerDBDataNotFound() {
        log.info("=======> testGetTriggerDBDataNotFound");

        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "L1PS", 99999L);
        InputStream result = triggerDb.getTriggerDBData(components);

        // Should return null when data is not found
        assertThat(result).isNull();
    }

    @Test
    public void testGetTriggerDBDataUnknownSchema() {
        log.info("=======> testGetTriggerDBDataUnknownSchema");

        UrlComponents components = new UrlComponents("UNKNOWN_SCHEMA", "L1PS", 1001L);

        assertThatThrownBy(() -> triggerDb.getTriggerDBData(components))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown schema");
    }

    @Test
    public void testGetTriggerDBDataUnknownTable() {
        log.info("=======> testGetTriggerDBDataUnknownTable");

        UrlComponents components = new UrlComponents("CONF_DATA_RUN3", "UNKNOWN_TABLE", 1001L);

        assertThatThrownBy(() -> triggerDb.getTriggerDBData(components))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown table");
    }

    @Test
    public void testUrlComponentsEquality() {
        log.info("=======> testUrlComponentsEquality");

        UrlComponents comp1 = new UrlComponents("CONF_DATA_RUN3", "L1PS", 1001L);
        UrlComponents comp2 = new UrlComponents("CONF_DATA_RUN3", "L1PS", 1001L);
        UrlComponents comp3 = new UrlComponents("CONF_DATA_RUN3", "L1PS", 1002L);

        assertThat(comp1).isEqualTo(comp2);
        assertThat(comp1).isNotEqualTo(comp3);
    }

    @Test
    public void testFullWorkflowParseAndFetch() throws IOException {
        log.info("=======> testFullWorkflowParseAndFetch");

        // Test the full workflow: parse URL then fetch data
        String url = "triggerdb://CONF_DATA_RUN3/L1PS/1001";

        UrlComponents components = triggerDb.parseUrl(url);
        assertThat(components).isNotNull();

        InputStream result = triggerDb.getTriggerDBData(components);
        assertThat(result).isNotNull();

        String data = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("L1 Prescale Set Test Data");
        result.close();

        log.info("Full workflow completed successfully for URL: {}", url);
    }
}

