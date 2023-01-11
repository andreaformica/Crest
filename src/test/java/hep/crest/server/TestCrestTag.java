package hep.crest.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.GlobalTagMapDto;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagSetDto;
import hep.crest.server.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@Slf4j
public class TestCrestTag {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper mapper;

    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapperFacade;

    private static final RandomGenerator rnd = new RandomGenerator();

    public void initializeTag(String gtname) {
        TagDto dto = (TagDto) rnd.generate(TagDto.class);
        dto.name(gtname);
        log.info("Store tag : {} ", dto);
        final ResponseEntity<TagDto> response = testRestTemplate
                .postForEntity("/crestapi/tags", dto, TagDto.class);
        log.info("Received response: {}", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    public void initializeGtag(String gtname) {
        GlobalTagDto dto = (GlobalTagDto) rnd.generate(GlobalTagDto.class);
        dto.name(gtname);
        log.info("Store global tag : {} ", dto);
        final ResponseEntity<GlobalTagDto> response = testRestTemplate
                .postForEntity("/crestapi/globaltags", dto, GlobalTagDto.class);
        log.info("Received response: {}", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    public void updateTag(TagDto body) throws JsonProcessingException {
        // Update a tag
        body.setDescription("another description updated");
        body.endOfValidity(BigDecimal.valueOf(1000L));
        body.synchronization("blkp");
        body.payloadSpec("newspec");
        final HttpEntity<TagDto> updrequest = new HttpEntity<TagDto>(body);
        log.info("Update tag : {} ", body.getName());
        final ResponseEntity<String> respupd = this.testRestTemplate
                .exchange("/crestapi/tags/" + body.getName(), HttpMethod.PUT, updrequest, String.class);
        {
            log.info("Update tag {} ", body.getName());
            final String responseBody = respupd.getBody();
            assertThat(respupd.getStatusCode()).isEqualTo(HttpStatus.OK);
            TagDto ok;
            log.info("Response from server is: " + responseBody);
            ok = mapper.readValue(responseBody, TagDto.class);
            assertThat(ok).isNotNull();
            assertThat(ok.getSynchronization()).isEqualTo("blkp");
        }
    }

    public void storeIovs(TagDto dto) throws JsonProcessingException {
        // Upload batch iovs
        final IovId id = new IovId().tagName(dto.getName())
                .since(BigInteger.valueOf(2000000L * 1000000000L)).insertionTime(new Date());
        final Iov miov = (Iov) rnd.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final IovId id2 = new IovId().tagName(dto.getName())
                .since(BigInteger.valueOf(3000000L*1000000000L)).insertionTime(new Date());
        final Iov miov2 = (Iov) rnd.generate(Iov.class);
        miov2.id(id2);
        miov2.id().insertionTime(null);
        log.info("...created iov2 via random gen: {}", miov2);

        final IovSetDto setdto = new IovSetDto();
        setdto.size(2L);
        setdto.format("IovSetDto");
        final GenericMap filters = new GenericMap();
        filters.put("tagName", dto.getName());
        setdto.datatype("iovs").filter(filters);
        IovDto diov = mapperFacade.map(miov, IovDto.class);
        IovDto diov2 = mapperFacade.map(miov2, IovDto.class);
        setdto.addResourcesItem(diov).addResourcesItem(diov2);

        final ResponseEntity<String> iovresp2 = this.testRestTemplate
                .postForEntity("/crestapi/iovs", setdto, String.class);
        log.info("Received response: " + iovresp2);
        assertThat(iovresp2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        {
            final String responseBody = iovresp2.getBody();
            IovSetDto ok;
            log.info("Response from server is: " + responseBody);
            ok = mapper.readValue(responseBody, IovSetDto.class);
            assertThat(ok.getSize()).isPositive();
        }
    }

    @Test
    public void testConflictTag() {
        log.info("=======> testConflictTag ");
        log.info("Create tag A-TEST-11");
        initializeTag("A-TEST-11");
        TagDto dto = (TagDto) rnd.generate(TagDto.class);
        dto.name("A-TEST-11");
        log.info("Store tag with same name: {} ", dto);
        final ResponseEntity<String> response = testRestTemplate
                .postForEntity("/crestapi/tags", dto, String.class);
        log.info("Received response: {}", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        final String url = "/crestapi/admin/tags/" + dto.getName();
        log.info("Removing tag {}", url);
        this.testRestTemplate.delete(url);
    }

    @Test
    public void testGetUpdateAndRemoveTags() {
        try {
            log.info("=======> testGetUpdateAndRemoveTags ");
            log.info("Create tag A-TEST-09");
            initializeTag("A-TEST-09");
            final ResponseEntity<TagSetDto> response = this.testRestTemplate
                    .getForEntity("/crestapi/tags", TagSetDto.class);
            log.info("Found response {}", response.getBody().toString());
            final TagSetDto tagset = response.getBody();
            for (final TagDto tagDto : tagset.getResources()) {
                if (tagDto.getName().equalsIgnoreCase("A-TEST-09")) {
                    log.info("Update tag A-TEST-09");
                    updateTag(tagDto);
                    final String url = "/crestapi/admin/tags/" + tagDto.getName();
                    log.info("Removing tag {}", url);
                    this.testRestTemplate.delete(url);
                }
            }
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getSize()).isNotNegative();
        }
        catch (JsonProcessingException e) {
            log.error("Cannot update tag : {}", e.getMessage());
        }
    }

    @Test
    public void testTagRest() {
        log.info("=======> testTagRest ");
        initializeGtag("A-TEST-GT-10");
        initializeTag("A-TEST-01");
        GlobalTagMapDto mapDto = new GlobalTagMapDto();
        mapDto.tagName("A-TEST-01").globalTagName("A-TEST-GT-10").record("some-rec").label("TEST");
        log.info("Store global tag to tag mapping : {} ", mapDto);
        final ResponseEntity<GlobalTagMapDto> response = testRestTemplate
                .postForEntity("/crestapi/globaltagmaps", mapDto, GlobalTagMapDto.class);
        {
            log.info("Created global tag to tag mapping {} ", response.getBody());
            GlobalTagMapDto respb = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            log.info("Response from server is: " + respb);
            assertThat(respb).isNotNull();
            assertThat(respb.getTagName()).isEqualTo("A-TEST-01");
        }
        final ResponseEntity<TagSetDto> respTrace = testRestTemplate
                .getForEntity("/crestapi/globaltags/A-TEST-GT-10/tags?record=some-rec&label=TEST", TagSetDto.class);
        {
            log.info("Trace global tag mappings {} ", respTrace.getBody());
            TagSetDto respc = respTrace.getBody();
            assertThat(respTrace.getStatusCode()).isEqualTo(HttpStatus.OK);
            log.info("Response from server is: " + respc);
            assertThat(respc).isNotNull();
            assertThat(respc.getSize()).isPositive();
        }
        final ResponseEntity<TagSetDto> respTrace1 = testRestTemplate
                .getForEntity("/crestapi/globaltags/A-TEST-GT-10/tags", TagSetDto.class);
        {
            log.info("Trace global tag mappings with no record and label {} ", respTrace1.getBody());
            TagSetDto respc = respTrace1.getBody();
            assertThat(respTrace1.getStatusCode()).isEqualTo(HttpStatus.OK);
            log.info("Response from server is: " + respc);
            assertThat(respc).isNotNull();
            assertThat(respc.getSize()).isPositive();
        }
        final ResponseEntity<TagSetDto> respTrace2 = testRestTemplate
                .getForEntity("/crestapi/globaltags/A-TEST-GT-10/tags?record=some-rec", TagSetDto.class);
        {
            log.info("Trace global tag mappings with record only {} ", respTrace2.getBody());
            TagSetDto respc = respTrace2.getBody();
            assertThat(respTrace2.getStatusCode()).isEqualTo(HttpStatus.OK);
            log.info("Response from server is: " + respc);
            assertThat(respc).isNotNull();
            assertThat(respc.getSize()).isPositive();
        }
        initializeTag("A-TEST-21");
        TagDto dto = null;
        final ResponseEntity<TagSetDto> resptagset = testRestTemplate
                .getForEntity("/crestapi/tags", TagSetDto.class);
        {
            log.info("Get tags to fill dto using A-TEST-21: {} ", resptagset.getBody());
            TagSetDto respb = resptagset.getBody();
            assertThat(resptagset.getStatusCode()).isEqualTo(HttpStatus.OK);
            log.info("Response from server is: " + respb);
            assertThat(respb).isNotNull();
            List<TagDto> resources = respb.getResources();
            for (TagDto dtot : resources) {
                if (dtot.getName().equalsIgnoreCase("A-TEST-21")) {
                    dto = dtot;
                }
            }
        }
        // Store IOV
        try {
            storeIovs(dto);
            final String url = "/crestapi/admin/tags/" + dto.getName();
            log.info("Removing tag {}", url);
            this.testRestTemplate.delete(url);
        }
        catch (JsonProcessingException e) {
            log.error("Cannot store iovs : {}", e.getMessage());
        }
    }

    @Test
    public void testTagIovRest() {
        log.info("=======> testTagIovRest ");
        initializeTag("A-TEST-02");
        TagDto dto = null;
        final ResponseEntity<TagSetDto> resptagset = testRestTemplate
                .getForEntity("/crestapi/tags", TagSetDto.class);
        {
            log.info("Get tags to fill dto using A-TEST-21: {} ", resptagset.getBody());
            TagSetDto respb = resptagset.getBody();
            assertThat(resptagset.getStatusCode()).isEqualTo(HttpStatus.OK);
            log.info("Response from server is: " + respb);
            assertThat(respb).isNotNull();
            List<TagDto> resources = respb.getResources();
            for (TagDto dtot : resources) {
                if (dtot.getName().equalsIgnoreCase("A-TEST-02")) {
                    dto = dtot;
                }
            }
        }
        // Store IOV
        try {
            storeIovs(dto);
            log.info("Query Iovs....");
            String url = "/crestapi/iovs?tagname=A-TEST-02&since=0&until=9000000000000000";
            final ResponseEntity<IovSetDto> respiovset = testRestTemplate
                    .getForEntity(url, IovSetDto.class);
            {
                log.info("Get iovs for tag A-TEST-02: {} ", respiovset.getBody());
                IovSetDto respb = respiovset.getBody();
                assertThat(respiovset.getStatusCode()).isEqualTo(HttpStatus.OK);
                log.info("Response from server is: " + respb);
                assertThat(respb).isNotNull();
                List<IovDto> resources = respb.getResources();
                for (IovDto dtot : resources) {
                    log.info("found iov: {}", dtot);
                }
            }
            String url1 = "/crestapi/iovs?method=GROUPS&tagname=A-TEST-02&groupsize=10000";
            final ResponseEntity<IovSetDto> respiovset1 = testRestTemplate
                    .getForEntity(url1, IovSetDto.class);
            {
                log.info("Get groups for tag A-TEST-02: {} ", respiovset1.getBody());
                assertThat(respiovset1.getStatusCode()).isEqualTo(HttpStatus.OK);
            }
            final HttpHeaders headers = new HttpHeaders();
            headers.add("X-Crest-Query", "RANGES");
            final HttpEntity<?> entity = new HttpEntity<>(headers);
            String url2 = "/crestapi/iovs?method=IOVS&timeformat=ISO&tagname=A-TEST-02&since=19710301T100000CEST"
                          + "&until=20500501T100000CEST";
            final ResponseEntity<String> respiovset2 = testRestTemplate
                    .exchange(url2, HttpMethod.GET, entity, String.class);
            {
                log.info("Get iovs using ISO for tag A-TEST-02: {} ", respiovset2.getBody());
                assertThat(respiovset1.getStatusCode()).isEqualTo(HttpStatus.OK);
            }

        }
        catch (JsonProcessingException e) {
            log.error("Cannot store iovs : {}", e.getMessage());
        }

    }
}
