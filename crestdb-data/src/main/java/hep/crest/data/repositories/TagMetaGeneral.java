package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.handlers.PayloadHandler;
import hep.crest.data.repositories.externals.TagMetaRequests;
import hep.crest.swagger.model.TagMetaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.InputStream;
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
public abstract class TagMetaGeneral extends DataGeneral implements TagMetaDataBaseCustom {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @param ds the DataSource
     */
    protected TagMetaGeneral(DataSource ds) {
        super(ds);
    }

    @Override
    @Transactional
    public TagMetaDto save(TagMetaDto entity) {
        TagMetaDto savedentity = null;
        try {
            savedentity = this.saveBlobAsBytes(entity);
        }
        catch (final Exception e) {
            log.error("Error in save paylod dto : {}", e.getMessage());
        }
        return savedentity;
    }

    @Override
    @Transactional
    public TagMetaDto update(TagMetaDto entity) {
        TagMetaDto savedentity = null;
        try {
            savedentity = this.updateAsBytes(entity);
        }
        catch (final Exception e) {
            log.error("Error in save paylod dto : {}", e.getMessage());
        }
        return savedentity;
    }

    @Override
    @Transactional
    public void delete(String id) {
        // Get the SQL.
        final String sql = TagMetaRequests.getDeleteQuery(getTagMetaTablename());
        log.debug("Remove tag meta with tag name {} using JDBCTEMPLATE", id);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
        try {
            jdbcTemplate.update(sql, id);
            log.debug("Entity removal done...");
        }
        catch (ConstraintViolationException e) {
            log.error("Cannot execute sql statement: {}", sql);
            throw new CdbSQLException("Cannot remove tag meta for " + id + ": " + e.getMessage());
        }
    }

    @Override
    public TagMetaDto find(String id) {
        log.debug("Find tag meta {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            // Get the SQL.
            final String sql = TagMetaRequests.getFindQuery(getTagMetaTablename());
            log.debug("Use sql request {}", sql);
            // Be careful, this seems not to work with Postgres: probably getBlob loads an
            // OID and not the byte[]
            // Temporarely, try to create a postgresql implementation of this class.
            return jdbcTemplate.queryForObject(sql, (rs, num) -> {
                final TagMetaDto entity = new TagMetaDto();
                Instant inst = Instant.ofEpochMilli(rs.getTimestamp("INSERTION_TIME").getTime());
                entity.setTagName(rs.getString("TAG_NAME"));
                entity.setDescription(rs.getString("DESCRIPTION"));
                entity.setChansize(rs.getInt("CHANNEL_SIZE"));
                entity.setColsize(rs.getInt("COLUMN_SIZE"));
                entity.setInsertionTime(inst.atOffset(ZoneOffset.UTC));
                entity.setTagInfo(getBlob(rs, "TAG_INFO"));
                return entity;
            }, id);
        }
        catch (final Exception e) {
            log.warn("Could not find entry for tag name {}", id);
            throw new CdbNotFoundException(id);
        }
    }

    @Override
    public TagMetaDto findMetaInfo(String id) {
        log.debug("Find tag meta info {} using JDBCTEMPLATE", id);
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDs());
            final String sql = TagMetaRequests.getFindMetaQuery(getTagMetaTablename());

            return jdbcTemplate.queryForObject(sql, (rs, num) -> {
                final TagMetaDto entity = new TagMetaDto();
                Instant inst = Instant.ofEpochMilli(rs.getTimestamp("INSERTION_TIME").getTime());
                entity.setTagName(rs.getString("TAG_NAME"));
                entity.setDescription(rs.getString("DESCRIPTION"));
                entity.setChansize(rs.getInt("CHANNEL_SIZE"));
                entity.setColsize(rs.getInt("COLUMN_SIZE"));
                entity.setInsertionTime(inst.atOffset(ZoneOffset.UTC));
                return entity;
            }, id);
        }
        catch (final Exception e) {
            log.warn("Could not find entry for tag {}", id);
            throw new CdbNotFoundException(id);
        }
    }

    /**
     * @param is     the InputStream
     * @param sql    the String
     * @param entity the TagMetaDto
     * @return
     * @throws AbstractCdbServiceException If an Exception occurred
     */
    protected void execute(InputStream is, String sql, TagMetaDto entity) {

        Instant now = Instant.now();
        final java.sql.Date inserttime = new java.sql.Date(now.toEpochMilli());
        entity.setInsertionTime(now.atOffset(ZoneOffset.UTC));

        if (is != null) {
            final byte[] blob = PayloadHandler.getBytesFromInputStream(is);
            if (blob != null) {
                entity.setTagInfo(new String(blob));
                log.debug("Read channel info blob of length {} ", blob.length);
            }
        }

        try (Connection conn = getDs().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, entity.getDescription());
            ps.setInt(2, entity.getChansize());
            ps.setInt(3, entity.getColsize());
            ps.setBytes(4, entity.getTagInfo().getBytes());
            ps.setDate(5, inserttime);
            // Now we set the update where condition, or tagname in insertion
            ps.setString(6, entity.getTagName());

            log.debug("Dump preparedstatement {}", ps);
            ps.execute();
            log.debug("Search for stored tag meta as a verification, use tag name {} ",
                    entity.getTagName());
        }
        catch (final SQLException e) {
            log.error("Sql exception when storing payload with sql {} : {}", sql, e.getMessage());
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException e) {
                log.error("Error in closing streams...potential leak");
            }
        }
    }

    /**
     * @param rs  the ResultSet
     * @param key the String
     * @return byte[]
     * @throws SQLException If an Exception occurred
     */
    protected abstract String getBlob(ResultSet rs, String key) throws SQLException;

    /**
     * @param entity
     * @return TagMetaDto
     * @throws AbstractCdbServiceException
     */
    protected TagMetaDto saveBlobAsBytes(TagMetaDto entity) {
        // Get the SQL.
        final String sql = TagMetaRequests.getInsertAllQuery(getTagMetaTablename());
        log.info("Insert Tag meta {} using JDBCTEMPLATE ", entity.getTagName());
        execute(null, sql, entity);
        return findMetaInfo(entity.getTagName());
    }

    /**
     * @param entity
     * @return TagMetaDto
     * @throws AbstractCdbServiceException
     */
    protected TagMetaDto updateAsBytes(TagMetaDto entity) {
        // Get the SQL.
        final String sql = TagMetaRequests.getUpdateQuery(getTagMetaTablename());
        log.info("Update Tag meta {} using JDBCTEMPLATE ", entity.getTagName());
        execute(null, sql, entity);
        return findMetaInfo(entity.getTagName());
    }
}
