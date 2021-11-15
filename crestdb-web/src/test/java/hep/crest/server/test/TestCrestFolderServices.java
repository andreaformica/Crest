package hep.crest.server.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.data.security.pojo.CrestFolders;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.services.FolderService;
import hep.crest.server.services.GlobalTagMapService;
import hep.crest.server.services.GlobalTagService;
import hep.crest.server.services.IovService;
import hep.crest.server.services.PayloadService;
import hep.crest.server.services.TagService;
import hep.crest.swagger.model.FolderDto;
import hep.crest.swagger.model.FolderSetDto;
import hep.crest.swagger.model.GlobalTagDto;
import hep.crest.swagger.model.GlobalTagSetDto;
import hep.crest.swagger.model.IovDto;
import hep.crest.swagger.model.PayloadDto;
import hep.crest.swagger.model.TagDto;
import hep.crest.swagger.model.TagSetDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class TestCrestFolderServices {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FolderService folderService;
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
    public void testA_createFolder() {
        final FolderDto dto = DataGenerator.generateFolderDto("SPECIAL_FOLDER", "/MY/SPECIAL_FOLDER", "MY_SCHEMA");
        try {
            dto.setTagPattern("MYSPECIAL");
            final CrestFolders entity = mapperFacade.map(dto, CrestFolders.class);
            final CrestFolders saved = folderService.insertFolder(entity);
            log.info("Saved new folder {}", saved);
            assertThat(saved.tagPattern()).isEqualTo(dto.getTagPattern());

            final FolderDto fromentity = mapperFacade.map(saved, FolderDto.class);
            log.info("Convert to dto {}", fromentity);
            assertThat(fromentity.getNodeName()).isEqualTo(dto.getNodeName());
        }
        catch (final AbstractCdbServiceException e) {
            log.info("Cannot save folder {}: {}", dto, e);
        }
    }

    @Test
    public void testB_listFolder() {
        final FolderDto dto = DataGenerator.generateFolderDto("NORMAL_FOLDER", "/MY/NORMAL_FOLDER", "MY_SCHEMA");
        try {
            dto.setTagPattern("MYNORMAL");
            log.info("Store global tag : {} ", dto);
            final ResponseEntity<FolderDto> response = this.testRestTemplate
                    .postForEntity("/crestapi/folders", dto, FolderDto.class);
            log.info("Received response: {}", response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            final ResponseEntity<String> responseconflict = this.testRestTemplate
                    .postForEntity("/crestapi/folders", dto, String.class);
            log.info("Received response: {}", responseconflict);
            assertThat(responseconflict.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

            final ResponseEntity<String> resp = this.testRestTemplate.exchange(
                    "/crestapi/folders", HttpMethod.GET, null, String.class);
            {
                log.info("Retrieved folders ... ");
                final String responseBody = resp.getBody();
                assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
                FolderSetDto ok;
                log.info("Response from server is: " + responseBody);
                ok = mapper.readValue(responseBody, FolderSetDto.class);
                assertThat(ok.getSize()).isGreaterThan(0);
            }

            final ResponseEntity<String> resp1 = this.testRestTemplate.exchange(
                    "/crestapi/folders?sort=tagPattern:DESC", HttpMethod.GET, null, String.class);
            {
                log.info("Retrieved folders sorting by tag pattern... ");
                final String responseBody = resp1.getBody();
                assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
                FolderSetDto ok;
                log.info("Response from server is: " + responseBody);
                ok = mapper.readValue(responseBody, FolderSetDto.class);
                assertThat(ok.getSize()).isGreaterThan(0);
            }

            final ResponseEntity<String> resp2 = this.testRestTemplate.exchange(
                    "/crestapi/folders?by=nodefullpath:NORMAL_FOLDER", HttpMethod.GET, null, String.class);
            {
                log.info("Retrieved folders matching FOLDER pattern ");
                final String responseBody = resp2.getBody();
                assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.OK);
                FolderSetDto ok;
                log.info("Response from server is: " + responseBody);
                ok = mapper.readValue(responseBody, FolderSetDto.class);
                assertThat(ok.getSize()).isGreaterThan(0);
            }
        }
        catch (final AbstractCdbServiceException e) {
            log.info("Cannot save or update global tag {}: {}", dto, e);
        }
        catch (JsonMappingException e) {
            log.info("Error in Json parsing: {}", e);
        }
        catch (JsonProcessingException e) {
            log.info("Error in Json processing: {}", e);
        }
    }

}
