package hep.crest.server.tzero.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public class TzeroDb implements ITzeroDb {

    /**
     * The JdbcTemplate.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @param ds the DataSource
     */
    public TzeroDb(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Long> calibRunList() {
        String sql = "select distinct run "
                + "from ("
                + "select round((SYSDATE - date '1970-01-01')*24*60*60) as curtime, "
                + "    nr.*, nt.id, nt.attempt, nt.state as state2, nt.taskinfo,"
                + "    BITAND(nr.state, 2) as pcal,"
                + "    BITAND(nr.state, 4) as coll,"
                + "    BITAND(nt.state, 2) as task_error"
                + "    from ATLAS_COOL_GLOBAL.NEMOP_RUN nr"
                + "        left join ATLAS_COOL_GLOBAL.NEMOP_TASK nt on nt.run=nr.run"
                + "    WHERE ACTIVE=1 ORDER BY nr.RUN DESC, nt.ttype)"
                + "    WHERE not ((curtime-utime) > (3600*24*7) and coll>0 and pcal>0 and "
                + "task_error>0)"
                + "  AND not((curtime-utime) > (3600*24*31*2) and coll>0)"
                + "  AND state=2 ORDER BY run DESC";
        List<Long> runs = jdbcTemplate.queryForList(sql, Long.class);
        return runs;
    }

}
