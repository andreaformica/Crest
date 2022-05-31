package hep.crest.server;


import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.handlers.PayloadHandler;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.TagRepository;
import hep.crest.server.repositories.PayloadDataBaseCustom;
import hep.crest.server.repositories.PayloadDataSQLITEImpl;
import hep.crest.server.repositories.TagMetaDataBaseCustom;
import hep.crest.server.repositories.TagMetaSQLITEImpl;
import hep.crest.server.swagger.model.PayloadDto;
import hep.crest.server.swagger.model.TagMetaDto;
import hep.crest.server.utils.DataGenerator;
import hep.crest.server.utils.RandomGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-sqlite.yml")
@ActiveProfiles("sqlite")
public class RepositorySqliteTests {

    private static final Logger log = LoggerFactory.getLogger(RepositorySqliteTests.class);

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

    private RandomGenerator rndgen = new RandomGenerator();

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
    public void testPayload() throws Exception {
        final Instant now = Instant.now();
        PayloadDto dto = (PayloadDto) rndgen.generate(PayloadDto.class);
        dto.insertionTime(now.atOffset(ZoneOffset.UTC));
        log.info("Save payload {}", dto);
        if (dto.getData() == null) {
            dto.data("testforpayload".getBytes(StandardCharsets.UTF_8));
        }
        if (dto.getStreamerInfo() == null) {
            dto.streamerInfo("{key: val}".getBytes(StandardCharsets.UTF_8));
        }
        if (dto.getSize() == null) {
            dto.setSize(dto.getData().length);
        }
        final PayloadDto saved = repobean.save(dto);
        assertThat(saved).isNotNull();
        assertThat(saved.getHash()).isEqualTo(dto.getHash());
        // modify streamer info
        int nu = repobean.updateMetaInfo(saved.getHash(), new String(saved.getStreamerInfo()) + ", {some other json}");
        assertThat(nu).isPositive();

        // Search for payload
        final PayloadDto loaded = repobean.find(dto.getHash());
        assertThat(loaded.toString().length()).isPositive();

        // Store payload from file
        DataGenerator.generatePayloadData("/tmp/cdms/payloadatassl.blob","blob for sqlite");
        final File f = new File("/tmp/cdms/payloadatassl.blob");
        InputStream ds = new BufferedInputStream(new FileInputStream(f));

        PayloadDto dtofromfile = (PayloadDto) rndgen.generate(PayloadDto.class);
        dtofromfile.insertionTime(now.atOffset(ZoneOffset.UTC));
        if (dtofromfile.getStreamerInfo() == null) {
            dtofromfile.streamerInfo("{key: val}".getBytes(StandardCharsets.UTF_8));
        }
        final PayloadDto savedfromblob = repobean.save(dtofromfile,ds);
        assertThat(savedfromblob.toString().length()).isPositive();
        if (ds != null) {
            ds.close();
        }
        // Load data
        final InputStream loadedblob = repobean.findData(savedfromblob.getHash());
        assertThat(loadedblob.available()).isPositive();
        // Load only meta info
        final PayloadDto loadedmeta = repobean.findMetaInfo(savedfromblob.getHash());
        assertThat(new String(loadedmeta.getStreamerInfo())).isEqualTo("{key: val}");
        repobean.delete(savedfromblob.getHash());

        ds = new BufferedInputStream(new FileInputStream(f));
        PayloadHandler.saveStreamToFile(ds, "/tmp/cdms/payloadatacopysl.blob");
        final File f1 = new File("/tmp/cdms/payloadatacopysl.blob");
        final InputStream ds1 = new BufferedInputStream(new FileInputStream(f1));
        final byte[] barr = PayloadHandler.getBytesFromInputStream(ds1);
        assertThat(barr).isNotEmpty();
        if (ds1 != null) {
            ds1.close();
        }
        try {
            final PayloadDto loadedblob1 = repobean.find(savedfromblob.getHash());
            assertThat(loadedblob1).isNull();
        }
        catch (AbstractCdbServiceException e) {
            log.error("Cannot find payload for hash {}: {}", savedfromblob.getHash(), e);
        }
    }

    @Test
    public void testTags() throws Exception {
        final Instant now = Instant.now();
        Tag mtag = (Tag) rndgen.generate(Tag.class);
        final Tag savedtag = tagrepository.save(mtag);
        assertThat(savedtag.name()).isEqualTo(mtag.name());

        TagMetaDto metadto = (TagMetaDto) rndgen.generate(TagMetaDto.class);
        metadto.tagName(mtag.name());
        final TagMetaDto savedmeta = tagmetarepobean.save(metadto);
        assertThat(savedmeta).isNotNull();
        assertThat(savedmeta.toString().length()).isPositive();
        assertThat(savedmeta.getTagName()).isEqualTo(savedtag.name());

        final TagMetaDto storedmeta = tagmetarepobean.find(savedmeta.getTagName());
        assertThat(storedmeta).isNotNull();
        storedmeta.tagInfo("{ \"key1\" : \"val1\" }");
        final TagMetaDto updmeta = tagmetarepobean.update(storedmeta);
        assertThat(updmeta).isNotNull();
        tagmetarepobean.delete(updmeta.getTagName());
        assertThat(updmeta).isNotNull();
        try {
            final TagMetaDto deletedmeta = tagmetarepobean.find(updmeta.getTagName());
            assertThat(deletedmeta).isNull();
        }
        catch (AbstractCdbServiceException e) {
            log.error("Cannot find deleted meta info: it was deleted before {}", e.getMessage());
        }
    }
}
