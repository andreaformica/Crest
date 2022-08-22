package hep.crest.server.converters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * @author formica
 */
public class TimestampToOffDateTimeConverter
        extends BidirectionalConverter<Timestamp, OffsetDateTime> {

    /*
     * (non-Javadoc)
     *
     * @see ma.glasnost.orika.converter.BidirectionalConverter#convertTo(java.lang.
     * Object, ma.glasnost.orika.metadata.Type, ma.glasnost.orika.MappingContext)
     */
    @Override
    public OffsetDateTime convertTo(Timestamp source, Type<OffsetDateTime> destinationType,
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
    public Timestamp convertFrom(OffsetDateTime source, Type<Timestamp> destinationType,
                                 MappingContext mappingContext) {
        final Instant sinst = source.toInstant();
        return Timestamp.from(sinst);
    }

}
