package hep.crest.server.converters;

import hep.crest.data.pojo.TagMeta;
import hep.crest.server.swagger.model.TagMetaDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Converter for tags.
 * It takes a Tag pojo and transform it to a Dto or viceversa.
 * Handle the conversion from Date to OffsetDateTime.
 */
public class TagMetaConverter extends BidirectionalConverter<TagMeta, TagMetaDto> {

    @Override
    public TagMetaDto convertTo(TagMeta source, Type<TagMetaDto> destinationType,
                                MappingContext mappingContext) {
        // Create a Dto.
        TagMetaDto dto = new TagMetaDto();
        // Set the insertion time as an Offsetdate time.
        if (source.insertionTime() != null) {
            final Instant insinst = source.insertionTime().toInstant();
            // Use UTC for timezone.
            OffsetDateTime it = insinst.atOffset(ZoneOffset.UTC);
            dto.insertionTime(it);
        }
        // Set all fields.
        dto.tagName(source.tagName()).description(source.description())
                .colsize(source.colsize()).chansize(source.chansize())
                .tagInfo(new String(source.tagInfo()));
        return dto;
    }

    @Override
    public TagMeta convertFrom(TagMetaDto source, Type<TagMeta> destinationType,
                               MappingContext mappingContext) {
        // Create a pojo.
        TagMeta entity = new TagMeta();
        // Set insertion time as date.
        if (source.getInsertionTime() != null) {
            final Instant insinst = source.getInsertionTime().toInstant();
            Date it = Date.from(insinst);
            entity.insertionTime(it);
        }
        // Set all fields.
        entity.tagName(source.getTagName()).description(source.getDescription())
                .colsize(source.getColsize()).chansize(source.getChansize())
                .tagInfo(source.getTagInfo().getBytes(StandardCharsets.UTF_8));
        return entity;
    }
}
