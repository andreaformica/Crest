package hep.crest.server;


import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.IovRepository;
import hep.crest.data.repositories.TagRepository;
import hep.crest.server.repositories.IovGroupsCustom;
import hep.crest.server.swagger.model.TagSummaryDto;
import hep.crest.server.utils.RandomGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RepositoryTests {

    private static final Logger log = LoggerFactory.getLogger(RepositoryTests.class);

    @Autowired
    @Qualifier("iovgroupsrepo")
    private IovGroupsCustom repobean;

    @Autowired
    private TagRepository tagrepository;

    @Autowired
    private IovRepository iovRepository;

    @Autowired
    @Qualifier("dataSource")
    private DataSource mainDataSource;

    private RandomGenerator rndgen = new RandomGenerator();

    @Test
    public void testGroups() throws Exception {
        final Instant now = Instant.now();
        log.info("====> testTags: ");
        final Tag mtag = (Tag) rndgen.generate(Tag.class);
        log.info("...created tag via random gen: {}", mtag);
        mtag.insertionTime(null);
        mtag.modificationTime(null);
        final Tag savedtag = tagrepository.save(mtag);

        final IovId id = new IovId().tagName(savedtag.name()).since(BigDecimal.valueOf(999L)).insertionTime(new Date());
        final Iov miov = (Iov) rndgen.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final Iov savediov = iovRepository.save(miov);

        final Iov m1iov = (Iov) rndgen.generate(Iov.class);
        m1iov.id(id);
        m1iov.id().insertionTime(null);
        m1iov.id().since(BigDecimal.valueOf(1010L));
        log.info("...created iov via random gen: {}", m1iov);
        final Iov savediov1 = iovRepository.save(m1iov);

        Long niovs = repobean.getSize(mtag.name());
        assertThat(niovs).isEqualTo(2);

        List<BigDecimal> grouplist = repobean.selectGroups(mtag.name(), 100L);
        assertThat(grouplist).isNotNull();
        assertThat(grouplist.size()).isPositive();

        List<BigDecimal> grouplistsnap = repobean.selectSnapshotGroups(mtag.name(), new Date(), 100L);
        assertThat(grouplistsnap).isNotNull();
        assertThat(grouplistsnap.size()).isPositive();

        List<TagSummaryDto> taginfo = repobean.getTagSummaryInfo(mtag.name());
        assertThat(taginfo).isNotNull();
        assertThat(taginfo.size()).isPositive();

        Long niovssnap = repobean.getSizeBySnapshot(mtag.name(), new Date());
        assertThat(niovssnap).isEqualTo(2);
    }
}
