package hep.crest.server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hep.crest.data.config.CrestProperties;
import hep.crest.data.serializers.CustomTimeDeserializer;
import hep.crest.data.serializers.CustomTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * Services configuration.
 *
 * @version %I%, %G%
 * @author formica
 *
 */
@Configuration
@ComponentScan("hep.crest.data")
@EnableAspectJAutoProxy
@EnableAsync
public class ServicesConfig {

    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     *
     * @return ObjectMapper
     */
    @Bean(name = "jacksonMapper")
    public ObjectMapper getJacksonMapper() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                // date/time
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                // optional fraction of seconds (from 0 to 9 digits)
                .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
                // offset
                .appendPattern("xxxx")
                // create formatter
                .toFormatter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(OffsetDateTime.class, new CustomTimeSerializer(formatter));
        module.addDeserializer(OffsetDateTime.class, new CustomTimeDeserializer(formatter));
        mapper.registerModule(module);
        return mapper;
    }




    /**
     * @return LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        final SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

}
