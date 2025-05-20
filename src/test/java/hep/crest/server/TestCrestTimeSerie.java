package hep.crest.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.converters.HashGenerator;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.pojo.TagMeta;
import hep.crest.server.services.IovService;
import hep.crest.server.services.TagMetaService;
import hep.crest.server.services.TagService;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.GlobalTagMapDto;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.IovSetDto;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagMetaDto;
import hep.crest.server.swagger.model.TagSetDto;
import hep.crest.server.swagger.model.TagSummarySetDto;
import hep.crest.server.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
@Slf4j
public class TestCrestTimeSerie {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TagMetaService tagMetaService;
    @Autowired
    private TagService tagService;

    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper mapper;

    public void initializeTag(String tagname) {
        TagDto dto = new TagDto();
        dto.name(tagname);
        dto.setPayloadSpec("crest-json-timeserie");
        dto.status(TagDto.StatusEnum.UNLOCKED);
        dto.setDescription("A test tag");
        dto.setTimeType("time");
        dto.synchronization(TagDto.SynchronizationEnum.ALL);
        log.info("Store tag : {} ", dto);
        final ResponseEntity<TagDto> response = testRestTemplate
                .postForEntity("/crestapi/tags", dto, TagDto.class);
        log.info("Received response: {}", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // Insert TagMeta
        TagMetaDto tagMetaDto = new TagMetaDto();
        tagMetaDto.setTagName(tagname);
        tagMetaDto.setChansize(1);
        tagMetaDto.setColsize(1);
        tagMetaDto.setDescription("A test tag meta");
        String tagInfo = "{\"channel_list\":[{\"0\":\"\"}],"
                + "\"node_description\":\"<timeStamp>time</timeStamp><addrHeader><address_header "
                + "service_type=\\\"71\\\" clid=\\\"254546453\\\" /> "
                + "</addrHeader>\", \"payload_spec\":[{\"veto\":\"Int32\"}]}";

        tagMetaDto.setTagInfo(tagInfo);
        // Insert the tag meta
        log.info("Store tag meta : {} ", tagMetaDto);
        final ResponseEntity<TagMetaDto> response2 = testRestTemplate
                .postForEntity("/crestapi/tags/" + tagname + "/meta", tagMetaDto, TagMetaDto.class);
        log.info("Received response: {}", response2);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    @Test
    public void testTagRest() {
        log.info("=======> testTagRest ");
        String tagname = "A-TEST-TAG-TIMESERIE";
        initializeTag(tagname);

        // Now fill the tag with IOVs
        StoreSetDto storeSetDto = new StoreSetDto();
        StoreDto sdto = new StoreDto();
        String payload = "{ \"until\": 2000, \"channelId\": 0, \"data\": "
                + "\"A_FAKE_PAYLOAD_TO_TEST\" }";
        sdto.data(payload);
        sdto.since(1000L);
        sdto.streamerInfo("A_FAKE_STREAMER_INFO");
        storeSetDto.addresourcesItem(sdto);

        // Store the payloads into the tag
        try {
            final ResponseEntity<String> response2 = uploadJson(tagname, storeSetDto, "crest-json"
                            + "-timeserie",
                    "none",
                    "1.0", "0");
            {
                log.info("Created payloads for tag {} ", response2.getBody());
                assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            }
        }
        catch (JsonProcessingException e) {
            log.error("Error in processing json: ", e);
        }
        // Retrieve the IOVs
        // Verify IOV loading
        final ResponseEntity<IovSetDto> response3 = testRestTemplate
                .getForEntity("/crestapi/iovs?tagname=" + tagname, IovSetDto.class);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);
        response3.getBody().getResources().forEach(iov -> {
            log.info("Found time serie iov: {}", iov);
        });
        // Remove the tag
        removeTag(tagname);
    }

    public void removeTag(String tagname) {
        log.info("======== removeTag {} ", tagname);
        String url = "/crestapi/admin/tags/" + tagname;
        ResponseEntity<String> resp = testRestTemplate
                .exchange(url, HttpMethod.DELETE, null, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public ResponseEntity<String> uploadJson(String tag, StoreSetDto storesetDto,
                                             String objectType, String compressionType,
                                             String version, String endtime) throws JsonProcessingException {
        // Prepare the multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        byte[] data = mapper.writeValueAsBytes(storesetDto);
        // Add the StoreSetDto as a file part
        ByteArrayResource storesetResource = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return "storeset.json"; // Filename for the file part
            }
        };
        body.add("storeset", storesetResource);

        // Add other form parameters
        body.add("tag", tag);
        body.add("objectType", objectType);
        body.add("compressionType", compressionType);
        body.add("version", version);
        body.add("endtime", endtime);

        // Set headers for multipart/form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Create the request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send the request
        //
        return testRestTemplate.exchange(
                "/crestapi/payloads", // Replace with the actual URL
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
    }
}
