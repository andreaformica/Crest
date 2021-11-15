/**
 *
 */
package hep.crest.data.test;

import hep.crest.data.exceptions.CdbBadRequestException;
import hep.crest.data.exceptions.CdbInternalException;
import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.exceptions.ConflictException;
import hep.crest.data.exceptions.PayloadEncodingException;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author formica
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExceptionsTests {

    private static final Logger log = LoggerFactory.getLogger(ExceptionsTests.class);

    @Test
    public void testExceptions() throws Exception {
        CdbSQLException sql = new CdbSQLException("Error in sql request");
        assertThat(sql.getResponseStatus()).isEqualTo(Response.Status.NOT_MODIFIED);
        assertThat(sql.getMessage()).contains("SQL");

        CdbNotFoundException notfound = new CdbNotFoundException("Entity not found");
        assertThat(notfound.getResponseStatus()).isEqualTo(Response.Status.NOT_FOUND);
        assertThat(notfound.getMessage()).contains("Resource");

        CdbBadRequestException badr = new CdbBadRequestException("Some bad request");
        assertThat(badr.getResponseStatus()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(badr.getMessage()).contains("Bad request");

        ConflictException conf = new ConflictException("Some conflict");
        assertThat(conf.getResponseStatus()).isEqualTo(Response.Status.CONFLICT);
        assertThat(conf.getMessage()).contains("Conflict");

        CdbInternalException cint = new CdbInternalException("Some internal error");
        assertThat(cint.getResponseStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(cint.getMessage()).contains("Internal");

        PayloadEncodingException pyld = new PayloadEncodingException("Some error in payload");
        assertThat(pyld.getResponseStatus()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(pyld.getMessage()).contains("Encoding");
    }

    @Test
    public void testExceptionsWithReThrow() throws Exception {
        RuntimeException e = new RuntimeException("some runtime");
        CdbSQLException sql = new CdbSQLException("Error in sql request", e);
        assertThat(sql.getResponseStatus()).isEqualTo(Response.Status.NOT_MODIFIED);
        assertThat(sql.getMessage()).contains("SQL");

        CdbNotFoundException notfound = new CdbNotFoundException("Entity not found", e);
        assertThat(notfound.getResponseStatus()).isEqualTo(Response.Status.NOT_FOUND);
        assertThat(notfound.getMessage()).contains("Resource");

        CdbBadRequestException badr = new CdbBadRequestException("Some bad request", e);
        assertThat(badr.getResponseStatus()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(badr.getMessage()).contains("Bad request");

        ConflictException conf = new ConflictException("Some conflict", e);
        assertThat(conf.getResponseStatus()).isEqualTo(Response.Status.CONFLICT);
        assertThat(conf.getMessage()).contains("Conflict");

        CdbInternalException cint = new CdbInternalException("Some internal error", e);
        assertThat(cint.getResponseStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(cint.getMessage()).contains("Internal");

        PayloadEncodingException pyld = new PayloadEncodingException("Some error in payload", e);
        assertThat(pyld.getResponseStatus()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(pyld.getMessage()).contains("Encoding");
    }

    @Test
    public void testExceptionsWithReThrowOnly() throws Exception {
        RuntimeException e = new RuntimeException("some runtime");
        CdbSQLException sql = new CdbSQLException(e);
        assertThat(sql.getResponseStatus()).isEqualTo(Response.Status.NOT_MODIFIED);
        assertThat(sql.getMessage()).contains("SQL");

        CdbNotFoundException notfound = new CdbNotFoundException(e);
        assertThat(notfound.getResponseStatus()).isEqualTo(Response.Status.NOT_FOUND);
        assertThat(notfound.getMessage()).contains("Resource");

        CdbBadRequestException badr = new CdbBadRequestException(e);
        assertThat(badr.getResponseStatus()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(badr.getMessage()).contains("Bad request");

        ConflictException conf = new ConflictException(e);
        assertThat(conf.getResponseStatus()).isEqualTo(Response.Status.CONFLICT);
        assertThat(conf.getMessage()).contains("Conflict");

        CdbInternalException cint = new CdbInternalException(e);
        assertThat(cint.getResponseStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(cint.getMessage()).contains("Internal");

        PayloadEncodingException pyld = new PayloadEncodingException(e);
        assertThat(pyld.getResponseStatus()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(pyld.getMessage()).contains("Encoding");
    }

}
