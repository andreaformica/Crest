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
package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.handlers.PostgresBlobHandler;
import hep.crest.data.repositories.externals.SqlRequests;
import hep.crest.swagger.model.PayloadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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
import java.util.List;

/**
 * An implementation for requests using Postgres database.
 *
 * @author formica
 */
public class PayloadDataPostgresImpl extends AbstractPayloadDataGeneral implements PayloadDataBaseCustom {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PayloadDataPostgresImpl.class);

    /**
     * Create Blob handler for postgres.
     */
    private final PostgresBlobHandler bhandler = new PostgresBlobHandler();

    /**
     * @param ds the DataSource
     */
    public PayloadDataPostgresImpl(DataSource ds) {
        super(ds);
    }

    @Override
    protected InputStream getBlobAsStream(ResultSet rs, String key) {
        try {
            // Use the local getBlob
            byte[] buf = this.getBlob(rs, key);
            return new ByteArrayInputStream(buf);
        }
        catch (SQLException e) {
            log.error("Cannot get stream from byte array: {}", e.getMessage());
        }
        return null;
    }

    @Override
    protected byte[] getBlob(ResultSet rs, String key) throws SQLException {
        byte[] buf = null;
        // In postgres we need the OID to access underlying LOB.
        long oid = rs.getLong(key);
        log.info("Retrieve blob from oid {}", oid);
        try (Connection conn = super.getDs().getConnection()) {
            conn.setAutoCommit(false);
            // Get the LOB and read it in a byte array.
            buf = bhandler.getlargeObj(oid, conn);
        }
        return buf;
    }

    @Override
    protected PayloadDto saveBlobAsBytes(PayloadDto entity) {
        // Set the SQL for insertion of a Payload entity.
        final String tablename = this.tablename();
        final String sql = SqlRequests.getInsertAllQuery(tablename);
        // Here we print the hash used.
        log.debug("Insert Payload {} using JDBCTEMPLATE ", entity.getHash());
        // Prepare the input streams for data and streamerInfo LOBs.
        final InputStream is = new ByteArrayInputStream(entity.getData());
        final InputStream sis = new ByteArrayInputStream(entity.getStreamerInfo());
        // Execute the SQL above.
        execute(is, sis, sql, entity);
        log.debug("Search for stored payload as a verification, use hash {}", entity.getHash());
        // Send back a dto without the data LOB.
        return findMetaInfo(entity.getHash());
    }

    @Override
    protected PayloadDto saveBlobAsStream(PayloadDto entity, InputStream is) {
        // Set the SQL for insertion of a Payload entity.
        final String tablename = this.tablename();
        final String sql = SqlRequests.getInsertAllQuery(tablename);
        // Here we print the hash used.
        log.debug("Insert Payload {} using JDBCTEMPLATE", entity.getHash());
        log.debug("Streamer info {} ", entity.getStreamerInfo());
        // Prepare the input streams for streamerInfo LOB.
        final InputStream sis = new ByteArrayInputStream(entity.getStreamerInfo());
        // The data LOB is in the input argument of the function.
        execute(is, sis, sql, entity);
        // Send back a dto without the data LOB.
        return findMetaInfo(entity.getHash());
    }

    /**
     * @param is     the InputStream
     * @param sis    the InputStream
     * @param sql    the String
     * @param entity the PayloadDto
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    protected void execute(InputStream is, InputStream sis, String sql, PayloadDto entity) {
        Instant now = Instant.now();
        final java.sql.Date inserttime = new java.sql.Date(now.toEpochMilli());
        entity.setInsertionTime(now.atOffset(ZoneOffset.UTC));
        // Get the connection from datasource.
        // Create the prepared statement using the input SQL string.
        try (Connection conn = super.getDs().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            // Create the LOBs
            final long oid = bhandler.writeLargeObjectId(conn, is, entity);
            final long sioid = bhandler.writeLargeObjectId(conn, sis, null);
            // Set the other columns.
            ps.setString(1, entity.getHash());
            ps.setString(2, entity.getObjectType());
            ps.setString(3, entity.getVersion());
            ps.setLong(4, oid);
            ps.setLong(5, sioid);
            ps.setDate(6, inserttime);
            ps.setInt(7, entity.getSize());
            log.info("Dump preparedstatement {} ", ps);
            ps.executeUpdate();
            conn.commit();
        }
        catch (final SQLException e) {
            log.error("Sql exception when storing payload with sql {} : {}", sql, e.getMessage());
        }
        finally {
            try {
                // Close all streams to avoid memory leaks.
                is.close();
                sis.close();
            }
            catch (final IOException e) {
                log.error("Error in closing streams...potential leak: {}", e.getMessage());
            }
        }
    }

    /**
     * The method does not access blob data.
     *
     * @param id           the String
     * @param streamerInfo the String
     * @return number of updated rows.
     */
    @Override
    @Transactional
    public int updateMetaInfo(String id, String streamerInfo) {
        log.info("Update payload streamer info {} using JDBCTEMPLATE (postgresql implementation)", id);
        // Get the connection from datasource.
        try (Connection conn = super.getDs().getConnection()) {
            conn.setAutoCommit(false);
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(super.getDs());
            final String tablename = this.tablename();
            final String sqlget = SqlRequests.getStreamerInfoQuery(tablename);
            // Retrieve oid to replace the content of the file.
            // This is done only for streamerInfo LOB.
            List<Long> oidlist = jdbcTemplate.query(sqlget,
                    (rs, row) -> rs.getLong(1),
                    id);
            // Create an inputStream for the string in the input argument.
            final InputStream sis = new ByteArrayInputStream(streamerInfo.getBytes(StandardCharsets.UTF_8));
            // If the OID for the streamerInfo LOB is present, then update it with the new content.
            if (!oidlist.isEmpty()) {
                Long oid = oidlist.get(0);
                bhandler.updateLargeObjectId(conn, sis, oid);
                return 1;
            }
        }
        catch (final DataAccessException | SQLException e) {
            log.error("Cannot update streamer info payload with data for hash {}: {}", id, e);
        }
        return 0;
    }

    @Override
    @Transactional
    public void delete(String id) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(super.getDs());
        final String tablename = this.tablename();
        // Create SQL for delete request.
        final String sql = SqlRequests.getDeleteQuery(tablename);
        log.info("Remove payload with hash {} using JDBC", id);
        // Remove the OIDs.
        this.deleteOids(id);
        jdbcTemplate.update(sql, id);
        log.debug("Entity removal done...");
    }

    /**
     * Delete the underlying files provided the hash.
     * It applies to Data and StreamerInfo.
     *
     * @param hash the payload hash.
     */
    protected void deleteOids(String hash) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(super.getDs());
        final String tablename = this.tablename();
        final String sqlget = SqlRequests.getFindDataQuery(tablename);
        // Get the list of OIDs to remove for the HASH in input.
        List<Long> oidlist = jdbcTemplate.query(sqlget,
                (rs, row) -> rs.getLong(1),
                hash);
        if (!oidlist.isEmpty()) {
            Long oid = oidlist.get(0);
            // This method remove the underlying LOB in postgresql.
            jdbcTemplate.execute("select lo_unlink(" + oid + ")");
        }
        final String sqlmetaget = SqlRequests.getStreamerInfoQuery(tablename);
        oidlist = jdbcTemplate.query(sqlmetaget,
                (rs, row) -> rs.getLong(1),
                hash);
        if (!oidlist.isEmpty()) {
            Long oid = oidlist.get(0);
            // This method remove the underlying LOB in postgresql.
            jdbcTemplate.execute("select lo_unlink(" + oid + ")");
        }
    }
}
