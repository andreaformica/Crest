package hep.crest.data.test;


import hep.crest.data.handlers.PayloadHandler;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.GlobalTagRepository;
import hep.crest.data.repositories.PayloadDataBaseCustom;
import hep.crest.data.repositories.TagMetaDataBaseCustom;
import hep.crest.data.repositories.TagRepository;
import hep.crest.data.test.tools.DataGenerator;
import hep.crest.swagger.model.PayloadDto;
import hep.crest.swagger.model.TagMetaDto;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-postgres.yml")
@ActiveProfiles("postgres")
@ContextConfiguration(initializers = {RepositoryPostgresTests.Initializer.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepositoryPostgresTests {

    private static final Logger log = LoggerFactory.getLogger(RepositoryPostgresTests.class);

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    @Autowired
    private GlobalTagRepository globaltagrepository;

    @Autowired
    @Qualifier("payloaddatadbrepo")
    private PayloadDataBaseCustom repobean;

    @Autowired
    private TagRepository tagrepository;

    @Autowired
    @Qualifier("tagmetarepo")
    private TagMetaDataBaseCustom tagmetarepobean;

    @Autowired
    @Qualifier("dataSource")
    private DataSource mainDataSource;

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());

//          if (postgreSQLContainer != null) {
//              // We initialize data source same way as before
//              final DataSource dataSource = initializeDataSource(postgreSQLContainer);
//              configurableApplicationContext.getBeanFactory().registerSingleton("dataSource",
//           dataSource);
        }
    }

    @Before
    public void setUp() {
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
        final Path cpath = Paths.get("/tmp/crest-dump");
        if (!cpath.toFile().exists()) {
            try {
                Files.createDirectories(cpath);
            }
            catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test1_GlobalTags() throws Exception {
        final GlobalTag gtag = DataGenerator.generateGlobalTag("MY-TEST-GT-01");
        GlobalTag saved = globaltagrepository.save(gtag);
        log.info("Save global tag {}", saved);
        assertThat(saved).isNotNull();

    }

    @Test
    public void test2_Payload() throws Exception {
        // This is a trick, we do not have a postgresql connection
        //final PayloadDataBaseCustom repobean = new PayloadDataPostgresImpl(mainDataSource);
        final Instant now = Instant.now();
        final Date time = new Date(now.toEpochMilli());
        final PayloadDto dto = DataGenerator.generatePayloadDto("myhashpg1", "mydata", "mystreamer",
                "test", time);
        log.info("Save payload {}", dto);
        if (dto.getSize() == null) {
            dto.setSize(dto.getData().length);
        }
        log.info("Add size of payload data {}", dto.getSize());
        final PayloadDto saved = repobean.save(dto);
        assertThat(saved).isNotNull();
        final PayloadDto loaded = repobean.find("myhashpg1");
        assertThat(loaded.toString().length()).isPositive();

        DataGenerator.generatePayloadData("/tmp/cdms/payloadataspg.blob", " for postgres");
        final File f = new File("/tmp/cdms/payloadataspg.blob");
        InputStream ds = new BufferedInputStream(new FileInputStream(f));

        dto.hash("mynewhashpg1");
        final PayloadDto savedfromblob = repobean.save(dto, ds);
        assertThat(savedfromblob.toString().length()).isPositive();
        if (ds != null) {
            ds.close();
        }
        final InputStream loadedblob = repobean.findData(savedfromblob.getHash());
        assertThat(loadedblob.available()).isPositive();
        final PayloadDto loadedmeta = repobean.findMetaInfo(savedfromblob.getHash());
        assertThat(new String(loadedmeta.getStreamerInfo())).isEqualTo("mystreamer");
        repobean.delete(savedfromblob.getHash());

        ds = new BufferedInputStream(new FileInputStream(f));
        PayloadHandler.saveStreamToFile(ds, "/tmp/cdms/payloadatacopypg.blob");
        final File f1 = new File("/tmp/cdms/payloadatacopypg.blob");
        final InputStream ds1 = new BufferedInputStream(new FileInputStream(f1));
        final byte[] barr = PayloadHandler.getBytesFromInputStream(ds1);
        assertThat(barr).isNotEmpty();
        if (ds1 != null) {
            ds1.close();
        }
        final PayloadDto loadedblob1 = repobean.find(savedfromblob.getHash());
        assertThat(loadedblob1).isNull();
    }

    @Test
    public void test1_Tags() throws Exception {
        final Instant now = Instant.now();
        final Date time = new Date(now.toEpochMilli());

        final Tag mtag = DataGenerator.generateTag("A-TEST-FOR-META", "test");
        final Tag savedtag = tagrepository.save(mtag);
        final TagMetaDto metadto = DataGenerator.generateTagMetaDto("A-TEST-FOR-META", "{ \"key\" : \"val\" }", time);
        final TagMetaDto savedmeta = tagmetarepobean.save(metadto);
        assertThat(savedmeta).isNotNull();
        assertThat(savedmeta.toString().length()).isPositive();
        assertThat(savedmeta.getTagName()).isEqualTo(savedtag.getName());

        final TagMetaDto storedmeta = tagmetarepobean.find(savedmeta.getTagName());
        assertThat(storedmeta).isNotNull();
        storedmeta.tagInfo("{ \"key1\" : \"val1\" }");
        final TagMetaDto updmeta = tagmetarepobean.update(storedmeta);
        assertThat(updmeta).isNotNull();
        tagmetarepobean.delete(updmeta.getTagName());
        assertThat(updmeta).isNotNull();
        final TagMetaDto deletedmeta = tagmetarepobean.find(updmeta.getTagName());
        assertThat(deletedmeta).isNull();
    }

}
