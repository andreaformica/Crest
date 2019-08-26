package hep.crest.server.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import hep.crest.swagger.model.GlobalTagDto;
import hep.crest.swagger.model.GlobalTagMapDto;
import hep.crest.swagger.model.TagDto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("default") 
public class TestCrestGlobalTag {
    @Autowired
    private TestRestTemplate testRestTemplate;
   
    @Test
    public void testA_getAndRemoveGlobaltags() {
        ResponseEntity<GlobalTagDto[]> response = this.testRestTemplate.getForEntity("/crestapi/globaltags", GlobalTagDto[].class);
        GlobalTagDto[] gtaglist = response.getBody();
        for (GlobalTagDto globalTagDto : gtaglist) {
        		String url = "/crestapi/admin/globaltags/"+globalTagDto.getName();
        		System.out.println("Removing global tag "+url);
            this.testRestTemplate.delete(url);
		}
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testB_storeGlobaltags() {
    		GlobalTagDto dto = new GlobalTagDto().description("test").name("MY_SB_TEST").release("1").scenario("test").type("test").workflow("M").validity(new BigDecimal(0)).snapshotTime(new Date()).insertionTime(new Date());
        System.out.println("Store request: "+dto);
        ResponseEntity<GlobalTagDto> response = this.testRestTemplate.postForEntity("/crestapi/globaltags", dto, GlobalTagDto.class);
        System.out.println("Received response: "+response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
    
    @Test
    public void testC_storeGlobalTagMap() {
		TagDto dto = new TagDto().description("test").name("SB_TAG_01").endOfValidity(new BigDecimal(1))
				.lastValidatedTime(new BigDecimal(1)).payloadSpec("test").synchronization("BLK").timeType("run")
				.modificationTime(new Date()).insertionTime(new Date());
		System.out.println("Store request: " + dto);
		ResponseEntity<TagDto> response = this.testRestTemplate.postForEntity("/crestapi/tags", dto, TagDto.class);
		System.out.println("Received response: " + response);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		
    	GlobalTagMapDto mapdto = new GlobalTagMapDto().globalTagName("MY_SB_TEST").label("label").record("0").tagName("SB_TAG_01");
        System.out.println("Store request: "+mapdto);
        ResponseEntity<GlobalTagMapDto> response2 = this.testRestTemplate.postForEntity("/crestapi/globaltagmaps", mapdto, GlobalTagMapDto.class);
        System.out.println("Received response: "+response2);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testC_getAllGlobaltags() {
        ResponseEntity<GlobalTagDto[]> response = this.testRestTemplate.getForEntity("/crestapi/globaltags", GlobalTagDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(0);
    }

}
