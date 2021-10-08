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
 * Converter for global tags.
 */
public class IovConverter extends BidirectionalConverter<Iov, IovDto> {

    @Override
    public IovDto convertTo(Iov source, Type<IovDto> destinationType,
                            MappingContext mappingContext) {
        IovDto dto = new IovDto();
        IovId id = source.id();

        if (id.insertionTime() != null) {
            final Instant insinst = id.insertionTime().toInstant();
            OffsetDateTime it = insinst.atOffset(ZoneOffset.UTC);
            dto.insertionTime(it);
        }
        dto.since(id.since()).payloadHash(source.payloadHash()).tagName(id.tagName());
        return dto;
    }

    @Override
    public Iov convertFrom(IovDto source, Type<Iov> destinationType,
                           MappingContext mappingContext) {
        Iov entity = new Iov();
        IovId id = new IovId().since(source.getSince()).tagName(source.getTagName());
        if (source.getInsertionTime() != null) {
            final Instant insinst = source.getInsertionTime().toInstant();
            Date it = Date.from(insinst);
            id.insertionTime(it);
        }
        entity.id(id).payloadHash(source.getPayloadHash());
        return entity;
    }
}
