package hep.crest.server;


import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.PayloadData;
import hep.crest.server.data.pojo.PayloadInfoData;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.pojo.TagMeta;
import hep.crest.server.data.repositories.PayloadDataRepository;
import hep.crest.server.data.repositories.PayloadInfoDataRepository;
import hep.crest.server.data.repositories.PayloadRepository;
import hep.crest.server.data.repositories.TagMetaRepository;
import hep.crest.server.data.repositories.TagRepository;
import hep.crest.server.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application-postgres.yml")
@ActiveProfiles("postgres")
@Testcontainers
@Slf4j
@ContextConfiguration(initializers = {RepositoryPostgresTests.Initializer.class})
public class RepositoryPostgresTests {

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
      .withDatabaseName("integration-tests-db")
      .withUsername("sa")
      .withPassword("sa");

    @Autowired
    private PayloadRepository payloadRepository;
    @Autowired
    private PayloadDataRepository payloadDataRepository;
    @Autowired
    private PayloadInfoDataRepository payloadInfoDataRepository;

    @Autowired
    private TagRepository tagrepository;

    @Autowired
    private TagMetaRepository tagMetaRepository;

    @Autowired
    @Qualifier("dataSource")
    private DataSource mainDataSource;

    private RandomGenerator rndgen = new RandomGenerator();

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

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
    public void test1_Payload() throws Exception {
    // This is a trick, we do not have a postgresql connection
        //final PayloadDataBaseCustom repobean = new PayloadDataPostgresImpl(mainDataSource);
        final Instant now = Instant.now();
        Payload entity = (Payload) rndgen.generate(Payload.class);
        PayloadData content = new PayloadData();
        content.hash(entity.hash());
        PayloadInfoData streamer = new PayloadInfoData();
        streamer.hash(entity.hash());
        streamer.streamerInfo("{key: val}".getBytes(StandardCharsets.UTF_8));
        log.info("Save payload {}", entity);
        byte[] dataBlob = "This is a very long blob".getBytes(StandardCharsets.UTF_8);
        Payload saved  = payloadRepository.save(entity);
        assertTrue(saved != null);
        payloadDataRepository.saveData(content.hash(), new ByteArrayInputStream(dataBlob), dataBlob.length);
        PayloadInfoData savedStreamer = payloadInfoDataRepository.save(streamer);
        assertTrue(savedStreamer != null);
    }

    @Test
    public void test1_Tags() throws Exception {
        final Instant now = Instant.now();
        Tag mtag = (Tag) rndgen.generate(Tag.class);
        mtag.insertionTime(Timestamp.from(now));
        mtag.modificationTime(Timestamp.from(now));
        log.info("Save tag {}", mtag);
        final Tag savedtag = tagrepository.save(mtag);
        assertEquals(savedtag.name(), mtag.name());

        TagMeta meta = (TagMeta) rndgen.generate(TagMeta.class);
        meta.tagName(savedtag.name());
        meta.tagInfo("{\"channels\": [0: \"name0\"]}".getBytes(StandardCharsets.UTF_8));
        final TagMeta savedmeta = tagMetaRepository.save(meta);
        log.info("Save tag meta information {}", savedmeta);
        assertTrue(savedmeta != null);

        final Optional<TagMeta> storedmetaopt = tagMetaRepository.findByTagName(savedmeta.tagName());
        assertTrue(storedmetaopt.isPresent());

    }
}
