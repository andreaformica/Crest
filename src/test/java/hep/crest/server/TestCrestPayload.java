package hep.crest.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovPayloadSetDto;
import hep.crest.server.swagger.model.PayloadSetDto;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
                .since(BigInteger.valueOf(3000000L * 1000000000L)).insertionTime(new Date());
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

        StoreDto sdto3 = new StoreDto();
        sdto3.streamerInfo("{\"filename\": \"test-file-1\"}");
        sdto3.since(new BigDecimal(BigInteger.valueOf(4000000L * 1000000000L)));
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

        final HttpEntity<ByteArrayResource> partsEntity = new HttpEntity<>(byteArrayResource, parts);
        final MultiValueMap<String, Object> map1 = new LinkedMultiValueMap<String, Object>();
        map1.add("files", partsEntity);
        map1.add("tag", tagname);
        map1.add("storeset", mapper.writeValueAsString(setdto1));
        final HttpEntity<MultiValueMap<String, Object>> request1 = new HttpEntity<MultiValueMap<String, Object>>(
                map1, headers1);
        final ResponseEntity<String> resp1 = this.testRestTemplate
                .postForEntity("/crestapi/payloads", request1, String.class);
        assertEquals(resp1.getStatusCode().value(), HttpStatus.CREATED.value());
        log.info("Received response: " + resp1);
        {
            StoreSetDto respb = mapper.readValue(resp1.getBody(), StoreSetDto.class);
            StoreDto dto = respb.getResources().get(0);
            log.info("Found stored payload in response : {}", dto);
            return dto.getHash();
        }
    }

    @Test
    public void testPayloadRest() {
        log.info("=======> testPayloadRest ");
        try {
            String tagname = "A-TEST-WITH-PYLD";
            initializeTag(tagname);
            storeIovsAndPayload(tagname);
            String hash = postPayloadFromFile(tagname);

            log.info("Retrieve all payloads:");
            final ResponseEntity<String> resp = this.testRestTemplate.exchange("/crestapi/payloads",
                    HttpMethod.GET, null, String.class);

            {
                log.info("Retrieved all payloads {} ", resp.getBody());
                final String responseBody = resp.getBody();
                assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
                PayloadSetDto ok;
                log.info("Response from server is: " + responseBody);
                ok = mapper.readValue(responseBody, PayloadSetDto.class);
                assertTrue(ok.getSize() > 0);
            }

            // Retrieve a payload metadata and streamer info from hash.
            log.info("Retrieve payload for hash : {}", hash);
            final ResponseEntity<String> resp3 = this.testRestTemplate.exchange("/crestapi/payloads/" + hash,
                    HttpMethod.GET, null, String.class);
            log.info("Received payload : {}", resp3);
            assertTrue(resp3.getBody().contains("theresource1"));

            log.info("Insert a new iov with the same hash : {}", hash);
            IovDto dto = new IovDto();
            dto.setSince(new BigDecimal(BigInteger.valueOf(5000000L)));
            Instant now = Instant.now();
            dto.insertionTime(now.atOffset(ZoneOffset.UTC));
            dto.tagName(tagname).payloadHash(hash);
            final HttpEntity<IovDto> request = new HttpEntity<>(dto);
            final ResponseEntity<String> respiov1 =
                    this.testRestTemplate.exchange("/crestapi/iovs/",
                    HttpMethod.PUT, request, String.class);
            log.info("Insert new iov with the same payload : {}", respiov1);
            assertTrue(respiov1.getBody().contains(tagname));

            log.info("Retrieve payload streamer for hash : {}", hash);
            String format = "?format=STREAMER";
            final ResponseEntity<String> resp2 = this.testRestTemplate.exchange("/crestapi/payloads/" + hash + format,
                    HttpMethod.GET, null, String.class);
            log.info("Received payload : {}", resp2);
            assertTrue(resp2.getBody().contains("test-file-1"));

            format = "?hash=" + hash;
            final ResponseEntity<String> resp1 = this.testRestTemplate.exchange("/crestapi/payloads" + format,
                    HttpMethod.GET, null, String.class);

            {
                log.info("Retrieved all payloads {} ", resp1.getBody());
                final String responseBody = resp1.getBody();
                assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
                PayloadSetDto ok;
                log.info("Response from server is: " + responseBody);
                ok = mapper.readValue(responseBody, PayloadSetDto.class);
                assertTrue(ok.getSize() > 0);
            }

            log.info("update streamer info");
            Map<String, String> map = new HashMap<>();
            map.put("streamerInfo", "new streamer info");
            final HttpHeaders headers = new HttpHeaders();
            final HttpEntity<Map<String, String>> req0 = new HttpEntity<>(
                    map, headers);
            format = "/" + hash;
            final ResponseEntity<String> resp0 = this.testRestTemplate.exchange("/crestapi/payloads" + format,
                    HttpMethod.PUT, req0, String.class);

            {
                log.info("Update streamer info {} ", resp0.getBody());
                final String responseBody = resp0.getBody();
                assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
                PayloadSetDto ok;
                log.info("Response from server is: " + responseBody);
                ok = mapper.readValue(responseBody, PayloadSetDto.class);
                assertTrue(ok.getSize() > 0);
            }

            // Retrieve iovs and payloads
            format = "?tagname=" + tagname;
            final ResponseEntity<String> resp5 = this.testRestTemplate.exchange("/crestapi/iovs/infos" + format,
                    HttpMethod.GET, null, String.class);

            {
                log.info("Retrieved all iovs and payloads {} ", resp5.getBody());
                final String responseBody = resp5.getBody();
                assertEquals(resp5.getStatusCode(), HttpStatus.OK);
                IovPayloadSetDto ok;
                log.info("Response from server is: " + responseBody);
                ok = mapper.readValue(responseBody, IovPayloadSetDto.class);
                assertTrue(ok.getSize() > 0);
            }

            // Retrieve size by tag
            format = "?tagname=" + tagname;
            final ResponseEntity<String> respiovsize =
                    this.testRestTemplate.exchange("/crestapi/iovs/size" + format,
                    HttpMethod.GET, null, String.class);
            {
                log.info("Retrieved size by tag {} ", respiovsize.getBody());
                final String responseBody = respiovsize.getBody();
                assertEquals(respiovsize.getStatusCode(), HttpStatus.OK);
            }
            // Retrieve monitoring by tag
            format = "?tagname=" + tagname;
            final ResponseEntity<String> respmon =
                    this.testRestTemplate.exchange("/crestapi/monitoring/payloads" + format,
                            HttpMethod.GET, null, String.class);
            {
                log.info("Retrieved monitoring by tag {} ", respmon.getBody());
                final String responseBody = respmon.getBody();
                assertEquals(respmon.getStatusCode(), HttpStatus.OK);
            }

            // Retrieve iovs and payloads
            final ResponseEntity<String> resp6 = this.testRestTemplate.exchange("/crestapi/admin/tags/" + tagname,
                    HttpMethod.DELETE, null, String.class);

            {
                log.info("Removed all iovs and payloads {} ", resp6.getBody());
                final String responseBody = resp6.getBody();
                assertEquals(resp6.getStatusCode(), HttpStatus.OK);
                log.info("Removed tag received {}", responseBody);
            }

        }
        catch (JsonProcessingException e) {
            log.error("Exception: {}", e.getMessage());
        }
    }
}
