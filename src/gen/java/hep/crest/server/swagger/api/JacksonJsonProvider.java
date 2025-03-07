package hep.crest.server.swagger.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.datatype.jsr310.*;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces({MediaType.APPLICATION_JSON})
public class JacksonJsonProvider extends JacksonJaxbJsonProvider {

    public JacksonJsonProvider() {

        ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule())
            .setDateFormat(new RFC3339DateFormat());

        setMapper(objectMapper);
    }
}
