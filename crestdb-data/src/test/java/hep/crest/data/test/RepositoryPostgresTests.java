package hep.crest.data.test;


import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.PayloadData;
import hep.crest.data.pojo.PayloadInfoData;
import hep.crest.data.pojo.Tag;
import hep.crest.data.pojo.TagMeta;
import hep.crest.data.repositories.IovGroupsCustom;
import hep.crest.data.repositories.IovRepository;
import hep.crest.data.repositories.PayloadDataRepository;
import hep.crest.data.repositories.PayloadInfoDataRepository;
import hep.crest.data.repositories.PayloadRepository;
import hep.crest.data.repositories.TagMetaRepository;
import hep.crest.data.repositories.TagRepository;
import hep.crest.data.test.tools.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations="classpath:application-postgres.yml")
@ContextConfiguration(initializers = {RepositoryPostgresTests.Initializer.class})
@ActiveProfiles("postgres")
@Testcontainers
@Slf4j
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
    private IovRepository iovRepository;

    @Autowired
    private IovGroupsCustom iovGroupsCustom;

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

        Optional<Payload> found = payloadRepository.findById(saved.hash());
        assertTrue(found.isPresent());
        Optional<PayloadData> foundData = payloadDataRepository.findById(saved.hash());
        assertTrue(foundData.isPresent());
        Optional<PayloadInfoData> foundStreamer = payloadInfoDataRepository.findById(saved.hash());
        assertTrue(foundStreamer.isPresent());

        byte[] sinfo = foundStreamer.get().streamerInfo();
        assertTrue(sinfo.length > 0);
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

    @Test
    public void testGroups() throws Exception {
        final Instant now = Instant.now();
        log.info("====> testGroups: ");
        final Tag mtag = (Tag) rndgen.generate(Tag.class);
        log.info("...created tag via random gen: {}", mtag);
        mtag.insertionTime(null);
        mtag.modificationTime(null);
        final Tag savedtag = tagrepository.save(mtag);

        final IovId id = new IovId().tagName(savedtag.name()).since(BigInteger.valueOf(900L)).insertionTime(new Date());
        final Iov miov = (Iov) rndgen.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final Iov savediov = iovRepository.save(miov);

        final Iov m1iov = (Iov) rndgen.generate(Iov.class);
        m1iov.id(id);
        m1iov.id().insertionTime(null);
        m1iov.id().since(BigInteger.valueOf(1010L));
        log.info("...created iov via random gen: {}", m1iov);
        final Iov savediov1 = iovRepository.save(m1iov);

        List<BigInteger> groupList = iovGroupsCustom.selectGroups(mtag.name(), 100L);
        assertTrue(groupList != null);
        assertTrue(groupList.size() > 0);
    }

}
