package hep.crest.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagMetaDto;
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
@ContextConfiguration
@Slf4j
public class TestCrestTagMeta {

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

        TagMetaDto metadto = (TagMetaDto) rnd.generate(TagMetaDto.class);
        metadto.insertionTime(null);
        metadto.tagInfo("{\"channels\": [{0: \"ch1\"}]}");
        metadto.tagName(gtname);
        log.info("Store tag meta info : {} ", metadto);
        final ResponseEntity<TagMetaDto> responsem = testRestTemplate
                .postForEntity("/crestapi/tags/"+gtname+"/meta", metadto, TagMetaDto.class);
        log.info("Received response: {}", responsem);
        assertThat(responsem.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    public void storeIovs(TagDto dto) throws JsonProcessingException {
        // Upload batch iovs
        final IovId id = new IovId().tagName(dto.getName())
                .since(BigInteger.valueOf(2000000L)).insertionTime(new Date());
        final Iov miov = (Iov) rnd.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final IovId id2 = new IovId().tagName(dto.getName())
                .since(BigInteger.valueOf(3000000L)).insertionTime(new Date());
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
    public void testTagMetaRest() {
        log.info("=======> testTagMetaRest ");
        initializeTag("A-TEST-31");
        TagDto dto = null;
        final ResponseEntity<TagSetDto> resptagset = testRestTemplate
                .getForEntity("/crestapi/tags", TagSetDto.class);
        {
            log.info("Get tag mapping {} ", resptagset.getBody());
            TagSetDto respb = resptagset.getBody();
            assertThat(resptagset.getStatusCode()).isEqualTo(HttpStatus.OK);
            log.info("Response from server is: " + respb);
            assertThat(respb).isNotNull();
            List<TagDto> resources = respb.getResources();
            for (TagDto dtot : resources) {
                if (dtot.getName().equalsIgnoreCase("A-TEST-31")) {
                    dto = dtot;
                }
            }
        }
        // Store IOV
        try {
            storeIovs(dto);
            final String url = "/crestapi/admin/tags/" + dto.getName();
            log.info("Removing tag and meta {}", url);
            this.testRestTemplate.delete(url);
        }
        catch (JsonProcessingException e) {
            log.error("Cannot store iovs : {}", e.getMessage());
        }
    }
}
