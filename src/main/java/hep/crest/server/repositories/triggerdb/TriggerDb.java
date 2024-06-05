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
     * Get TriggerDB data using the provided hash-URL.
     *
     * @param components
     * @return InputStream
     */
    public InputStream getTriggerDBData(UrlComponents components) {
        String sql = buildSql(components);
        try {
            Long id = components.getId();
            String columnName = getColumnName(components);
            log.debug("Execute query {} using {}", sql, id);
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                Blob blob = rs.getBlob(columnName);
                return blob.getBinaryStream();
            });
        }
        catch (EmptyResultDataAccessException e) {
            // No result, log the error.
            log.error("Cannot find {} for ID {}: {}", components.getFullTable(),
                    components.getId(), e);
            return null;
        }
    }

    /**
     * Get the SQL query.
     * @param components
     * @return String
     */
    protected String buildSql(UrlComponents components) {
        String schema = components.getSchema();
        switch (components.getTable()) {
            case "L1PSK":
                return "select L1PS_DATA from "
                       + schema + "." + components.getFullTable()
                       + " where l1ps_id=?";
            case "HLTMK":
                return "select HMT.HTM_DATA from "
                       + schema + ".SUPER_MASTER_TABLE SMT, "
                       + schema + "." + components.getFullTable() + " HMT"
                       + " where HMT.HTM_ID=SMT.SMT_HLT_MENU_ID and SMT.SMT_ID=?";
            case "L1MK":
                return "select L1MT.L1TM_DATA from "
                       + schema + ".SUPER_MASTER_TABLE SMT, "
                       + schema + "." + components.getFullTable() + " L1MT"
                       + " where L1MT.L1TM_ID=SMT.SMT_L1_MENU_ID and SMT.SMT_ID=?";
            case "HLTPSK":
                return "select HPS_DATA from "
                       + schema + "." + components.getFullTable()
                       + " where HPS_ID=?";
            case "BGK":
                return "select L1BGS_DATA from "
                       + schema + "." + components.getFullTable()
                       + " where L1BGS_ID=?";
            case "MGK":
                return "select HMG.HMG_DATA from "
                       + schema + ".SUPER_MASTER_TABLE SMT, "
                       + schema + "." + components.getFullTable() + " HMG"
                       + " where HMG.HMG_IN_USE=1 and SMT.SMT_HLT_MENU_ID = HMG.HMG_HLT_MENU_ID "
                       + " and SMT.SMT_ID=?";
            case "JOK":
                return "select JO.HJO_DATA from "
                       + schema + ".SUPER_MASTER_TABLE SMT, "
                       + schema + "." + components.getFullTable() + " JO"
                       + " where JO.HJO_ID=SMT.SMT_HLT_JOBOPTIONS_ID "
                       + " and SMT.SMT_ID=?";
            default:
                throw new IllegalArgumentException("Unknown table: " + components.getTable());
        }
    }

    /**
     * Get the column name.
     * @param components
     * @return String
     */
    protected String getColumnName(UrlComponents components) {
        switch (components.getTable()) {
            case "L1PSK":
                return "L1PS_DATA";
            case "HLTMK":
                return "HTM_DATA";
            case "L1MK":
                return "L1TM_DATA";
            case "HLTPSK":
                return "HPS_DATA";
            case "BGK":
                return "L1BGS_DATA";
            case "MGK":
                return "HMG_DATA";
            case "JOK":
                return "HJO_DATA";
            default:
                throw new IllegalArgumentException("Unknown table: " + components.getTable());
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
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }
}
