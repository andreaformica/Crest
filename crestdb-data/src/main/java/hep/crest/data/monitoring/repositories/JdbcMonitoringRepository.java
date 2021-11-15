/**
 *
 */
package hep.crest.data.monitoring.repositories;

import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Payload;
import hep.crest.swagger.model.PayloadTagInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.Table;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author formica
 */
public class JdbcMonitoringRepository implements IMonitoringRepository {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(JdbcMonitoringRepository.class);

    /**
     * The datasource.
     */
    private DataSource ds;
    /**
     * Default table name.
     */
    private String defaultTablename = null;

    /**
     * @param ds the DataSource
     */
    public JdbcMonitoringRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * @param defaultTablename the String
     * @return
     */
    public void setDefaultTablename(String defaultTablename) {
        if (this.defaultTablename == null) {
            this.defaultTablename = defaultTablename;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * hep.crest.data.monitoring.repositories.IMonitoringRepository#selectTagInfo(
     *java.lang.String)
     */
    @Override
    public List<PayloadTagInfoDto> selectTagInfo(String tagpattern) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        String sql;
        try {
            // Create the sql string using a query defined in a package.
            // This should word on any DB.
            sql = "select iv.tag_name, count(iv.tag_name) as niovs, sum(pl.data_size) as tot_volume, avg(pl"
                  + ".data_size) as avg_volume FROM " + this.payloadTable() + " pl, " + this.iovTable() + " iv "
                  + " WHERE iv.TAG_NAME like ? AND iv.PAYLOAD_HASH=pl.HASH group by iv.TAG_NAME order by iv.TAG_NAME";
            log.debug("Execute query {} using {}", sql, tagpattern);
            return jdbcTemplate.query(sql, new PayloadInfoMapper(), tagpattern);
        }
        catch (final EmptyResultDataAccessException e) {
            // No result, log the error.
            log.error("Cannot find tag information for pattern {}: {}", tagpattern, e);
        }
        return new ArrayList<>();
    }

    /**
     * Provide the correct table names taking into account the schema name.
     *
     * @return String
     */
    protected String payloadTable() {
        final Table ann = Payload.class.getAnnotation(Table.class);
        String tablename = ann.name();
        if (defaultTablename != null) {
            tablename = defaultTablename + "." + tablename;
        }
        log.debug("Generating payload table name as {}", tablename);
        return tablename;
    }

    /**
     * Provide the correct table names taking into account the schema name.
     *
     * @return String
     */
    protected String iovTable() {
        final Table ann = Iov.class.getAnnotation(Table.class);
        String tablename = ann.name();
        if (defaultTablename != null) {
            tablename = defaultTablename + "." + tablename;
        }
        log.debug("Generating iov table name as {}", tablename);
        return tablename;
    }

    /**
     * Procname. Use stored procedures.
     *
     * @return the string
     */
    protected String procname() {
        // Get the package name.
        if (defaultTablename == null || defaultTablename.isEmpty()) {
            return "CREST_TOOLS";
        }
        return defaultTablename + ".CREST_TOOLS";
    }
}
