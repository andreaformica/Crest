/**
<<<<<<< HEAD
 * 
=======
 *
>>>>>>> cms-2021
 */
package hep.crest.data.test;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.Tag;
import hep.crest.data.pojo.TagMeta;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author formica
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PojoTests {

    private static final Logger log = LoggerFactory.getLogger(PojoTests.class);

    @Test
    public void testGlobalTagMap() throws Exception {
        GlobalTagMapId mapid = new GlobalTagMapId();
        mapid.record("somerecord").label("somelabel").globalTagName("TGT-01");

        GlobalTagMapId mapid2 = new GlobalTagMapId();
        mapid2.record("somerecord").label("somelabel").globalTagName("TGT-02");

        GlobalTagMapId mapid3 = new GlobalTagMapId();
        mapid3.record("somerecord").label("somelabel2").globalTagName("TGT-01");

        assertThat(mapid).isNotEqualTo(mapid2).isNotEqualTo(mapid3);
        assertThat(mapid.hashCode()).isNotZero();

        mapid3.record("somerecord2").label("somelabel").globalTagName("TGT-01");

        assertThat(mapid).isNotEqualTo(mapid3);
        assertThat(mapid3).isNotNull();

        Tag tag = new Tag().name("TAG-01");
        GlobalTag gtag = new GlobalTag().name("GT-01");
        GlobalTagMap map = new GlobalTagMap(mapid, tag, gtag);
        assertThat(map.globalTag().name()).isEqualTo("GT-01");
        assertThat(map).isNotNull();

        TagMeta meta = new TagMeta();
    }

    @Test
    public void testIovId() throws Exception {
        IovId iovid = new IovId();
        Long now = Instant.now().toEpochMilli();
        iovid.since(new BigDecimal(now));
        iovid.tagName("TEST-TAG-01");
        Date instime = iovid.insertionTime();
        iovid.insertionTime(new Date(now));
        IovId iovid1 = new IovId();
        iovid1.since(new BigDecimal(now));
        iovid1.tagName("TEST-TAG-01");
        Date instime1 = iovid1.insertionTime();
        IovId iovid2 = new IovId();
        iovid2.since(new BigDecimal(now));
        iovid2.tagName(null);

        assertThat(iovid.hashCode()).isNotZero();
        assertThat(iovid2).isNotNull().isNotEqualTo(iovid).isNotEqualTo(iovid1);
    }

    @Test
    public void testPayload() throws Exception {
        Payload pyld = new Payload();
        Long now = Instant.now().toEpochMilli();
        pyld.size(100);
        pyld.hash("somehash");
        pyld.insertionTime(new Date(now));
        pyld.objectType("sometype");
        pyld.version("someversion");
        assertThat(pyld.size()).isPositive();
        assertThat(pyld.version().length()).isPositive();
        assertThat(pyld.objectType().length()).isPositive();

        assertThat(pyld.hashCode()).isNotZero();

        Payload pyld1 = new Payload().hash("somehash").objectType("anotherobj").insertionTime(pyld.insertionTime());
        assertThat(pyld1).isNotNull();
    }

}
