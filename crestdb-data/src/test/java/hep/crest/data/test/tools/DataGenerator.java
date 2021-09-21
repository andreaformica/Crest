/**
 * 
 */
package hep.crest.data.test.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.Tag;
import hep.crest.data.runinfo.pojo.RunInfo;
import hep.crest.data.security.pojo.CrestFolders;
import hep.crest.swagger.model.FolderDto;
import hep.crest.swagger.model.GlobalTagDto;
import hep.crest.swagger.model.GlobalTagMapDto;
import hep.crest.swagger.model.IovDto;
import hep.crest.swagger.model.PayloadDto;
import hep.crest.swagger.model.RunInfoDto;
import hep.crest.swagger.model.TagDto;
import hep.crest.swagger.model.TagSummaryDto;

/**
 * @author formica
 *
 */
public class DataGenerator {

    public static GlobalTag generateGlobalTag(String name) {
        final Instant now = Instant.now();
        final Date snapshotTime = new Date(now.toEpochMilli());
        final GlobalTag entity = new GlobalTag();
        entity.name(name);
        entity.description("A test global tag "+name);
        entity.release("rel-1");
        entity.scenario("test");
        entity.type('T');
        entity.workflow("none");
        entity.validity(new BigDecimal(0L));
        entity.snapshotTime(snapshotTime);
        return entity;
    }
    
    public static GlobalTagDto generateGlobalTagDto(String name, Date it) {
        final GlobalTagDto entity = new GlobalTagDto();
        Instant inst = Instant.ofEpochMilli(it.getTime());

        entity.name(name);
        entity.description("A test global tag "+name);
        entity.release("rel-1");
        entity.scenario("test");
        entity.type("T");
        entity.workflow("none");
        entity.validity(new BigDecimal(0L));
        entity.snapshotTime(inst.atOffset(ZoneOffset.UTC));
        entity.insertionTime(inst.atOffset(ZoneOffset.UTC));
        entity.setInsertionTimeMilli(10L);
        entity.setSnapshotTimeMilli(10L);
        return entity;
    }

    public static Tag generateTag(String name, String ttype) {
        final Tag entity = new Tag();
        entity.name(name).description("A test tag "+name).endOfValidity(new BigDecimal(-1L))
        .lastValidatedTime(new BigDecimal(-1L))
        .objectType("type")
        .synchronization("synchro")
        .timeType(ttype);
        return entity;
    }
    
    public static TagDto generateTagDto(String name, String ttype) {
        final TagDto entity = new TagDto();
        entity.name(name);
        entity.payloadSpec("sometype");
        entity.description("A test tag "+name);
        entity.endOfValidity(new BigDecimal(-1L));
        entity.lastValidatedTime(new BigDecimal(-1L));
        entity.synchronization("synchro");
        entity.timeType(ttype);
        return entity;
    }

    public static GlobalTagMap generateMapping(GlobalTag gt, Tag at, GlobalTagMapId id) {
        final GlobalTagMap entity = new GlobalTagMap();
        entity.id(id).globalTag(gt).tag(at);
        return entity;
    }
    
    public static GlobalTagMapDto generateMappingDto(String tagName, String gtagName, String record, String label) {
        final GlobalTagMapDto entity = new GlobalTagMapDto();
        entity.tagName(tagName);
        entity.globalTagName(gtagName);
        entity.record(record);
        entity.label(label);
        return entity;
    }

    public static Payload generatePayload(String hash, String objtype) {
        final Payload entity = new Payload();
        entity.hash(hash);
        entity.objectType(objtype);
        entity.version("v1");
        return entity;
    }

    public static IovDto generateIovDto(String hash, String tagname, BigDecimal since) {
        final IovDto dto = new IovDto();
        dto.payloadHash(hash).tagName(tagname).since(since);
        return dto;
    }

    public static Iov generateIov(String hash, String tagname, BigDecimal since) {
        final IovId id = new IovId(tagname,since,new Date());
        final Tag tag = new Tag();
        final Iov entity = new Iov(id,tag,hash);
        return entity;
    }

    public static PayloadDto generatePayloadDto(String hash, String payloaddata, String stinfo, String objtype, Date it) {
        final PayloadDto dto = new PayloadDto();
        final byte[] bindata = payloaddata.getBytes();
        final byte[] binstinfo = stinfo.getBytes();
        Instant inst = Instant.ofEpochMilli(it.getTime());
        dto.insertionTime(inst.atOffset(ZoneOffset.UTC)).data(bindata).hash(hash).objectType(objtype)
                .streamerInfo(binstinfo).version("v1");
        dto.size(bindata.length);
        return dto;
    }

    public static RunInfo generateRunInfo(Date start, Date end, BigDecimal run) {
        final RunInfo entity = new RunInfo();
        entity.setRunNumber(run);
        entity.setEndTime(end);
        entity.setStartTime(start);
        return entity;
    }

    public static RunInfoDto generateRunInfoDto(Date start, Date end, BigDecimal run) {
        final RunInfoDto dto = new RunInfoDto();
        Instant ist = Instant.ofEpochMilli(start.getTime());
        Instant ien = Instant.ofEpochMilli(end.getTime());
        dto.setRunNumber(run);
        dto.setEndTime(ien.atOffset(ZoneOffset.UTC));
        dto.setStartTime(ist.atOffset(ZoneOffset.UTC));
        return dto;
    }

    public static TagSummaryDto generateTagSummaryDto(String name, Long niovs) {
        final TagSummaryDto dto = new TagSummaryDto();
        dto.tagname(name).niovs(niovs);
        return dto;
    }

    public static FolderDto generateFolderDto(String name, String fullpath, String schema) {
        final FolderDto dto = new FolderDto();
        dto.schemaName(schema);
        dto.nodeFullpath(fullpath);
        dto.nodeName(name);
        dto.tagPattern("MY-TEST");
        dto.nodeDescription("Some node");
        dto.groupRole("TEST");
        return dto;
    }

    public static CrestFolders generateFolder(String name, String fullpath, String schema) {
        final CrestFolders entity = new CrestFolders();
        entity.setSchemaName(schema);
        entity.setNodeFullpath(fullpath);
        entity.setNodeName(name);
        entity.setTagPattern(name+"-MY-TEST");
        entity.setNodeDescription("Some node");
        entity.setGroupRole("TEST");
        return entity;
    }

    public static void generatePayloadData(String filename, String content) {
        try {
            final File file = new File(filename);
            final FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("This is ");
            fileWriter.write("a test");
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
