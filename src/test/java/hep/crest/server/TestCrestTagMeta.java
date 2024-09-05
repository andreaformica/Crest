package hep.crest.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.services.PayloadService;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagMetaDto;
import hep.crest.server.swagger.model.TagMetaSetDto;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@ContextConfiguration
@Slf4j
public class TestCrestTagMeta {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PayloadService payloadService;
    private String hash = "hashresource2";

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
        metadto.colsize(2);
        log.info("Store tag meta info : {} ", metadto);
        final ResponseEntity<TagMetaDto> responsem = testRestTemplate
                .postForEntity("/crestapi/tags/"+gtname+"/meta", metadto, TagMetaDto.class);
        log.info("Received response: {}", responsem);
        assertThat(responsem.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    public String postPayloadFromFile(String tagname) throws JsonProcessingException {
        // Upload batch payload using file resource
        final GenericMap filters = new GenericMap();
        filters.put("tagName", tagname);

        final StoreSetDto setdto1 = new StoreSetDto();
        setdto1.size(1L);
        setdto1.format("StoreSetDto");
        filters.put("tagName", tagname);
        setdto1.datatype("json").filter(filters);

        StoreDto sdto = new StoreDto();
        sdto.streamerInfo("{\"filename\": \"test-file-2\"}");
        sdto.since(0L);
        sdto.hash("hashresource2");
        sdto.setData("theresource2");

        setdto1.addResourcesItem(sdto);
        String fileName = "theresource2";
        byte[] fileContent = "this is file content for theresource2 and should be new".getBytes();
        HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.TEXT_PLAIN);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        final HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.MULTIPART_FORM_DATA);

        final HttpEntity<ByteArrayResource> partsEntity = new HttpEntity<>(byteArrayResource, parts);
        final MultiValueMap<String, Object> map1 = new LinkedMultiValueMap<String, Object>();
        map1.add("files", partsEntity);
        map1.add("tag", tagname);
        map1.add("storeset", mapper.writeValueAsString(setdto1));
        final HttpEntity<MultiValueMap<String, Object>> request1 = new HttpEntity<MultiValueMap<String, Object>>(
                map1, headers1);
        final ResponseEntity<String> resp = this.testRestTemplate
                .postForEntity("/crestapi/payloads", request1, String.class);
        assertEquals(resp.getStatusCode().value(), HttpStatus.CREATED.value());
        log.info("Received response: " + resp);
        {
            StoreSetDto respb = mapper.readValue(resp.getBody(), StoreSetDto.class);
            StoreDto dto = respb.getResources().get(0);
            log.info("Found stored payload in response : {}", dto);
            return dto.getHash();
        }
    }

    public void storeIovs(TagDto dto, Boolean settag) throws JsonProcessingException {
        // Init payload data if not already done
        try {
            Payload pyld = payloadService.getPayload(hash);
        } catch (CdbNotFoundException e) {
            log.info("Payload not stored");
            String hashstored = postPayloadFromFile(dto.getName());
            if (!hash.equals(hashstored)) {
                log.warn("Hash mismatch...set hash to {}", hashstored);
                hash = hashstored;
            }
        }
        // Upload batch iovs
        final IovId id = new IovId().tagName(dto.getName())
                .since(BigInteger.valueOf(2000000L)).insertionTime(new Date());
        final Iov miov = (Iov) rnd.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        miov.payloadHash(hash);
        log.info("...created iov via random gen: {}", miov);
        final IovId id2 = new IovId().tagName(dto.getName())
                .since(BigInteger.valueOf(3000000L)).insertionTime(new Date());
        final Iov miov2 = (Iov) rnd.generate(Iov.class);
        miov2.id(id2);
        miov2.payloadHash(hash);
        miov2.id().insertionTime(null);
        log.info("...created iov2 via random gen: {}", miov2);

        final IovSetDto setdto = new IovSetDto();
        setdto.size(2L);
        setdto.format("IovSetDto");
        final GenericMap filters = new GenericMap();
        if (settag) {
            filters.put("tagName", dto.getName());
        }
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

    public void updateTagMeta(String tagname, Map<String, String> request) throws JsonProcessingException {
        // Update a tag
        final HttpEntity<Map<String, String>> updrequest = new HttpEntity<>(request);
        log.info("Update tag meta : {} ", tagname);
        final ResponseEntity<String> respupd = this.testRestTemplate
                .exchange("/crestapi/tags/" + tagname + "/meta", HttpMethod.PUT, updrequest,
                        String.class);
        {
            log.info("Update tag meta {} ", tagname);
            final String responseBody = respupd.getBody();
            assertThat(respupd.getStatusCode()).isEqualTo(HttpStatus.OK);
            TagMetaDto ok;
            log.info("Response from server is: " + responseBody);
            ok = mapper.readValue(responseBody, TagMetaDto.class);
            assertThat(ok).isNotNull();
            //assertThat(ok.getColsize()).isEqualTo(2);
        }
    }

    @Test
    public void testTagStoreDeleteRest() {
        log.info("=======> testTagStoreDeleteRest ");
        initializeTag("A-TEST-31");
        TagDto dto = null;
        // Get a tag
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
            storeIovs(dto, true);
            final String url = "/crestapi/admin/tags/" + dto.getName();
            log.info("Removing tag and meta {}", url);
            this.testRestTemplate.delete(url);
        }
        catch (JsonProcessingException e) {
            log.error("Cannot store iovs : {}", e.getMessage());
        }
    }

    @Test
    public void testRetrieveUpdateTag() throws JsonProcessingException {
        log.info("=======> testRetrieveTag ");
        initializeTag("A-TEST-32");
        // Get tag
        // Find tag name
        final ResponseEntity<String> resp1 = testRestTemplate
                .exchange("/crestapi/tags/A-TEST-32" , HttpMethod.GET,
                        null, String.class);
        {
            log.info("Find tag {} ", resp1.getBody());
            assertThat(resp1.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        // Find tag meta name
        TagMetaSetDto ok;
        final ResponseEntity<String> resp2 = testRestTemplate
                .exchange("/crestapi/tags/A-TEST-32/meta" , HttpMethod.GET,
                        null, String.class);
        {
            log.info("Find tag meta {} ", resp2.getBody());
            assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.OK);
            ok = mapper.readValue(resp2.getBody(), TagMetaSetDto.class);
            assertThat(ok).isNotNull();
        }
        TagMetaDto meta = ok.getResources().get(0);
        Map<String, String> metaMap = new HashMap<>();
        metaMap.put("tagInfo", "{\"channels\": [{0: \"ch1\"}, {1: \"ch2\"}]}");
        updateTagMeta(meta.getTagName(), metaMap);

        // Store IOV
        try {
            TagDto dto = new TagDto();
            dto.setName("A-TEST-32");
            storeIovs(dto, false);
            final String url = "/crestapi/admin/tags/" + dto.getName();
            log.info("Removing tag and meta {}", url);
            this.testRestTemplate.delete(url);
        }
        catch (JsonProcessingException e) {
            log.error("Cannot store iovs : {}", e.getMessage());
        }

    }
}
