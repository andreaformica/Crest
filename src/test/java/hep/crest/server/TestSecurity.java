package hep.crest.server;

import hep.crest.server.data.pojo.CrestRoles;
import hep.crest.server.data.pojo.CrestUser;
import hep.crest.server.data.repositories.CrestRolesRepository;
import hep.crest.server.data.repositories.CrestUserRepository;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.swagger.model.TagSetDto;
import hep.crest.server.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test","dbsecurity"})
@ContextConfiguration
@Slf4j
public class TestSecurity {

    @Autowired
    private CrestUserRepository userRepository;
    @Autowired
    private CrestRolesRepository roleRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final RandomGenerator rnd = new RandomGenerator();
    String username = "testuser";
    String password = "testpassword";


    public void initUserAndRoles() {
        CrestUser user = new CrestUser();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setId("crest-testuser");

        CrestRoles role = new CrestRoles();
        role.setRole("crest-testuser");
        role.setTagPattern("SPECIAL");

        userRepository.save(user);
        roleRepository.save(role);

        log.info("====> testUser: ");
        userRepository.findAll().forEach(usr -> {
            log.info("...user: {}", usr);
        });
        log.info("====> testRoles: ");
        roleRepository.findAll().forEach(r -> {
            log.info("...role: {}", r);
        });
    }

    public void initializeTag(String tgname) {
        TagDto dto = (TagDto) rnd.generate(TagDto.class);
        dto.name(tgname);
        dto.status(TagDto.StatusEnum.UNLOCKED);
        dto.synchronization(TagDto.SynchronizationEnum.ALL);
        dto.setTimeType("time");
        dto.setDescription("test tag");
        dto.setPayloadSpec("crest-json-single-iov");
        dto.setLastValidatedTime(0L);
        dto.setEndOfValidity(0L);
        // Create headers with basic authentication
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        // Create an HTTP entity with the headers
        HttpEntity<TagDto> entity = new HttpEntity<>(dto, headers);
        log.info("====> headers: {}", headers);
        log.info("====> entity: {}", entity);

        log.info("Store tag : {} ", dto);
        final ResponseEntity<TagDto> response = testRestTemplate
                .postForEntity("/crestapi/tags", entity, TagDto.class);
        log.info("Received response: {}", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
    @Test
    public void testSecurity() {
        initUserAndRoles();
        initializeTag("SPECIAL-01");
        // Try to create a TAG MYTAG with the user
        // This should fail because the user has no rights to create a tag
        // Pass the authentication parameter to RestTemplate.
        String url = "/crestapi/tags?name=SPECIAL%";

        // Create headers with basic authentication
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        // Create an HTTP entity with the headers
        HttpEntity<TagDto> entity = new HttpEntity<>(headers);

        ResponseEntity<TagSetDto> response = testRestTemplate.exchange(url, HttpMethod.GET, entity,
                TagSetDto.class);

        // Assert the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // Add more assertions based on the expected response

    }
}
