package hep.crest.data.repositories;

import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.exceptions.CdbServiceException;
import hep.crest.data.handlers.PayloadHandler;
import hep.crest.data.pojo.Payload;
import hep.crest.data.repositories.externals.SqlRequests;
import hep.crest.swagger.model.PayloadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import javax.sql.DataSource;
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
 * General base class for repository implementations.
 *
 * @author formica
 */
public abstract class AbstractPayloadDataGeneral extends DataGeneral implements PayloadDataBaseCustom {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractPayloadDataGeneral.class);

    /**
     * @param ds the DataSource
     */
    protected AbstractPayloadDataGeneral(DataSource ds) {
        super(ds);
        super.setAnn(Payload.class.getAnnotation(Table.class));
    }

    /**
     * @param id the String
     * @return String
     */
    @Override
    public String exists(String id) {
        log.info("Find payload {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            final String tablename = this.tablename();
            // Check if payload with a given hash exists.
            final String sql = SqlRequests.getExistsHashQuery(tablename);
            return jdbcTemplate.queryForObject(sql, (rs, num) -> rs.getString("HASH"), new Object[]{id});
        }
        catch (final DataAccessException e) {
            log.warn("Hash {} does not exists, returning null", id);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.phycdb.svc.repositories.PayloadDataBaseCustom#save(hep.phycdb.data.pojo.
     * Payload)
     */
    @Override
    @Transactional
    public PayloadDto save(PayloadDto entity) {
        PayloadDto savedentity = null;
        try {
            log.info("Saved payload {} of size {}", entity.getHash(), entity.getSize());
            savedentity = this.saveBlobAsBytes(entity);
        }
        catch (final RuntimeException e) {
            log.error("Error in save paylod dto : {}", e.getMessage());
            throw new CdbSQLException(e);
        }
        return savedentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.data.repositories.PayloadDataBaseCustom#save(hep.crest.swagger.
     * model.PayloadDto, java.io.InputStream)
     */
    @Override
    @Transactional
    public PayloadDto save(PayloadDto entity, InputStream is) {
        PayloadDto savedentity = null;
        try {
            log.info("Saved payload {} from input stream of size {}", entity.getHash(), entity.getSize());
            savedentity = this.saveBlobAsStream(entity, is);
        }
        catch (final RuntimeException e) {
            log.error("Exception during payload dto insertion: {}", e.getMessage());
            throw new CdbSQLException(e);
        }
        return savedentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.data.repositories.PayloadDataBaseCustom#delete(java.lang.String)
     */
    @Override
    @Transactional
    public void delete(String id) {
        final String tablename = this.tablename();

        final String sql = SqlRequests.getDeleteQuery(tablename);
        log.info("Remove payload with hash {} using JDBCTEMPLATE", id);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
        jdbcTemplate.update(sql, id);
        log.debug("Entity removal done...");
    }

    /* (non-Javadoc)
     * @see hep.crest.data.repositories.PayloadDataBaseCustom#find(java.lang.String)
     */
    @Override
    @Transactional
    public PayloadDto find(String id) {
        log.info("Find payload {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            final String tablename = this.tablename();

            final String sql = SqlRequests.getFindQuery(tablename);

            // Be careful, this seems not to work with Postgres: probably getBlob loads an
            // OID and not the byte[]
            // Temporarely, try to create a postgresql implementation of this class.
            return jdbcTemplate.queryForObject(sql, (rs, num) -> {
                final PayloadDto entity = new PayloadDto();
                Instant inst = Instant.ofEpochMilli(rs.getTimestamp("INSERTION_TIME").getTime());
                entity.setInsertionTime(inst.atOffset(ZoneOffset.UTC));
                entity.setHash(rs.getString("HASH"));
                entity.setObjectType(rs.getString("OBJECT_TYPE"));
                entity.setVersion(rs.getString("VERSION"));
                entity.setData(getBlob(rs, "DATA"));
                entity.setStreamerInfo(getBlob(rs, "STREAMER_INFO"));
                entity.setSize(rs.getInt("DATA_SIZE"));
                return entity;
            }, new Object[]{id});
        }
        catch (final DataAccessException e) {
            log.warn("Could not find entry for hash {}: {}", id, e);
            throw new CdbNotFoundException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.data.repositories.PayloadDataBaseCustom#findMetaInfo(java.lang.
     * String)
     */
    @Override
    @Transactional
    public PayloadDto findMetaInfo(String id) {
        log.info("Find payload meta info {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            final String tablename = this.tablename();
            final String sql = SqlRequests.getFindMetaQuery(tablename);

            return jdbcTemplate.queryForObject(sql, (rs, num) -> {
                final PayloadDto entity = new PayloadDto();
                Instant inst = Instant.ofEpochMilli(rs.getTimestamp("INSERTION_TIME").getTime());
                entity.setInsertionTime(inst.atOffset(ZoneOffset.UTC));
                entity.setHash(rs.getString("HASH"));
                entity.setObjectType(rs.getString("OBJECT_TYPE"));
                entity.setVersion(rs.getString("VERSION"));
                entity.setStreamerInfo(getBlob(rs, "STREAMER_INFO"));
                entity.setSize(rs.getInt("DATA_SIZE"));
                return entity;
            }, new Object[]{id});
        }
        catch (final DataAccessException e) {
            log.warn("Could not find meta info entry for hash {}: {}", id, e.getMessage());
            throw new CdbNotFoundException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.data.repositories.PayloadDataBaseCustom#findData(java.lang.String)
     */
    @Override
    @Transactional
    public InputStream findData(String id) {
        log.info("Find payload data {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            final String tablename = this.tablename();

            final String sql = SqlRequests.getFindDataQuery(tablename);
            return jdbcTemplate.queryForObject(sql,
                    (rs, num) -> getBlobAsStream(rs, "DATA"), new Object[]{id});
        }
        catch (final DataAccessException e) {
            log.error("Cannot find payload with data for hash {}: {}", id, e);
            throw new CdbNotFoundException(e);
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
    public int updateMetaInfo(String id, String streamerInfo) {
        log.info("Update payload streamer info {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            final String tablename = this.tablename();

            final String sql = SqlRequests.getUpdateMetaQuery(tablename);
            return jdbcTemplate.update(sql, streamerInfo.getBytes(StandardCharsets.UTF_8), id);
        }
        catch (final DataAccessException e) {
            log.error("Cannot update streamer info payload with data for hash {}: {}", id, e);
            throw new CdbNotFoundException(e);
        }
    }

    /**
     * @param rs
     * @param key
     * @return byte[]
     * @throws SQLException
     */
    protected abstract byte[] getBlob(ResultSet rs, String key) throws SQLException;

    /**
     * Transform the byte array from the Blob into a binary stream.
     *
     * @param rs
     * @param key
     * @return InputStream
     * @throws SQLException
     */
    protected abstract InputStream getBlobAsStream(ResultSet rs, String key) throws SQLException;

    /**
     * @param is     the InputStream
     * @param sql    the String
     * @param entity the PayloadDto
     * @return
     * @throws CdbServiceException If an Exception occurred
     */
    protected void execute(InputStream is, String sql, PayloadDto entity) {

        Instant now = Instant.now();
        final java.sql.Date inserttime = new java.sql.Date(now.toEpochMilli());
        entity.setInsertionTime(now.atOffset(ZoneOffset.UTC));

        if (is != null) {
            final byte[] blob = PayloadHandler.getBytesFromInputStream(is);
            if (blob != null) {
                entity.setSize(blob.length);
                entity.setData(blob);
                log.debug("Read data blob of length {} and streamer info {}", blob.length,
                        entity.getStreamerInfo().length);
            }
        }
        try (Connection conn = getDs().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, entity.getHash());
            ps.setString(2, entity.getObjectType());
            ps.setString(3, entity.getVersion());
            ps.setBytes(4, entity.getData());
            ps.setBytes(5, entity.getStreamerInfo());
            ps.setDate(6, inserttime);
            ps.setInt(7, entity.getSize());
            log.debug("Dump preparedstatement {} using sql {} and arguments : {} {} {} {}", ps, sql,
                    entity.getHash(), entity.getObjectType(), entity.getVersion(),
                    entity.getInsertionTime());
            ps.execute();
        }
        catch (final SQLException e) {
            log.error("Sql exception when storing payload with sql {} : {}", sql, e.getMessage());
            throw new CdbSQLException(e);
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException e) {
                log.error("Error in closing streams...potential leak: {}", e.getMessage());
            }
        }
    }

    /**
     * @param entity the PaloadDto
     * @param is     the InputStream
     * @return PayloadDto
     * @throws CdbServiceException If an Exception occurred
     */
    protected PayloadDto saveBlobAsStream(PayloadDto entity, InputStream is) {
        final String tablename = this.tablename();
        // Save blob from stream
        final String sql = SqlRequests.getInsertAllQuery(tablename);
        log.debug("Insert Payload with hash {} using saveBlobAsStream", entity.getHash());
        execute(is, sql, entity);
        return entity;
    }

    /**
     * @param entity the PayloadDto
     * @return PayloadDto
     * @throws CdbServiceException If an Exception occurred
     */
    protected PayloadDto saveBlobAsBytes(PayloadDto entity) {
        final String tablename = this.tablename();
        // Save blob from byte array
        final String sql = SqlRequests.getInsertAllQuery(tablename);
        log.debug("Insert Payload with hash {} using saveBlobAsBytes", entity.getHash());
        execute(null, sql, entity);
        return entity;
    }

    /**
     * Placeholder method for NULL payload storage.
     *
     * @return Payload
     */
    public Payload saveNull() {
        log.warn("Method not implemented");
        return null;
    }

}
