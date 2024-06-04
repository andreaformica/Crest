package hep.crest.server.repositories.triggerdb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Blob;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TriggerDb implements ITriggerDb {

    /**
     * The JdbcTemplate.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @param ds the DataSource
     */
    public TriggerDb(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    /**
     * Get L1 prescale set.
     *
     * @param id
     * @return InputStream
     */
    public InputStream getL1PrescaleSet(Long id) {
        String sql = "select L1PS_DATA from ATLAS_CONF_TRIGGER_RUN3.l1_prescale_set where "
                     + "l1ps_id=?";
        try {
            log.debug("Execute query {} using {}", sql, id);
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                Blob blob = rs.getBlob("L1PS_DATA");
                return blob.getBinaryStream();
            });
        }
        catch (EmptyResultDataAccessException e) {
            // No result, log the error.
            log.error("Cannot find L1 prescale sets for ID {}: {}", id, e);
            return null;
        }
    }

    /**
     * Parse the URL for trigger://schema/pkt/id.
     *
     * @param url
     * @return UrlComponents
     */
    public UrlComponents parseUrl(String url) {
        String regex = "^triggerdb://([^/]+)/([^/]+)/([^/]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.matches()) {
            String schema = matcher.group(1);
            String table = matcher.group(2);
            String id = matcher.group(3);
            return new UrlComponents(schema, table, Long.valueOf(id));
        }
        else {
            return null; // URL doesn't match the expected pattern
        }
    }
}
