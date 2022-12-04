package hep.crest.server.converters;

import hep.crest.server.data.pojo.GlobalTagMap;
import hep.crest.server.data.pojo.GlobalTagMapId;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.swagger.model.GlobalTagMapDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Converter for global tags.
 * It takes a GlobalTagMap pojo and transform it to a Dto or viceversa.
 * The ID for the map is extracted separately, and the Tag is created using the tagname in input.
 */
public class GlobalTagMapConverter extends BidirectionalConverter<GlobalTagMap, GlobalTagMapDto> {

    @Override
    public GlobalTagMapDto convertTo(GlobalTagMap source, Type<GlobalTagMapDto> destinationType,
                                     MappingContext mappingContext) {
        GlobalTagMapDto dto = new GlobalTagMapDto();
        // Get the Id. It is used to extract fields and set them in the DTO.
        GlobalTagMapId id = source.id();
        // set all fields from source.
        dto.globalTagName(id.globalTagName()).record(id.record()).label(id.label()).tagName(source.tag().name());
        return dto;
    }

    @Override
    public GlobalTagMap convertFrom(GlobalTagMapDto source, Type<GlobalTagMap> destinationType,
                                    MappingContext mappingContext) {
        GlobalTagMap entity = new GlobalTagMap();
        // Create a map ID from the fields in DTO.
        GlobalTagMapId id =
                new GlobalTagMapId().globalTagName(source.getGlobalTagName()).record(source.getRecord())
                        .label(source.getLabel());
        Tag atag = new Tag();
        // Create a tag using the provided tag name.
        atag.name(source.getTagName());
        // set all fields from source.
        entity.id(id).tag(atag);
        return entity;
    }
}
