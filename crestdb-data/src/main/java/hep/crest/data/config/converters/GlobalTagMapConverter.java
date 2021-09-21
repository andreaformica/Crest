package hep.crest.data.config.converters;

import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Tag;
import hep.crest.swagger.model.GlobalTagMapDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Converter for global tags.
 */
public class GlobalTagMapConverter extends BidirectionalConverter<GlobalTagMap, GlobalTagMapDto> {

    @Override
    public GlobalTagMapDto convertTo(GlobalTagMap source, Type<GlobalTagMapDto> destinationType,
                                     MappingContext mappingContext) {
        GlobalTagMapDto dto = new GlobalTagMapDto();
        GlobalTagMapId id = source.id();
        dto.globalTagName(id.globalTagName()).record(id.record()).label(id.label()).tagName(source.tag().name());
        return dto;
    }

    @Override
    public GlobalTagMap convertFrom(GlobalTagMapDto source, Type<GlobalTagMap> destinationType,
                                    MappingContext mappingContext) {
        GlobalTagMap entity = new GlobalTagMap();
        GlobalTagMapId id = new GlobalTagMapId(source.getGlobalTagName(), source.getRecord(), source.getLabel());
        Tag atag = new Tag();
        atag.name(source.getTagName());
        entity.id(id).tag(atag);
        return entity;
    }
}
