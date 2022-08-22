/**
 * This file is part of PhysCondDB.
 * <p>
 * PhysCondDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * PhysCondDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with PhysCondDB.  If not, see <http://www.gnu.org/licenses/>.
 **/
package hep.crest.server.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.repositories.externals.TagMetaRequests;
import hep.crest.server.converters.PostgresBlobHandler;
import hep.crest.server.swagger.model.TagMetaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * @author formica
 */
public class TagMetaPostgresImpl extends TagMetaGeneral implements TagMetaDataBaseCustom {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Create Blob handler for postgres.
     */
    private PostgresBlobHandler bhandler = new PostgresBlobHandler();

    /**
     * @param ds the DataSource
     */
    public TagMetaPostgresImpl(DataSource ds) {
        super(ds);
    }


    @Override
    protected String getBlob(ResultSet rs, String key) throws SQLException {
        byte[] buf = null;
        Long oid = rs.getLong(key);
        log.info("Retrieve blob from oid {}", oid);
        try (Connection conn = super.getDs().getConnection();) {
            conn.setAutoCommit(false);
            buf = bhandler.getlargeObj(oid, conn);
        }
        return new String(buf);
    }

    /**
     * @param tis    the InputStream
     * @param sql    the String
     * @param entity the TagMetaDto
     * @return
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    @Override
    protected void execute(InputStream tis, String sql, TagMetaDto entity) {
        Instant now = Instant.now();
        final java.sql.Date inserttime = new java.sql.Date(now.toEpochMilli());
        entity.setInsertionTime(now.atOffset(ZoneOffset.UTC));

        try (Connection conn = super.getDs().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            conn.setAutoCommit(false);
            final long tid = bhandler.writeLargeObjectId(conn, tis, null);
            ps.setString(1, entity.getDescription());
            ps.setInt(2, entity.getChansize());
            ps.setInt(3, entity.getColsize());
            ps.setLong(4, tid);
            ps.setDate(5, inserttime);
            ps.setString(6, entity.getTagName());
            log.debug("Dump preparedstatement {} ", ps);
            ps.executeUpdate();
            conn.commit();
        }
        catch (final SQLException e) {
            log.error("Sql exception when storing payload with sql {} : {}", sql, e.getMessage());
        }
        finally {
            try {
                tis.close();
            }
            catch (final IOException e) {
                log.error("Error in closing streams...potential leak");
            }
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(super.getDs());
        final String sqlget = TagMetaRequests.getFindTagInfoQuery(getCrestTableNames().getTagMetaTableName());
        final String sql = TagMetaRequests.getDeleteQuery(getCrestTableNames().getTagMetaTableName());
        log.info("Remove payload with hash {} using JDBC", id);
        Long oid = jdbcTemplate.queryForObject(sqlget,
                (rs, row) -> rs.getLong(1),
                id);
        jdbcTemplate.execute("select lo_unlink(" + oid + ")");
        jdbcTemplate.update(sql, id);
        log.debug("Entity removal done...");
    }


    @Override
    protected TagMetaDto saveBlobAsBytes(TagMetaDto entity) {
        final String sql = TagMetaRequests.getInsertAllQuery(getCrestTableNames().getTagMetaTableName());
        log.debug("Insert Tag meta {} using JDBCTEMPLATE ", entity.getTagName());
        final InputStream is = new ByteArrayInputStream(entity.getTagInfo().getBytes(StandardCharsets.UTF_8));
        execute(is, sql, entity);
        log.debug("Search for stored tag meta as a verification, use tag {}", entity.getTagName());
        return findMetaInfo(entity.getTagName());
    }


    @Override
    protected TagMetaDto updateAsBytes(TagMetaDto entity) {
        final String sql = TagMetaRequests.getUpdateQuery(getCrestTableNames().getTagMetaTableName());
        log.debug("Update Tag meta {} using JDBCTEMPLATE ", entity.getTagName());
        final InputStream is = new ByteArrayInputStream(entity.getTagInfo().getBytes(StandardCharsets.UTF_8));
        execute(is, sql, entity);
        log.debug("Search for stored tag meta as a verification, use tag {}", entity.getTagName());
        return findMetaInfo(entity.getTagName());
    }
}
