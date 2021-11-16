package hep.crest.data.config;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Payload;
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
     * @param type
     * @return String
     */
    public String tablename(String type) {
        String tablename = null;
        Table ann = null;
        switch (type) {
            case "Iov":
                ann = Iov.class.getAnnotation(Table.class);
                break;
            case "Payload":
                ann = Payload.class.getAnnotation(Table.class);
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
        if (!DatabasePropertyConfigurator.SCHEMA_NAME.isEmpty()) {
            tablename = DatabasePropertyConfigurator.SCHEMA_NAME + "." + tablename;
        }
        else if (this.defaultTablename != null) {
            tablename = this.defaultTablename + "." + tablename;
        }
        return tablename;
    }

}
