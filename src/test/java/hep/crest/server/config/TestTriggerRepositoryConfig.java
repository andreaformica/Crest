package hep.crest.server.config;

import hep.crest.server.triggerdb.repositories.ITriggerDb;
import hep.crest.server.triggerdb.repositories.TriggerDb;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for TriggerRepositoryConfig.
 * This test uses the "triggerdb" profile to activate TriggerRepositoryConfig
 * and verifies that the beans are properly created.
 *
 * The test uses application-triggerdb.properties in test/resources which
 * configures an H2 in-memory database to simulate the Oracle TriggerDB.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "triggerdb"})
@Slf4j
public class TestTriggerRepositoryConfig {

    @Autowired
    @Qualifier("triggerDataSource")
    private DataSource triggerDataSource;

    @Autowired
    private ITriggerDb triggerDbRepository;

    /**
     * Test that triggerDataSource bean is properly created by TriggerRepositoryConfig.
     */
    @Test
    public void testTriggerDataSourceBeanCreated() {
        log.info("=======> testTriggerDataSourceBeanCreated");
        assertThat(triggerDataSource).isNotNull();
        log.info("TriggerDataSource bean is properly created: {}", triggerDataSource.getClass().getName());
    }

    /**
     * Test that triggerDbRepository bean is properly created by TriggerRepositoryConfig.
     */
    @Test
    public void testTriggerDbRepositoryBeanCreated() {
        log.info("=======> testTriggerDbRepositoryBeanCreated");
        assertThat(triggerDbRepository).isNotNull();
        assertThat(triggerDbRepository).isInstanceOf(TriggerDb.class);
        log.info("TriggerDbRepository bean is properly created: {}", triggerDbRepository.getClass().getName());
    }

    /**
     * Test that triggerDbRepository can parse URLs (basic functionality check).
     */
    @Test
    public void testTriggerDbRepositoryParseUrl() {
        log.info("=======> testTriggerDbRepositoryParseUrl");
        var components = triggerDbRepository.parseUrl("triggerdb://CONF_DATA_RUN3/L1PS/1001");
        assertThat(components).isNotNull();
        assertThat(components.getSchema()).isEqualTo("CONF_DATA_RUN3");
        assertThat(components.getTable()).isEqualTo("L1PS");
        assertThat(components.getId()).isEqualTo(1001L);
    }
}

