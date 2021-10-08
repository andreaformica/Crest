package hep.crest.data.config.converters;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.swagger.model.GlobalTagDto;
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
public class GlobalTagConverter extends BidirectionalConverter<GlobalTag, GlobalTagDto> {

    @Override
    public GlobalTagDto convertTo(GlobalTag source, Type<GlobalTagDto> destinationType,
                                  MappingContext mappingContext) {
        GlobalTagDto dto = new GlobalTagDto();
        if (source.insertionTime() != null) {
            Long instimemilli = (source.insertionTime().getTime());
            dto.insertionTimeMilli(instimemilli);
            final Instant insinst = source.insertionTime().toInstant();
            OffsetDateTime it = insinst.atOffset(ZoneOffset.UTC);
            dto.insertionTime(it);
        }
        if (source.snapshotTime() != null) {
            Long snaptimemilli = (source.snapshotTime().getTime());
            dto.snapshotTimeMilli(snaptimemilli);
            final Instant sinst = source.snapshotTime().toInstant();
            OffsetDateTime st = sinst.atOffset(ZoneOffset.UTC);
            dto.snapshotTime(st);
        }
        dto.name(source.name()).description(source.description())
                .workflow(source.workflow()).release(source.release())
                .scenario(source.scenario()).validity(source.validity())
                .type(String.valueOf(source.type()));
        return dto;
    }

    @Override
    public GlobalTag convertFrom(GlobalTagDto source, Type<GlobalTag> destinationType,
                                 MappingContext mappingContext) {
        GlobalTag entity = new GlobalTag();
        if (source.getSnapshotTime() != null) {
            final Instant sinst = source.getSnapshotTime().toInstant();
            Date st = Date.from(sinst);
            entity.snapshotTime(st);
        }
        if (source.getInsertionTime() != null) {
            final Instant insinst = source.getInsertionTime().toInstant();
            Date it = Date.from(insinst);
            entity.insertionTime(it);
        }
        if (source.getType() != null) {
            entity.type(source.getType().toCharArray()[0]);
        }
        entity.name(source.getName()).description(source.getDescription())
                .workflow(source.getWorkflow()).release(source.getRelease())
                .scenario(source.getScenario()).validity(source.getValidity());
        return entity;
    }
}
