/**
 *
 */
package hep.crest.data.monitoring.repositories;

import hep.crest.data.repositories.DataGeneral;
import hep.crest.swagger.model.PayloadTagInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author formica
 */
public class JdbcMonitoringRepository extends DataGeneral implements IMonitoringRepository {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(JdbcMonitoringRepository.class);

    /**
     * @param ds the DataSource
     */
    public JdbcMonitoringRepository(DataSource ds) {
        super(ds);
    }

    /*
     * (non-Javadoc)
     * @see
     * hep.crest.data.monitoring.repositories.IMonitoringRepository#selectTagInfo(
     *java.lang.String)
     */
    @Override
    public List<PayloadTagInfoDto> selectTagInfo(String tagpattern) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
        String sql;
        try {
            // Create the sql string using a query defined in a package.
            // This should word on any DB.
            sql = "select iv.tag_name, count(iv.tag_name) as niovs, sum(pl.data_size) as tot_volume, avg(pl"
                  + ".data_size) as avg_volume FROM " + getCrestTableNames().getPayloadTableName() + " pl, "
                  + getCrestTableNames().getIovTableName() + " iv "
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
     * Procname. Use stored procedures.
     *
     * @return the string
     */
    protected String procname() {
        // Get the package name.
        if (getCrestTableNames().getDefaultTablename() == null
            || getCrestTableNames().getDefaultTablename().isEmpty()) {
            return "CREST_TOOLS";
        }
        return getCrestTableNames().getDefaultTablename() + ".CREST_TOOLS";
    }
}
