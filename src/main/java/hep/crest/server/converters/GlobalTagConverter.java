package hep.crest.server.converters;

import hep.crest.server.data.pojo.GlobalTag;
import hep.crest.server.swagger.model.GlobalTagDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Converter for global tags.
 * It takes a GlobalTag pojo and transform it to a Dto or viceversa.
 * The Offsetdatetime is using UTC offset.
 */
public class GlobalTagConverter extends BidirectionalConverter<GlobalTag, GlobalTagDto> {

    @Override
    public GlobalTagDto convertTo(GlobalTag source, Type<GlobalTagDto> destinationType,
                                  MappingContext mappingContext) {
        GlobalTagDto dto = new GlobalTagDto();
        // Transform the insertion time in an offsetdatetime.
        if (source.insertionTime() != null) {
            Long instimemilli = (source.insertionTime().getTime());
            // Set the milliseconds.
            dto.insertionTimeMilli(instimemilli);
            final Instant insinst = source.insertionTime().toInstant();
            // Set the offsetdatetime.
            OffsetDateTime it = insinst.atOffset(ZoneOffset.UTC);
            dto.insertionTime(it);
        }
        // Transform the snapshot time in an offsetdatetime.
        if (source.snapshotTime() != null) {
            Long snaptimemilli = (source.snapshotTime().getTime());
            // Set the milliseconds.
            dto.snapshotTimeMilli(snaptimemilli);
            final Instant sinst = source.snapshotTime().toInstant();
            // Set the offsetdatetime.
            OffsetDateTime st = sinst.atOffset(ZoneOffset.UTC);
            dto.snapshotTime(st);
        }
        if (source.validity() != null) {
            dto.validity(source.validity().longValue());
        }
        // Set all the fields from source.
        dto.name(source.name()).description(source.description())
                .workflow(source.workflow()).release(source.release())
                .scenario(source.scenario())
                .type(String.valueOf(source.type()));
        return dto;
    }

    @Override
    public GlobalTag convertFrom(GlobalTagDto source, Type<GlobalTag> destinationType,
                                 MappingContext mappingContext) {
        GlobalTag entity = new GlobalTag();
        // Transform the snapshot time in an date.
        if (source.getSnapshotTime() != null) {
            final Instant sinst = source.getSnapshotTime().toInstant();
            Date st = Date.from(sinst);
            entity.snapshotTime(st);
        }
        // Transform the insertion time in an date.
        if (source.getInsertionTime() != null) {
            final Instant insinst = source.getInsertionTime().toInstant();
            Date it = Date.from(insinst);
            entity.insertionTime(it);
        }
        // Get the first char for field type.
        // Since the length of Type column is one char, we guarantee to take only
        // the first from the String.
        if (source.getType() != null) {
            entity.type(source.getType().toCharArray()[0]);
        }
        if (source.getValidity() != null) {
            entity.validity(BigDecimal.valueOf(source.getValidity()));
        }
        // Set all the fields from source.
        entity.name(source.getName()).description(source.getDescription())
                .workflow(source.getWorkflow()).release(source.getRelease())
                .scenario(source.getScenario());
        return entity;
    }
}
