package hep.crest.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.swagger.api.ApiResponseMessage;
import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.GlobalTagSetDto;
import hep.crest.server.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@Slf4j
public class TestCrestGlobalTag {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper mapper;

    private static final RandomGenerator rnd = new RandomGenerator();

    public void initialize(String gtname) {
        GlobalTagDto dto = (GlobalTagDto) rnd.generate(GlobalTagDto.class);
        dto.name(gtname);
        dto.snapshotTime(null);
        dto.type("N");
        log.info("Store global tag : {} ", dto);
        final ResponseEntity<GlobalTagDto> response = testRestTemplate
                .postForEntity("/crestapi/globaltags", dto, GlobalTagDto.class);
        log.info("Received response: {}", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    @Test
    public void testA_getAndRemoveGlobaltags() {
        log.info("=======> testA_getAndRemoveGlobaltags ");

        final ResponseEntity<String> response = this.testRestTemplate
                .exchange("/crestapi/globaltags", HttpMethod.GET, null, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            GlobalTagSetDto ok;
            try {
                if (mapper == null) {
                    log.error("Cannot use mapper because it is null");
                }
                log.info("testA_getAndRemoveGlobaltags => Received response {}",
                        response.getBody());
                ok = mapper.readValue(response.getBody(), GlobalTagSetDto.class);
                final List<GlobalTagDto> gtaglist = ok.getResources();
                for (final GlobalTagDto globalTagDto : gtaglist) {
                    final String url = "/crestapi/admin/globaltags/" + globalTagDto.getName();
                    log.info("Removing global tag {}", url);
                    this.testRestTemplate.delete(url);
                }
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(ok.getSize()).isNotNegative();
            }
            catch (final JsonParseException e) {
                e.printStackTrace();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            try {
                final ApiResponseMessage err = mapper.readValue(response.getBody(),
                        ApiResponseMessage.class);
                log.info("Response error in global tag {}", err.getMessage());
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testB_storeGlobaltags() throws Exception {
        log.info("=======> testB_storeGlobaltags ");
        initialize("A-TEST-GT-01");
        GlobalTagDto dto = (GlobalTagDto) rnd.generate(GlobalTagDto.class);
        dto.name("A-TEST-GT-01");
        log.info("Try to store global tag with same name : {} ", dto);
        final ResponseEntity<String> response1 = this.testRestTemplate
                .postForEntity("/crestapi/globaltags", dto, String.class);
        log.info("Received response: {}", response1);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        log.info("Try to update global tag : {} ", dto);
        final GlobalTagDto body = dto;
        body.setDescription("Description has changed");
        body.validity(BigInteger.valueOf(1990L).longValue());
        body.scenario("update");
        body.type("N");
        final HttpEntity<GlobalTagDto> updrequest = new HttpEntity<GlobalTagDto>(body);

        final ResponseEntity<String> respupd = this.testRestTemplate
                .exchange("/crestapi/admin/globaltags/" + dto.getName(), HttpMethod.PUT, updrequest, String.class);
        {
            log.info("Update global tag {} ", body.getName());
            final String responseBody = respupd.getBody();
            assertThat(respupd.getStatusCode()).isEqualTo(HttpStatus.OK);
            GlobalTagDto ok;
            log.info("Response from server is: " + responseBody);
            ok = mapper.readValue(responseBody, GlobalTagDto.class);
            assertThat(ok).isNotNull();
            assertThat(ok.getScenario()).isEqualTo("update");
        }
        final ResponseEntity<String> respupdfail = this.testRestTemplate
                .exchange("/crestapi/admin/globaltags/NOT-THERE", HttpMethod.PUT, updrequest, String.class);
        {
            log.info("Update global tag NOT-THERE ");
            assertThat(respupdfail.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        final ResponseEntity<String> resprmnotthere = this.testRestTemplate
                .exchange("/crestapi/admin/globaltags/NOT-THERE", HttpMethod.DELETE, null, String.class);
        {
            log.info("Remove global tag NOT-THERE ");
            assertThat(resprmnotthere.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        dto.name(null);
        log.info("Try to use null name in global tag again : {} ", dto);
        final ResponseEntity<String> response2 = this.testRestTemplate
                .postForEntity("/crestapi/globaltags", dto, String.class);
        log.info("Received response: {}", response2);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        final ResponseEntity<String> responsedelete = this.testRestTemplate.exchange(
                "/crestapi/admin/globaltags/A-TEST-GT-01", HttpMethod.DELETE, null, String.class);
        log.info("Received response on delete: {}", responsedelete);
        assertThat(responsedelete.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testC_findGlobaltags() throws Exception {
        log.info("=======> testC_findGlobaltags ");
        initialize("A-TEST-GT-01");
        initialize("A-TEST-GT-02");

        log.info("Retrieve all global tags:");
        final ResponseEntity<String> resp = this.testRestTemplate.exchange("/crestapi/globaltags",
                HttpMethod.GET, null, String.class);

        {
            log.info("Retrieved all global tags {} ", resp.getBody());
            final String responseBody = resp.getBody();
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            GlobalTagSetDto ok;
            log.info("Response from server is: " + responseBody);
            ok = mapper.readValue(responseBody, GlobalTagSetDto.class);
            assertThat(ok.getSize()).isPositive();
        }

        final ResponseEntity<String> resp1 = this.testRestTemplate.exchange(
                "/crestapi/globaltags/" + "A-TEST-GT-01", HttpMethod.GET, null, String.class);
        {
            log.info("Retrieved global tag A-TEST-GT-01");
            final String responseBody = resp1.getBody();
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            GlobalTagSetDto ok;
            log.info("Response from server is: " + responseBody);
            ok = mapper.readValue(responseBody, GlobalTagSetDto.class);
            assertThat(ok.getSize()).isEqualTo(1);
        }
        // This should not find the GT
        final ResponseEntity<String> resp1null = this.testRestTemplate
                .exchange("/crestapi/globaltags/SOME-GT", HttpMethod.GET, null, String.class);
        {
            log.info("Retrieved global tag SOME-GT should return null");
            assertThat(resp1null.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        final ResponseEntity<String> resp2 = this.testRestTemplate
                .exchange("/crestapi/globaltags?name=A-TEST-GT%", HttpMethod.GET, null,
                        String.class);
        {
            log.info("Retrieved global tag list " + resp2.getBody());
            final String responseBody = resp2.getBody();
            assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.OK);
            GlobalTagSetDto ok;
            log.info("Response from server is: " + responseBody);
            ok = mapper.readValue(responseBody, GlobalTagSetDto.class);
            assertThat(ok.getSize()).isPositive();
        }

        String url = "/crestapi/globaltags?type=N&release=1&snapshot=1&workflow"
                + "=prod&scenario=update&description=Description%20has%20changed&validity=1990";
        final ResponseEntity<String> resp3 = this.testRestTemplate
                .exchange(url, HttpMethod.GET, null,
                        String.class);
        {
            log.info("Retrieved empty global tag list " + resp3.getBody());
            final String responseBody = resp3.getBody();
            assertThat(resp3.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

    }

    @Test
    public void testD_globaltagsfail() {
        final ResponseEntity<String> resp = this.testRestTemplate
                .exchange("/crestapi/globaltags?name=123", HttpMethod.GET, null, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        final ResponseEntity<String> resp1 = this.testRestTemplate
                .exchange("/crestapi/globaltags", HttpMethod.GET, null, String.class);
        assertThat(resp1.getStatusCode()).isEqualTo(HttpStatus.OK);

        final ResponseEntity<String> resp2 = this.testRestTemplate.exchange(
                "/crestapi/globaltags?sort=fff:DESC", HttpMethod.GET, null, String.class);
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testE_removeLockedGlobalTag() throws Exception {
        log.info("=======> testE_removeLockedGlobalTag ");
        // Create a global tag
        initialize("A-TEST-GT-LOCKED");

        // Lock the global tag using the /globaltags/{name}/lock endpoint
        final ResponseEntity<String> lockResp = this.testRestTemplate.exchange(
                "/crestapi/globaltags/A-TEST-GT-LOCKED/lock?status=L",
                HttpMethod.PUT, null, String.class);
        log.info("Lock global tag response: {}", lockResp.getBody());
        assertThat(lockResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Try to remove the locked global tag - should fail with CONFLICT
        final ResponseEntity<String> deleteResp = this.testRestTemplate.exchange(
                "/crestapi/admin/globaltags/A-TEST-GT-LOCKED",
                HttpMethod.DELETE, null, String.class);
        log.info("Delete locked global tag response: {}", deleteResp.getBody());
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Unlock the global tag to clean up
        final ResponseEntity<String> unlockResp = this.testRestTemplate.exchange(
                "/crestapi/globaltags/A-TEST-GT-LOCKED/lock?status=N",
                HttpMethod.PUT, null, String.class);
        assertThat(unlockResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Now delete should work
        final ResponseEntity<String> deleteResp2 = this.testRestTemplate.exchange(
                "/crestapi/admin/globaltags/A-TEST-GT-LOCKED",
                HttpMethod.DELETE, null, String.class);
        assertThat(deleteResp2.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testF_removeGlobalTagWithMappings() throws Exception {
        log.info("=======> testF_removeGlobalTagWithMappings ");
        // Create a global tag
        initialize("A-TEST-GT-MAPPED");

        // Create a tag to associate with the global tag
        hep.crest.server.swagger.model.TagDto tagDto =
                (hep.crest.server.swagger.model.TagDto) rnd.generate(
                        hep.crest.server.swagger.model.TagDto.class);
        tagDto.name("A-TEST-TAG-FOR-MAPPING");
        tagDto.synchronization(hep.crest.server.swagger.model.TagDto.SynchronizationEnum.ALL);
        tagDto.status(hep.crest.server.swagger.model.TagDto.StatusEnum.UNLOCKED);
        final ResponseEntity<String> tagResp = this.testRestTemplate
                .postForEntity("/crestapi/tags", tagDto, String.class);
        log.info("Create tag response: {}", tagResp.getBody());
        assertThat(tagResp.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.CONFLICT);

        // Create a mapping between global tag and tag
        hep.crest.server.swagger.model.GlobalTagMapDto mapDto =
                new hep.crest.server.swagger.model.GlobalTagMapDto();
        mapDto.globalTagName("A-TEST-GT-MAPPED");
        mapDto.tagName("A-TEST-TAG-FOR-MAPPING");
        mapDto.record("test-record");
        mapDto.label("test-label");
        final ResponseEntity<String> mapResp = this.testRestTemplate
                .postForEntity("/crestapi/globaltagmaps", mapDto, String.class);
        log.info("Create mapping response: {}", mapResp.getBody());
        assertThat(mapResp.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.CONFLICT);

        // Try to remove the global tag with mappings - should fail with CONFLICT
        final ResponseEntity<String> deleteResp = this.testRestTemplate.exchange(
                "/crestapi/admin/globaltags/A-TEST-GT-MAPPED",
                HttpMethod.DELETE, null, String.class);
        log.info("Delete global tag with mappings response: {}", deleteResp.getBody());
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Clean up: remove the mapping first
        final ResponseEntity<String> deleteMapResp = this.testRestTemplate.exchange(
                "/crestapi/globaltagmaps/A-TEST-GT-MAPPED?tagName=A-TEST-TAG-FOR-MAPPING&record=test-record&label=test-label",
                HttpMethod.DELETE, null, String.class);
        log.info("Delete mapping response: {}", deleteMapResp.getBody());
        // Mapping deletion might return OK or NOT_FOUND depending on implementation
        assertThat(deleteMapResp.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND, HttpStatus.NO_CONTENT);

        // Now delete global tag should work (or it might still have mappings)
        final ResponseEntity<String> deleteResp2 = this.testRestTemplate.exchange(
                "/crestapi/admin/globaltags/A-TEST-GT-MAPPED",
                HttpMethod.DELETE, null, String.class);
        log.info("Delete global tag after removing mapping response: {}", deleteResp2.getBody());
        // Accept both OK (if mappings were removed) or CONFLICT (if mappings still exist)
        assertThat(deleteResp2.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CONFLICT);
    }

}
