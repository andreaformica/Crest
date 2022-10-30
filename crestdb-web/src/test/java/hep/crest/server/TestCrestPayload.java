package hep.crest.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import hep.crest.server.swagger.model.TagDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@Slf4j
public class TestCrestPayload {

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
        assertEquals(response.getStatusCode().value(), HttpStatus.CREATED.value());
    }

    public void storeIovsAndPayload(String tagname) throws JsonProcessingException {
        // Upload batch iovs
        final IovId id = new IovId().tagName(tagname)
                .since(BigInteger.valueOf(2000000L * 1000000000L)).insertionTime(new Date());
        final Iov miov = (Iov) rnd.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final IovId id2 = new IovId().tagName(tagname)
                .since(BigInteger.valueOf(3000000L*1000000000L)).insertionTime(new Date());
        final Iov miov2 = (Iov) rnd.generate(Iov.class);
        miov2.id(id2);
        miov2.id().insertionTime(null);
        log.info("...created iov2 via random gen: {}", miov2);

        final StoreSetDto setdto = new StoreSetDto();
        setdto.size(2L);
        setdto.format("StoreSetDto");
        final GenericMap filters = new GenericMap();
        filters.put("tagName", tagname);
        setdto.datatype("payloads").filter(filters);

        StoreDto sdto = new StoreDto();
        sdto.streamerInfo("{\"filename\": \"test-inline-1\"}");
        sdto.since(new BigDecimal(miov.id().since()));
        sdto.hash("somehash1");
        sdto.setData("{ \"key\": \"an inline payload as a json\"}");

        StoreDto sdto1 = new StoreDto();
        sdto1.streamerInfo("{\"filename\": \"test-inline-2\"}");
        sdto1.since(new BigDecimal(miov2.id().since()));
        sdto1.hash("somehash2");
        sdto1.setData("{ \"key\": \"an inline payload as a json 2 should have different hash\"}");
        setdto.addResourcesItem(sdto).addResourcesItem(sdto1);

        // Upload batch payload using inline blob
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("X-Crest-PayloadFormat", "JSON");
        final MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("files", null);
        map.add("tag", tagname);
        map.add("storeset", mapper.writeValueAsString(setdto));
        map.add("endtime", "0");
        map.add("objectType", "JSON");
        map.add("compressionType", "none");
        map.add("version", "1.0");
        final HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
                map, headers);
        final ResponseEntity<String> resp = this.testRestTemplate
                .postForEntity("/crestapi/payloads", request, String.class);
        assertEquals(resp.getStatusCode().value(), HttpStatus.CREATED.value());
        log.info("Received response: " + resp);

        // Upload batch payload using file resource
        final StoreSetDto setdto1 = new StoreSetDto();
        setdto1.size(2L);
        setdto1.format("StoreSetDto");
        filters.put("tagName", tagname);
        setdto.datatype("payloads").filter(filters);

        StoreDto sdto3 = new StoreDto();
        sdto3.streamerInfo("{\"filename\": \"test-file-1\"}");
        sdto3.since(new BigDecimal(BigInteger.valueOf(4000000L*1000000000L)));
        sdto3.hash("hashresource1");
        sdto3.setData("theresource1");

        setdto1.addResourcesItem(sdto3);
        String fileName = "theresource1";
        byte[] fileContent = "this is file content for theresource1".getBytes();
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
        headers1.add("X-Crest-PayloadFormat", "FILE");

        final HttpEntity<ByteArrayResource> partsEntity = new HttpEntity<>(byteArrayResource, parts);
        final MultiValueMap<String, Object> map1 = new LinkedMultiValueMap<String, Object>();
        map1.add("files", partsEntity);
        map1.add("tag", tagname);
        map1.add("storeset", mapper.writeValueAsString(setdto1));
        map1.add("endtime", "0");
        map1.add("objectType", "FILE");
        map1.add("compressionType", "none");
        map1.add("version", "2.0");
        final HttpEntity<MultiValueMap<String, Object>> request1 = new HttpEntity<MultiValueMap<String, Object>>(
                map1, headers1);
        final ResponseEntity<String> resp1 = this.testRestTemplate
                .postForEntity("/crestapi/payloads", request1, String.class);
        assertEquals(resp1.getStatusCode().value(), HttpStatus.CREATED.value());
        log.info("Received response: " + resp1);

    }

    @Test
    public void testPayloadRest() {
        log.info("=======> testPayloadRest ");
        try {
            initializeTag("A-TEST-WITH-PYLD");
            storeIovsAndPayload("A-TEST-WITH-PYLD");
        }
        catch (JsonProcessingException e) {
            log.error("Exception: {}", e.getMessage());
        }
    }
}
