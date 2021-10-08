package hep.crest.data.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author formica
 */
public class CustomTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    /**
     * The formatter.
     */
    private DateTimeFormatter formatter;

    /**
     * Ctor.
     *
     * @param formatter
     */
    public CustomTimeDeserializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        OffsetDateTime odt = OffsetDateTime.parse(parser.getText(), this.formatter);
        return odt;
    }
}
