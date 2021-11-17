package hep.crest.data.repositories;

import hep.crest.data.config.CrestTableNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

@Slf4j
public class DataGeneral {
    /**
     * The Data Source.
     */
    private final DataSource ds;
    /**
     * The upload directory for files.
     */
    @Value("${crest.upload.dir:/tmp}")
    private String serverUploadLocationFolder;
    /**
     * Default table name.
     */
    private String defaultTablename = null;

    /**
     * Create the utility class for table names.
     */
    private CrestTableNames crestTableNames = new CrestTableNames();

    /**
     * @param ds
     */
    public DataGeneral(DataSource ds) {
        super();
        this.ds = ds;
    }

    /**
     * @param defaultTablename the String
     * @return
     */
    public void setDefaultTablename(String defaultTablename) {
        log.info("Setting default table name to {}", defaultTablename);
        if (this.defaultTablename == null) {
            this.defaultTablename = defaultTablename;
        }
        crestTableNames.setDefaultTablename(this.defaultTablename);
    }

    /**
     * @param type the repository type.
     * @return String
     */
    protected String tablename(String type) {
        return crestTableNames.tablename(type);
    }

    /**
     * @return DataSource
     */
    protected DataSource getDs() {
        return ds;
    }

    /**
     * @return the serverUploadLocationFolder
     */
    protected String getServerUploadLocationFolder() {
        return serverUploadLocationFolder;
    }

    /**
     * @param serverUploadLocationFolder the serverUploadLocationFolder to set
     */
    protected void setServerUploadLocationFolder(String serverUploadLocationFolder) {
        this.serverUploadLocationFolder = serverUploadLocationFolder;
    }

    /**
     * Get the default table name.
     *
     * @return String
     */
    public String getDefaultTablename() {
        return defaultTablename;
    }
}
