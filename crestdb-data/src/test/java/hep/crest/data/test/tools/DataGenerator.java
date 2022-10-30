/**
 *
 */
package hep.crest.data.test.tools;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.Tag;
import hep.crest.data.runinfo.pojo.RunLumiInfo;
import hep.crest.data.security.pojo.CrestFolders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

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
        entity.description("A test global tag " + name);
        entity.release("rel-1");
        entity.scenario("test");
        entity.type('T');
        entity.workflow("none");
        entity.validity(new BigDecimal(0L));
        entity.snapshotTime(snapshotTime);
        return entity;
    }

    public static Tag generateTag(String name, String ttype) {
        final Tag entity = new Tag();
        entity.name(name).description("A test tag " + name)
                .endOfValidity(BigInteger.valueOf(-1L))
                .lastValidatedTime(BigInteger.valueOf(-1L))
                .objectType("type")
                .synchronization("synchro")
                .timeType(ttype);
        return entity;
    }

    public static GlobalTagMap generateMapping(GlobalTag gt, Tag at, GlobalTagMapId id) {
        final GlobalTagMap entity = new GlobalTagMap();
        entity.id(id).globalTag(gt).tag(at);
        return entity;
    }

    public static Payload generatePayload(String hash, String objtype) {
        final Payload entity = new Payload();
        entity.hash(hash);
        entity.objectType(objtype);
        entity.version("v1");
        return entity;
    }

    public static Iov generateIov(String hash, String tagname, BigInteger since) {
        final IovId id = new IovId().tagName(tagname).since(since).insertionTime(new Date());
        final Tag tag = new Tag().name(tagname);
        final Iov entity = new Iov().id(id).tag(tag).payloadHash(hash);
        return entity;
    }

    public static RunLumiInfo generateRunLumiInfo(BigInteger since, BigInteger run, BigInteger lb) {
        final RunLumiInfo entity = new RunLumiInfo();
        entity.runNumber(run);
        entity.endtime(BigInteger.valueOf(99L));
        entity.starttime(BigInteger.valueOf(1L));
        entity.lb(lb);
        entity.since(since);
        return entity;
    }


    public static CrestFolders generateFolder(String name, String fullpath, String schema) {
        final CrestFolders entity = new CrestFolders();
        entity.schemaName(schema);
        entity.nodeFullpath(fullpath);
        entity.nodeName(name);
        entity.tagPattern(name + "-MY-TEST");
        entity.nodeDescription("Some node");
        entity.groupRole("TEST");
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
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
