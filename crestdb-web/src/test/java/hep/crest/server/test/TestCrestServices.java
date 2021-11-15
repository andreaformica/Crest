package hep.crest.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.services.GlobalTagMapService;
import hep.crest.server.services.GlobalTagService;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import hep.crest.server.services.TagService;
import hep.crest.swagger.model.IovDto;
import hep.crest.swagger.model.PayloadDto;
import hep.crest.swagger.model.TagDto;
import hep.crest.testutils.DataGenerator;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCrestServices {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GlobalTagService globaltagService;
    @Autowired
    private GlobalTagMapService globaltagmapService;
    @Autowired
    private TagService tagService;
    @Autowired
    private IovService iovService;
    @Autowired
    private PayloadService payloadService;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper mapper;
    @Autowired
    private MapperFacade mapperFacade;
    /**
     * Helper.
     */
    @Autowired
    private EntityDtoHelper edh;


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
    public void testA_Globaltags() {
        final GlobalTag dto = DataGenerator.generateGlobalTag("TEST-GT");
        try {
            final GlobalTag saved = globaltagService.insertGlobalTag(dto);
            saved.description("this is an updated description");
            final GlobalTag updated = globaltagService.updateGlobalTag(saved);
            assertThat(updated.description()).isNotEqualTo(dto.description());
            updated.name("ANOTHER-NOTEXISTING-GT");
            updated.description("this should not be updated");
            globaltagService.updateGlobalTag(updated);
        }
        catch (final AbstractCdbServiceException e) {
            log.info("Cannot save or update global tag {}: {}", dto, e);
        }
    }

    @Test
    public void testB_Tags() {
        final TagDto dto = DataGenerator.generateTagDto("MY-TEST-01", "time");
        try {
            final Tag entity = mapperFacade.map(dto, Tag.class);
            final Tag saved = tagService.insertTag(entity);
            saved.description("this is an updated tag description");
            final Tag updated = tagService.updateTag(saved);
            assertThat(updated.description()).isNotEqualTo(dto.getDescription());
            updated.name("ANOTHER-UNEXISTING-TAG");
            updated.description("this should not be updated");
            tagService.updateTag(updated);
        }
        catch (final AbstractCdbServiceException e) {
            log.info("Cannot save or update global tag {}: {}", dto, e);
        }
    }

    @Test
    public void testC_Iovs() {
        final PayloadDto dto = DataGenerator.generatePayloadDto("afakehashp10", "",
                "some other info", "test");
        try {
            payloadService.insertPayload(dto);
        }
        catch (final AbstractCdbServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final IovDto iovdto = DataGenerator.generateIovDto("afakehashp10", "MY-TEST-01", new BigDecimal(1000L));
        try {
            final Iov ioventity = mapperFacade.map(iovdto, Iov.class);
            ioventity.tag(new Tag().name(iovdto.getTagName()));
            iovService.insertIov(ioventity);
        }
        catch (final AbstractCdbServiceException e) {
            log.error("Exception in iov insertion {}", e);
        }
        try {
            final Iterable<Iov> iovlist = iovService.findAllIovs(null, PageRequest.of(0, 10));
            final List<IovDto> dtolist = edh.entityToDtoList(iovlist, IovDto.class);
            assertThat(dtolist.size()).isPositive();
        }
        catch (final RuntimeException e) {
            log.error("Exception in iov retrieval {}", e);
        }
    }

    @Test
    public void testC_Mappings() {

        // Create a mapping with everything
        try {
            final GlobalTag gt = DataGenerator.generateGlobalTag("TEST-GT-FORMAP-01");
            final GlobalTag gts = globaltagService.insertGlobalTag(gt);
            // We create a tag BUT without saveing it
            final Tag tag = DataGenerator.generateTag("MY-TEST-FORMAP-01", "time");
            final Tag ts = tagService.insertTag(tag);
            final GlobalTagMapId mapid = new GlobalTagMapId();
            mapid.globalTagName(gt.name()).label("somelabel").record("somerecord");
            final GlobalTagMap entity = DataGenerator.generateMapping(gt, tag, mapid);
            final GlobalTagMap saved = globaltagmapService.insertGlobalTagMap(entity);
            assertThat(saved).isNotNull();
        }
        catch (RuntimeException e) {
            log.info("Exception : {}", e);
        }

        // Create a mapping wih a not existing tag
        try {
            final GlobalTag gt = DataGenerator.generateGlobalTag("TEST-GT-FORMAP-02");
            final GlobalTag gts = globaltagService.insertGlobalTag(gt);
            // We create a tag BUT without saveing it
            final Tag tag = DataGenerator.generateTag("MY-TEST-FORMAP-02", "time");
            final GlobalTagMapId mapid = new GlobalTagMapId();
            mapid.globalTagName(gt.name()).label("somelabel").record("somerecord");
            final GlobalTagMap entity = DataGenerator.generateMapping(gt, tag, mapid);
            final GlobalTagMap saved = globaltagmapService.insertGlobalTagMap(entity);
        }
        catch (RuntimeException e) {
            log.info("Exception : {}", e);
        }
        // Create a mapping wih an existing MAPID
        try {
            // These two already exists as a mapping
            final GlobalTag gt = DataGenerator.generateGlobalTag("TEST-GT-FORMAP-02");
            final Tag tag = DataGenerator.generateTag("MY-TEST-FORMAP-02", "time");
            final GlobalTagMapId mapid = new GlobalTagMapId();
            mapid.globalTagName(gt.name()).label("somelabel").record("somerecord");
            final GlobalTagMap entity = DataGenerator.generateMapping(gt, tag, mapid);
            final GlobalTagMap saved = globaltagmapService.insertGlobalTagMap(entity);
        }
        catch (RuntimeException e) {
            log.info("Exception : {}", e);
        }

        // Now try get mappings with null arguments
        globaltagmapService.getTagMap(null);
        globaltagmapService.getTagMapByTagName(null);

    }
}
