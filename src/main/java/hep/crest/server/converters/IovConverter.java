package hep.crest.server.converters;

import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.swagger.model.IovDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Converter for Iovs.
 * It takes a Iov pojo and transform it to a Dto or viceversa.
 * It is important to correctly deal with the internal IovId class
 * of the pojo, which contains
 * relevant information like the since and the insertion time.
 */
public class IovConverter extends BidirectionalConverter<Iov, IovDto> {

    @Override
    public IovDto convertTo(Iov source, Type<IovDto> destinationType,
                            MappingContext mappingContext) {

        IovDto dto = new IovDto();
        // Get the IovId to extract relevant fields.
        IovId id = source.id();
        // Transform insertion time in Offsetdate time.
        if (id.insertionTime() != null) {
            final Instant insinst = id.insertionTime().toInstant();
            OffsetDateTime it = insinst.atOffset(ZoneOffset.UTC);
            dto.insertionTime(it);
        }
        if (id.since() == null) {
            dto.since(null);
        }
        else {
            dto.since(new BigDecimal(id.since()));
        }
        // Set all fields.
        dto.payloadHash(source.payloadHash()).tagName(id.tagName());
        return dto;
    }

    @Override
    public Iov convertFrom(IovDto source, Type<Iov> destinationType,
                           MappingContext mappingContext) {
        Iov entity = new Iov();
        // Create the IovId.
        BigInteger s = null;
        if (source.getSince() != null) {
            s = source.getSince().toBigInteger();
        }
        IovId id = new IovId().since(s).tagName(source.getTagName());
        // Set the insertion time as a date.
        if (source.getInsertionTime() != null) {
            final Instant insinst = source.getInsertionTime().toInstant();
            Date it = Date.from(insinst);
            id.insertionTime(it);
        }
        // Set all fields.
        entity.id(id).payloadHash(source.getPayloadHash());
        return entity;
    }
}
