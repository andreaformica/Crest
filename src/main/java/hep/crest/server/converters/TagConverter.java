package hep.crest.server.converters;

import hep.crest.server.data.pojo.Tag;
import hep.crest.server.swagger.model.TagDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Converter for tags.
 * It takes a Tag pojo and transform it to a Dto or viceversa.
 * Handle the conversion from Date to OffsetDateTime.
 */
public class TagConverter extends BidirectionalConverter<Tag, TagDto> {

    @Override
    public TagDto convertTo(Tag source, Type<TagDto> destinationType,
                            MappingContext mappingContext) {
        // Create a Dto.
        TagDto dto = new TagDto();
        // Set the insertion time as an Offsetdate time.
        if (source.insertionTime() != null) {
            final Instant insinst = source.insertionTime().toInstant();
            // Use UTC for timezone.
            OffsetDateTime it = insinst.atOffset(ZoneOffset.UTC);
            dto.insertionTime(it);
        }
        // Set the modification time as an Offsetdate time.
        if (source.modificationTime() != null) {
            final Instant insinst = source.modificationTime().toInstant();
            // Use UTC for timezone.
            OffsetDateTime it = insinst.atOffset(ZoneOffset.UTC);
            dto.modificationTime(it);
        }
        // Set all fields.
        dto.name(source.name()).description(source.description())
                .timeType(source.timeType()).payloadSpec(source.objectType())
                .synchronization(source.synchronization())
                .endOfValidity(new BigDecimal(source.endOfValidity()))
                .lastValidatedTime(new BigDecimal(source.lastValidatedTime()));
        return dto;
    }

    @Override
    public Tag convertFrom(TagDto source, Type<Tag> destinationType,
                                 MappingContext mappingContext) {
        // Create a pojo.
        Tag entity = new Tag();
        // Ignore insertion and modification time.
        // Set all fields.
        entity.name(source.getName()).description(source.getDescription())
                .objectType(source.getPayloadSpec()).synchronization(source.getSynchronization())
                .timeType(source.getTimeType()).endOfValidity(source.getEndOfValidity().toBigInteger())
                .lastValidatedTime(source.getLastValidatedTime().toBigInteger());
        return entity;
    }
}
