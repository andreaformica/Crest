package hep.crest.data.config.converters;

import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.swagger.model.IovDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

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
        // Set all fields.
        dto.since(id.since()).payloadHash(source.payloadHash()).tagName(id.tagName());
        return dto;
    }

    @Override
    public Iov convertFrom(IovDto source, Type<Iov> destinationType,
                           MappingContext mappingContext) {
        Iov entity = new Iov();
        // Create the IovId.
        IovId id = new IovId().since(source.getSince()).tagName(source.getTagName());
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
