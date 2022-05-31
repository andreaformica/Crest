package hep.crest.data.config.converters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author formica
 */
public class DateToOffDateTimeConverter
        extends BidirectionalConverter<Date, OffsetDateTime> {

    /*
     * (non-Javadoc)
     *
     * @see ma.glasnost.orika.converter.BidirectionalConverter#convertTo(java.lang.
     * Object, ma.glasnost.orika.metadata.Type, ma.glasnost.orika.MappingContext)
     */
    @Override
    public OffsetDateTime convertTo(Date source, Type<OffsetDateTime> destinationType,
                                    MappingContext mappingContext) {
        final Instant sinst = source.toInstant();
        return sinst.atOffset(ZoneOffset.UTC);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * ma.glasnost.orika.converter.BidirectionalConverter#convertFrom(java.lang.
     * Object, ma.glasnost.orika.metadata.Type, ma.glasnost.orika.MappingContext)
     */
    @Override
    public Date convertFrom(OffsetDateTime source, Type<Date> destinationType,
                            MappingContext mappingContext) {
        final Instant sinst = source.toInstant();
        return Date.from(sinst);
    }

}
