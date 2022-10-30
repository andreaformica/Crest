package hep.crest.data.config;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.PayloadData;
import hep.crest.data.pojo.PayloadInfoData;
import hep.crest.data.pojo.Tag;
import hep.crest.data.pojo.TagMeta;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Table;

/**
 * Properties for crest.
 * 
 * @author formica
 *
 */
@Slf4j
public class CrestTableNames {

    /**
     * Default table name.
     */
    private String defaultTablename = null;
    /**
     * The payload table.
     */
    private String payloadTableName = null;
    /**
     * The payload data table.
     */
    private String payloadDataTableName = null;
    /**
     * The payload data table.
     */
    private String payloadInfoTableName = null;
    /**
     * The iov table.
     */
    private String iovTableName = null;
    /**
     * The iov table.
     */
    private String tagMetaTableName = null;
    /**
     * The iov table.
     */
    private String tagTableName = null;
    /**
     * The iov table.
     */
    private String globaltagTableName = null;

    /**
     * Default ctor.
     */
    public CrestTableNames() {

    }

    /**
     *
     * @param defaultTablename
     */
    public void setDefaultTablename(String defaultTablename) {
        this.defaultTablename = defaultTablename;
    }

    /**
     *
     * @return the string
     */
    public String getDefaultTablename() {
        return this.defaultTablename;
    }

    /**
     * @param type
     * @return String
     */
    public String tablename(String type) {
        String tablename = null;
        Table ann = null;
        log.info("Getting table name for type {}", type);
        switch (type) {
            case "Iov":
                ann = Iov.class.getAnnotation(Table.class);
                break;
            case "Payload":
                ann = Payload.class.getAnnotation(Table.class);
                break;
            case "PayloadData":
                ann = PayloadData.class.getAnnotation(Table.class);
                break;
            case "PayloadInfo":
                ann = PayloadInfoData.class.getAnnotation(Table.class);
                break;
            case "Tag":
                ann = Tag.class.getAnnotation(Table.class);
                break;
            case "GlobalTag":
                ann = GlobalTag.class.getAnnotation(Table.class);
                break;
            case "TagMeta":
                ann = TagMeta.class.getAnnotation(Table.class);
                break;
            default:
                log.error("Cannot recognize the table type");
                return "";
        }
        tablename = ann.name();
        log.info("From annotation: {}", tablename);
        if (!DatabasePropertyConfigurator.SCHEMA_NAME.isEmpty()) {
            tablename = DatabasePropertyConfigurator.SCHEMA_NAME + "." + tablename;
        }
        else if (this.defaultTablename != null) {
            tablename = this.defaultTablename + "." + tablename;
        }
        log.info("Registered table name: {}", tablename);
        return tablename;
    }

    /**
     *
     * @return the table name.
     */
    public String getPayloadTableName() {
        if (this.payloadTableName == null) {
            this.payloadTableName = tablename("Payload");
        }
        return this.payloadTableName;
    }
    /**
     *
     * @return the table name.
     */
    public String getPayloadDataTableName() {
        if (this.payloadDataTableName == null) {
            this.payloadDataTableName = tablename("PayloadData");
        }
        return this.payloadDataTableName;
    }
    /**
     *
     * @return the table name.
     */
    public String getPayloadInfoDataTableName() {
        if (this.payloadInfoTableName == null) {
            this.payloadInfoTableName = tablename("PayloadInfo");
        }
        return this.payloadInfoTableName;
    }
    /**
     *
     * @return the table name.
     */
    public String getTagTableName() {
        if (this.tagTableName == null) {
            this.tagTableName = tablename("Tag");
        }
        return this.tagTableName;
    }
    /**
     *
     * @return the table name.
     */
    public String getIovTableName() {
        if (this.iovTableName == null) {
            this.iovTableName = tablename("Iov");
        }
        return this.iovTableName;
    }
    /**
     *
     * @return the table name.
     */
    public String getTagMetaTableName() {
        if (this.tagMetaTableName == null) {
            this.tagMetaTableName = tablename("TagMeta");
        }
        return this.tagMetaTableName;
    }
}
