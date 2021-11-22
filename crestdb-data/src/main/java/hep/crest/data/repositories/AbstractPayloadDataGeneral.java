package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.handlers.PayloadHandler;
import hep.crest.data.pojo.Payload;
import hep.crest.data.repositories.externals.SqlRequests;
import hep.crest.swagger.model.IovPayloadDto;
import hep.crest.swagger.model.PayloadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
     * The decoder.
     */
    private CharsetDecoder decoder = StandardCharsets.US_ASCII.newDecoder();

    /**
     * @param ds the DataSource
     */
    protected AbstractPayloadDataGeneral(DataSource ds) {
        super(ds);
    }

    /**
     * @param id the String
     * @return String
     */
    @Override
    public String exists(String id) {
        log.info("Find payload {} using JDBCTEMPLATE", id);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
        try {
            // Check if payload with a given hash exists.
            final String sql = SqlRequests.getExistsHashQuery(getCrestTableNames().getPayloadTableName());
            return jdbcTemplate.queryForObject(sql, (rs, num) -> rs.getString("HASH"), id);
        }
        catch (final DataAccessException e) {
            String msg = "Error checking existence of hash " + id;
            log.warn(msg + ": {}", e.getMessage());
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
    public PayloadDto save(PayloadDto entity) {
        PayloadDto savedentity = null;
        try {
            log.info("Saving payload {} of size {} using saveBlobAsBytes", entity.getHash(), entity.getSize());
            savedentity = this.saveBlobAsBytes(entity);
        }
        catch (final RuntimeException e) {
            String msg = "Error saving payload dto " + entity;
            log.warn(msg);
            throw new CdbSQLException(msg, e);
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
    public PayloadDto save(PayloadDto entity, InputStream is) {
        PayloadDto savedentity = null;
        try {
            log.info("Saved payload {} of size {} using saveBlobAsStream", entity.getHash(), entity.getSize());
            savedentity = this.saveBlobAsStream(entity, is);
        }
        catch (final RuntimeException e) {
            String msg = "Error saving payload dto using input stream " + entity;
            log.warn(msg);
            throw new CdbSQLException(msg, e);
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
        final String sql = SqlRequests.getDeleteQuery(getCrestTableNames().getPayloadTableName());
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
            final String sql = SqlRequests.getFindQuery(getCrestTableNames().getPayloadTableName());

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
            }, id);
        }
        catch (final DataAccessException e) {
            String msg = "Could not find entry for hash " + id;
            log.warn(msg);
            throw new CdbSQLException(msg, e);
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
            final String sql = SqlRequests.getFindMetaQuery(getCrestTableNames().getPayloadTableName());

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
            }, id);
        }
        catch (final DataAccessException e) {
            String msg = "Could not find meta info entry for hash " + id;
            log.warn(msg);
            throw new CdbSQLException(msg, e);
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
            final String sql = SqlRequests.getFindDataQuery(getCrestTableNames().getPayloadTableName());
            return jdbcTemplate.queryForObject(sql,
                    (rs, num) -> getBlobAsStream(rs, "DATA"), id
            );
        }
        catch (final DataAccessException e) {
            String msg = "Could not find data entry for hash " + id;
            log.warn(msg);
            throw new CdbSQLException(msg, e);
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
        log.info("Update payload streamer info {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            final String sql = SqlRequests.getUpdateMetaQuery(getCrestTableNames().getPayloadTableName());
            return jdbcTemplate.update(sql, streamerInfo.getBytes(StandardCharsets.UTF_8), id);
        }
        catch (final DataAccessException e) {
            String msg = "Could not update streamerInfo entry for hash " + id;
            log.warn(msg);
            throw new CdbSQLException(msg, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.data.repositories.IovGroupsCustom#getRangeIovPayloadInfo(java.lang
     * .String, java.math.BigDecimal, java.math.BigDecimal, java.util.Date)
     */
    @Override
    public List<IovPayloadDto> getRangeIovPayloadInfo(String name, BigDecimal since,
                                                      BigDecimal until, Date snapshot) {
        log.debug("Select Iov and Payload meta info for tag  {} using JDBCTEMPLATE", name);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
        // Get sql query.
        final String sql = SqlRequests.getRangeIovPayloadQuery(getCrestTableNames().getIovTableName(),
                getCrestTableNames().getPayloadTableName());
        // Execute request.
        return jdbcTemplate.query(sql, (rs, num) -> {
            final IovPayloadDto entity = new IovPayloadDto();
            Instant inst = Instant.ofEpochMilli(rs.getTimestamp("INSERTION_TIME").getTime());
            entity.setSince(rs.getBigDecimal("SINCE"));
            entity.setInsertionTime(inst.atOffset(ZoneOffset.UTC));
            entity.setPayloadHash(rs.getString("PAYLOAD_HASH"));
            entity.setVersion(rs.getString("VERSION"));
            entity.setObjectType(rs.getString("OBJECT_TYPE"));
            entity.setSize(rs.getInt("DATA_SIZE"));
            entity.setStreamerInfo(getStringFromBuf(getBlob(rs, "STREAMER_INFO")));
            log.debug("create entity {}", entity);
            return entity;
        }, name, name, since, snapshot, until, snapshot);
    }

    /**
     * @param rs  the result set.
     * @param key the key.
     * @return byte[]
     * @throws SQLException if an Sql exception occurs.
     */
    protected abstract byte[] getBlob(ResultSet rs, String key) throws SQLException;

    /**
     * Transform the byte array from the Blob into a binary stream.
     *
     * @param rs  the result set.
     * @param key the key.
     * @return InputStream
     * @throws SQLException If an Sql exception occurs.
     */
    protected abstract InputStream getBlobAsStream(ResultSet rs, String key) throws SQLException;

    /**
     * @param is     the InputStream
     * @param sql    the String
     * @param entity the PayloadDto
     * @throws AbstractCdbServiceException If an Exception occurred
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
            throw new CdbSQLException("Cannot store payload", e);
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
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    protected PayloadDto saveBlobAsStream(PayloadDto entity, InputStream is) {
        // Save blob from stream
        final String sql = SqlRequests.getInsertAllQuery(getCrestTableNames().getPayloadTableName());
        log.debug("Insert Payload with hash {} using saveBlobAsStream", entity.getHash());
        execute(is, sql, entity);
        return entity;
    }

    /**
     * @param entity the PayloadDto
     * @return PayloadDto
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    protected PayloadDto saveBlobAsBytes(PayloadDto entity) {
        // Save blob from byte array
        final String sql = SqlRequests.getInsertAllQuery(getCrestTableNames().getPayloadTableName());
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

    /**
     * @param buf
     * @return the String.
     */
    protected String getStringFromBuf(byte[] buf) {
        decoder.onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        byte[] streaminfoByteArr = buf.clone();
        try {
            // Use decoder to read the byte array into a string.
            return decoder.decode(ByteBuffer.wrap(streaminfoByteArr))
                    .toString();
        }
        catch (CharacterCodingException e) {
            // If there are character encoding problems, then use a base64 encoder.
            log.warn("Cannot decode as String with charset US_ASCII, use base64: {}", e.getMessage());
            return Base64.getEncoder().encodeToString(streaminfoByteArr);
        }
    }

}
