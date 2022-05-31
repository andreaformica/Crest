package hep.crest.data.test;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.GlobalTagMapRepository;
import hep.crest.data.repositories.GlobalTagRepository;
import hep.crest.data.repositories.IovRepository;
import hep.crest.data.repositories.TagRepository;
import hep.crest.data.repositories.args.GtagQueryArgs;
import hep.crest.data.repositories.args.IovModeEnum;
import hep.crest.data.repositories.args.IovQueryArgs;
import hep.crest.data.repositories.args.TagQueryArgs;
import hep.crest.data.runinfo.pojo.RunLumiInfo;
import hep.crest.data.runinfo.repositories.RunLumiInfoRepository;
import hep.crest.data.security.pojo.CrestFolders;
import hep.crest.data.security.pojo.CrestFoldersRepository;
import hep.crest.data.test.tools.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;

@DataJpaTest
@ActiveProfiles("test")
@Slf4j
public class RepositoryDBTests {


    /**
     * Repository.
     */
    @Autowired
    private TagRepository tagrepository;

    /**
     * Repository.
     */
    @Autowired
    private IovRepository iovrepository;

    /**
     * Repository.
     */
    @Autowired
    private GlobalTagRepository globalTagRepository;
    /**
     * Repository.
     */
    @Autowired
    private GlobalTagMapRepository globalTagMapRepository;
    /**
     * Repository.
     */
    @Autowired
    private RunLumiInfoRepository runLumiInfoRepository;
    /**
     * Repository.
     */
    @Autowired
    private CrestFoldersRepository crestFoldersRepository;

    private RandomGenerator rnd = new RandomGenerator();

    @Test
    public void testGlobalTags() throws Exception {
        log.info("====> testGlobalTags: ");
        final GlobalTag mtag = (GlobalTag) rnd.generate(GlobalTag.class);
        log.info("Created global tag via random gen: {}", mtag);
        mtag.snapshotTime(null);
        mtag.insertionTime(null);
        final GlobalTag savedtag = globalTagRepository.save(mtag);
        assertThat(savedtag).isNotNull();
        assertThat(savedtag.toString().length()).isPositive();
        assertThat(mtag.name()).isEqualTo(savedtag.name());

        log.info("...find global tags: {}", mtag);
        List<GlobalTag> found = globalTagRepository.findByNameLike("%");
        assertThat(found).isNotNull();
        assertThat(found.size()).isPositive();

        PageRequest req = PageRequest.of(0, 10);
        GtagQueryArgs args = new GtagQueryArgs();
        args.name(mtag.name()+"%");
        args.workflow(savedtag.workflow()).scenario(savedtag.scenario())
        .description(savedtag.description()).release(savedtag.release())
        .validity(savedtag.validity());
        Page<GlobalTag> pageselect = globalTagRepository.findGlobalTagList(args, req);
        assertThat(pageselect).isNotNull();
        assertThat(pageselect.getTotalElements()).isPositive();
        // Now use tagname equal ...
        args.name(mtag.name());
        pageselect = globalTagRepository.findGlobalTagList(args, req);
        assertThat(pageselect).isNotNull();
        assertThat(pageselect.getTotalElements()).isEqualTo(1L);
    }

    @Test
    public void testTags() throws Exception {
        log.info("====> testTags: ");
        final Tag mtag = (Tag) rnd.generate(Tag.class);
        log.info("...created tag via random gen: {}", mtag);
        mtag.insertionTime(null);
        mtag.modificationTime(null);
        final Tag savedtag = tagrepository.save(mtag);
        assertThat(savedtag).isNotNull();
        assertThat(savedtag.toString().length()).isPositive();
        assertThat(mtag.name()).isEqualTo(savedtag.name());
        log.info("...update the tag {} by changing description:", mtag.name());
        savedtag.description("I changed the description");
        Tag updtag = tagrepository.save(savedtag);
        assertThat(updtag.description()).isNotEqualTo(mtag.description());
        assertThat(updtag.modificationTime()).isAfterOrEqualTo(savedtag.modificationTime());

        log.info("...find tags: {}", mtag);
        List<Tag> found = tagrepository.findByNameLike("%");
        assertThat(found).isNotNull();
        assertThat(found.size()).isPositive();

        PageRequest req = PageRequest.of(0, 10);
        TagQueryArgs args = new TagQueryArgs();
        // Use tagname like ...
        args.name(mtag.name()+"%").timeType(savedtag.timeType())
                .description(savedtag.description())
                .objectType(savedtag.objectType());
        Page<Tag> pageselect = tagrepository.findTagList(args, req);
        assertThat(pageselect).isNotNull();
        assertThat(pageselect.getTotalElements()).isPositive();
        // Now use tagname equal ...
        args.name(mtag.name());
        pageselect = tagrepository.findTagList(args, req);
        assertThat(pageselect).isNotNull();
        assertThat(pageselect.getTotalElements()).isEqualTo(1L);
    }

    @Test
    public void testIovs() throws Exception {
        log.info("====> testIovs: ");
        final Tag mtag = (Tag) rnd.generate(Tag.class);
        final Tag savedtag = tagrepository.save(mtag);
        final IovId id = new IovId().tagName(savedtag.name()).since(new BigDecimal(999L)).insertionTime(new Date());
        final Iov miov = (Iov) rnd.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final Iov savediov = iovrepository.save(miov);
        assertThat(savediov.toString().length()).isPositive();

        final IovId id2 = new IovId().tagName(savedtag.name()).since(new BigDecimal(1010L)).insertionTime(new Date());
        final Iov miov2 = (Iov) rnd.generate(Iov.class);
        miov2.id(id2);
        miov2.id().insertionTime(null);
        final Iov savediov2 = iovrepository.save(miov2);
        assertThat(savediov2.toString().length()).isPositive();

        log.info("...find iov by tag: {}", mtag);
        PageRequest req = PageRequest.of(0, 10);
        Page<Iov> pagefound = iovrepository.findByIdTagName(mtag.name(), req);
        assertThat(pagefound).isNotNull();
        assertThat(pagefound.getTotalElements()).isPositive();

        IovQueryArgs args = new IovQueryArgs();
        args.mode(IovModeEnum.RANGES);
        args.since(BigDecimal.valueOf(1000L)).until(BigDecimal.valueOf(1020L)).tagName(savedtag.name());
        if (args.checkArgsNull(null)) {
            log.warn("Relevant arguments are null");
        }
        else {
            Page<Iov> pageselect = iovrepository.findIovList(args, req);
            assertThat(pageselect).isNotNull();
            assertThat(pageselect.getTotalElements()).isPositive();

            args.snapshot(Timestamp.from(Instant.now()));
            args.mode(IovModeEnum.IOVS);
            args.tagName(mtag.name() + "%");
            //args.hash(savediov.payloadHash());
            pageselect = iovrepository.findIovList(args, req);
            assertThat(pageselect).isNotNull();
            assertThat(pageselect.getTotalElements()).isPositive();
        }
        log.info("Try to get back one IOV only using AT");
        args.mode(IovModeEnum.AT);
        args.since(BigDecimal.valueOf(1001L)).until(null).tagName(savedtag.name());
        if (args.checkArgsNull(null)) {
            log.warn("Relevant arguments are null");
        }
        else {
            Page<Iov> pageselect = iovrepository.findIovList(args, req);
            log.info("Found AT: {}", pageselect.toList());
            assertThat(pageselect).isNotNull();
            assertThat(pageselect.getTotalElements()).isPositive();
        }
    }

    @Test
    public void testMappings() throws Exception {
        log.info("====> testMappings: ");
        final Tag mtag = (Tag) rnd.generate(Tag.class);
        final Tag savedtag = tagrepository.save(mtag);
        final GlobalTag gtag = (GlobalTag) rnd.generate(GlobalTag.class);
        log.info("...created global tag via random gen: {}", gtag);
        final GlobalTag savedgtag = globalTagRepository.save(gtag);
        GlobalTagMap map = new GlobalTagMap();
        map.tag(savedtag);
        map.globalTag(savedgtag);
        GlobalTagMapId id = new GlobalTagMapId();
        id.label("testlabel");
        id.record("testrecord");
        id.globalTagName(savedgtag.name());
        map.id(id);
        log.info("...created mapping : {}", map);
        final GlobalTagMap savedmap = globalTagMapRepository.save(map);
        assertThat(savedmap.toString().length()).isPositive();
        List<GlobalTagMap> found = globalTagMapRepository.findByGlobalTagName(savedgtag.name());
        assertThat(found).isNotNull();
        assertThat(found.size()).isPositive();
        Optional<GlobalTag> gt = globalTagRepository.findGlobalTagFetchTags(savedgtag.name(), "testrecord",
                "testlabel");
        assertThat(gt.isPresent()).isTrue();
        assertThat(gt.get().name()).isEqualTo(savedgtag.name());
        log.info("Found global tag {}", gt.get());

        Optional<GlobalTag> gt1 = globalTagRepository.findGlobalTagFetchTags(savedgtag.name(), "testrecord",
                "notthere");
        assertThat(gt1.isPresent()).isFalse();
        log.info("Cannot found global tag for label notthere");
    }

    @Test
    public void testCrestFolders() throws Exception {
        log.info("====> testCrestFolders: ");
        final CrestFolders entity = (CrestFolders) rnd.generate(CrestFolders.class);
        final CrestFolders saved = crestFoldersRepository.save(entity);
        log.info("...created folder via random gen: {}", entity);
        assertThat(saved.toString().length()).isPositive();
        String grouprole = entity.groupRole();
        List<CrestFolders> found = crestFoldersRepository.findByGroupRole(grouprole);
        assertThat(found).isNotNull();
        assertThat(found.size()).isPositive();
    }

    @Test
    public void testRunInfo() throws Exception {
        log.info("====> testRunInfo: ");
        final RunLumiInfo entity = (RunLumiInfo) rnd.generate(RunLumiInfo.class);
        entity.runNumber(BigInteger.valueOf(1000L));
        entity.insertionTime(null);
        final RunLumiInfo saved = runLumiInfoRepository.save(entity);
        final RunLumiInfo entity2 = (RunLumiInfo) rnd.generate(RunLumiInfo.class);
        entity2.runNumber(BigInteger.valueOf(1020L));
        final RunLumiInfo saved2 = runLumiInfoRepository.save(entity2);
        log.info("...created runlumi via random gen: {}, {}", entity, entity2);
        assertThat(saved.toString().length()).isPositive();
        assertThat(saved2.toString().length()).isPositive();
        Page<RunLumiInfo> found =
                runLumiInfoRepository.findByRunNumberInclusive(BigInteger.valueOf(900L),
                        BigInteger.valueOf(1010L), PageRequest.of(0,10));
        assertThat(found).isNotNull();
        assertThat(found.getTotalElements()).isPositive();
    }

}
