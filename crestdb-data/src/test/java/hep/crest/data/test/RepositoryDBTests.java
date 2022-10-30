package hep.crest.data.test;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.PayloadData;
import hep.crest.data.pojo.PayloadInfoData;
import hep.crest.data.pojo.Tag;
import hep.crest.data.pojo.TagMeta;
import hep.crest.data.repositories.GlobalTagMapRepository;
import hep.crest.data.repositories.GlobalTagRepository;
import hep.crest.data.repositories.IovGroupsCustom;
import hep.crest.data.repositories.IovRepository;
import hep.crest.data.repositories.PayloadDataRepository;
import hep.crest.data.repositories.PayloadInfoDataRepository;
import hep.crest.data.repositories.PayloadRepository;
import hep.crest.data.repositories.TagMetaRepository;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
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
    private IovGroupsCustom iovGroupsRepository;

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

    @Autowired
    private PayloadRepository payloadRepository;
    @Autowired
    private PayloadDataRepository payloadDataRepository;
    @Autowired
    private PayloadInfoDataRepository payloadInfoDataRepository;
    @Autowired
    private TagMetaRepository tagMetaRepository;

    private RandomGenerator rnd = new RandomGenerator();

    @Test
    public void testGlobalTags() throws Exception {
        log.info("====> testGlobalTags: ");
        final GlobalTag mtag = (GlobalTag) rnd.generate(GlobalTag.class);
        log.info("Created global tag via random gen: {}", mtag);
        mtag.snapshotTime(null);
        mtag.insertionTime(null);
        final GlobalTag savedtag = globalTagRepository.save(mtag);
        assertTrue(savedtag != null);
        assertTrue(savedtag.toString().length() > 0);
        assertEquals(mtag.name(), savedtag.name());

        log.info("...find global tags: {}", mtag);
        List<GlobalTag> found = globalTagRepository.findByNameLike("%");
        assertTrue(found != null);
        assertTrue(found.size() > 0);

        PageRequest req = PageRequest.of(0, 10);
        GtagQueryArgs args = new GtagQueryArgs();
        args.name(mtag.name()+"%");
        args.workflow(savedtag.workflow()).scenario(savedtag.scenario())
        .description(savedtag.description()).release(savedtag.release())
        .validity(savedtag.validity());
        Page<GlobalTag> pageselect = globalTagRepository.findGlobalTagList(args, req);
        assertTrue(pageselect != null);
        assertTrue(pageselect.getTotalElements() > 0);
        // Now use tagname equal ...
        args.name(mtag.name());
        pageselect = globalTagRepository.findGlobalTagList(args, req);
        assertTrue(pageselect != null);
        assertEquals(pageselect.getTotalElements(), 1L);
    }

    @Test
    public void testTags() throws Exception {
        log.info("====> testTags: ");
        final Tag mtag = (Tag) rnd.generate(Tag.class);
        log.info("...created tag via random gen: {}", mtag);
        mtag.insertionTime(null);
        mtag.modificationTime(null);
        final Tag savedtag = tagrepository.save(mtag);
        assertTrue(savedtag != null);
        assertTrue(savedtag.toString().length() > 0);
        assertEquals(mtag.name(), savedtag.name());

        log.info("...update the tag {} by changing description:", mtag.name());
        savedtag.description("I changed the description");
        Tag updtag = tagrepository.save(savedtag);
        assertNotEquals(updtag.description(), mtag.description());
        assertTrue(updtag.modificationTime().after(savedtag.modificationTime()));

        log.info("...find tags: {}", mtag);
        List<Tag> found = tagrepository.findByNameLike("%");
        assertTrue(found != null);
        assertTrue(found.size() > 0);

        PageRequest req = PageRequest.of(0, 10);
        TagQueryArgs args = new TagQueryArgs();
        // Use tagname like ...
        args.name(mtag.name()+"%").timeType(savedtag.timeType())
                .description(savedtag.description())
                .objectType(savedtag.objectType());
        Page<Tag> pageselect = tagrepository.findTagList(args, req);
        assertTrue(pageselect != null);
        assertTrue(pageselect.getTotalElements() > 0);
        // Now use tagname equal ...
        args.name(mtag.name());
        pageselect = tagrepository.findTagList(args, req);
        assertTrue(pageselect != null);
        assertEquals(pageselect.getTotalElements(), 1L);
    }

    @Test
    public void testIovs() throws Exception {
        log.info("====> testIovs: ");
        final Tag mtag = (Tag) rnd.generate(Tag.class);
        final Tag savedtag = tagrepository.save(mtag);
        final IovId id = new IovId().tagName(savedtag.name()).since(BigInteger.valueOf(999L)).insertionTime(new Date());
        final Iov miov = (Iov) rnd.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final Iov savediov = iovrepository.save(miov);
        assertTrue(savediov.toString().length() > 0);

        final IovId id2 = new IovId().tagName(savedtag.name()).since(BigInteger.valueOf(1010L)).insertionTime(new Date());
        final Iov miov2 = (Iov) rnd.generate(Iov.class);
        miov2.id(id2);
        miov2.id().insertionTime(null);
        final Iov savediov2 = iovrepository.save(miov2);
        assertTrue(savediov2.toString().length() > 0);

        log.info("...find iov by tag: {}", mtag);
        PageRequest req = PageRequest.of(0, 10);
        Page<Iov> pagefound = iovrepository.findByIdTagName(mtag.name(), req);
        assertTrue(pagefound != null);
        assertTrue(pagefound.getTotalElements() > 0);

        IovQueryArgs args = new IovQueryArgs();
        args.mode(IovModeEnum.RANGES);
        args.since(BigInteger.valueOf(1000L)).until(BigInteger.valueOf(1020L)).tagName(savedtag.name());
        if (args.checkArgsNull(null)) {
            log.warn("Relevant arguments are null");
        }
        else {
            Page<Iov> pageselect = iovrepository.findIovList(args, req);
            assertTrue(pageselect != null);
            assertTrue(pageselect.getTotalElements() > 0);

            args.snapshot(Timestamp.from(Instant.now()));
            args.mode(IovModeEnum.IOVS);
            args.tagName(mtag.name() + "%");
            //args.hash(savediov.payloadHash());
            pageselect = iovrepository.findIovList(args, req);
            assertTrue(pageselect != null);
            assertTrue(pageselect.getTotalElements() > 0);
        }
        log.info("Try to get back one IOV only using AT");
        args.mode(IovModeEnum.AT);
        args.since(BigInteger.valueOf(1001L)).until(null).tagName(savedtag.name());
        if (args.checkArgsNull(null)) {
            log.warn("Relevant arguments are null");
        }
        else {
            Page<Iov> pageselect = iovrepository.findIovList(args, req);
            assertTrue(pageselect != null);
            assertTrue(pageselect.getTotalElements() > 0);
            log.info("Found AT: {}", pageselect.toList());
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
        assertTrue(savedmap != null);
        assertEquals(savedmap.id().globalTagName(), savedgtag.name());
        // Fetch data
        List<GlobalTagMap> found = globalTagMapRepository.findByGlobalTagName(savedgtag.name());
        assertTrue(found != null);
        assertTrue(found.size() > 0);
        GlobalTagMap m = found.get(0);
        assertTrue(m.toString().length() > 0);

        Optional<GlobalTag> gt = globalTagRepository.findGlobalTagFetchTags(savedgtag.name(), "testrecord",
                "testlabel");
        assertTrue(gt.isPresent());
        assertEquals(gt.get().name(), savedgtag.name());
        log.info("Found global tag {}", gt.get());

        Optional<GlobalTag> gt1 = globalTagRepository.findGlobalTagFetchTags(savedgtag.name(), "testrecord",
                "notthere");
        assertFalse(gt1.isPresent());
        log.info("Cannot found global tag for label notthere");
    }

    @Test
    public void testCrestFolders() throws Exception {
        log.info("====> testCrestFolders: ");
        final CrestFolders entity = (CrestFolders) rnd.generate(CrestFolders.class);
        final CrestFolders saved = crestFoldersRepository.save(entity);
        log.info("...created folder via random gen: {}", entity);
        assertTrue(saved.toString().length() > 0);
        String grouprole = entity.groupRole();
        List<CrestFolders> found = crestFoldersRepository.findByGroupRole(grouprole);
        assertTrue(found != null);
        assertTrue(found.size() > 0);
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
        assertTrue(saved.toString().length() > 0);
        assertTrue(saved2.toString().length() > 0);
        Page<RunLumiInfo> found =
                runLumiInfoRepository.findByRunNumberInclusive(BigInteger.valueOf(900L),
                        BigInteger.valueOf(1010L), PageRequest.of(0,10));
        assertTrue(found != null);
        assertTrue(found.getTotalElements() > 0);
    }

    @Test
    public void testGroups() throws Exception {
        final Instant now = Instant.now();
        log.info("====> testGroups: ");
        final Tag mtag = (Tag) rnd.generate(Tag.class);
        log.info("...created tag via random gen: {}", mtag);
        mtag.insertionTime(null);
        mtag.modificationTime(null);
        final Tag savedtag = tagrepository.save(mtag);

        final IovId id = new IovId().tagName(savedtag.name()).since(BigInteger.valueOf(900L)).insertionTime(new Date());
        final Iov miov = (Iov) rnd.generate(Iov.class);
        miov.id(id);
        miov.id().insertionTime(null);
        log.info("...created iov via random gen: {}", miov);
        final Iov savediov = iovrepository.save(miov);

        final Iov m1iov = (Iov) rnd.generate(Iov.class);
        m1iov.id(id);
        m1iov.id().insertionTime(null);
        m1iov.id().since(BigInteger.valueOf(1010L));
        log.info("...created iov via random gen: {}", m1iov);
        final Iov savediov1 = iovrepository.save(m1iov);

        List<BigInteger> groupList = iovGroupsRepository.selectGroups(mtag.name(), 100L);
        assertTrue(groupList != null);
        assertTrue(groupList.size() > 0);
    }

    @Test
    public void test1_Payload() throws Exception {
        // This is a trick, we do not have a postgresql connection
        //final PayloadDataBaseCustom repobean = new PayloadDataPostgresImpl(mainDataSource);
        final Instant now = Instant.now();
        Payload entity = (Payload) rnd.generate(Payload.class);
        PayloadData content = new PayloadData();
        content.hash(entity.hash());
        PayloadInfoData streamer = new PayloadInfoData();
        streamer.hash(entity.hash());
        streamer.streamerInfo("{key: val}".getBytes(StandardCharsets.UTF_8));
        log.info("Save payload {}", entity);
        byte[] dataBlob = "This is a very long blob".getBytes(StandardCharsets.UTF_8);
        Payload saved  = payloadRepository.save(entity);
        assertTrue(saved != null);
        payloadDataRepository.saveData(content.hash(), new ByteArrayInputStream(dataBlob), dataBlob.length);
        PayloadInfoData savedStreamer = payloadInfoDataRepository.save(streamer);
        assertTrue(savedStreamer != null);

        Optional<Payload> found = payloadRepository.findById(saved.hash());
        assertTrue(found.isPresent());
        Optional<PayloadData> foundData = payloadDataRepository.findById(saved.hash());
        assertTrue(foundData.isPresent());
        Optional<PayloadInfoData> foundStreamer = payloadInfoDataRepository.findById(saved.hash());
        assertTrue(foundStreamer.isPresent());

        byte[] retcontent = readLob(saved.hash());
        assertTrue( retcontent.length > 0);
        assertEquals(retcontent[0], dataBlob[0]);
        assertEquals(retcontent.length, dataBlob.length);

        byte[] sinfo = foundStreamer.get().streamerInfo();
        assertTrue(sinfo.length > 0);
    }

    @Transactional
    protected byte[] readLob(String id) {
        try (InputStream is = payloadDataRepository.findData(id);) {
            byte[] arr = is.readAllBytes();
            return arr;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test1_Tags() throws Exception {
        final Instant now = Instant.now();
        Tag mtag = (Tag) rnd.generate(Tag.class);
        mtag.insertionTime(Timestamp.from(now));
        mtag.modificationTime(Timestamp.from(now));
        log.info("Save tag {}", mtag);
        final Tag savedtag = tagrepository.save(mtag);
        assertEquals(savedtag.name(), mtag.name());

        TagMeta meta = (TagMeta) rnd.generate(TagMeta.class);
        meta.tagName(savedtag.name());
        meta.tagInfo("{\"channels\": [0: \"name0\"]}".getBytes(StandardCharsets.UTF_8));
        final TagMeta savedmeta = tagMetaRepository.save(meta);
        log.info("Save tag meta information {}", savedmeta);
        assertTrue(savedmeta != null);

        final Optional<TagMeta> storedmetaopt = tagMetaRepository.findByTagName(savedmeta.tagName());
        assertTrue(storedmetaopt.isPresent());

    }

}
