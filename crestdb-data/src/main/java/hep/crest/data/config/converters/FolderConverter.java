package hep.crest.data.config.converters;

import hep.crest.data.security.pojo.CrestFolders;
import hep.crest.swagger.model.FolderDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Converter for folders.
 * It takes a CrestFolder pojo and transform it to a Dto or viceversa.
 */
public class FolderConverter extends BidirectionalConverter<CrestFolders, FolderDto> {

    @Override
    public FolderDto convertTo(CrestFolders source, Type<FolderDto> destinationType,
                               MappingContext mappingContext) {
        // Create the dto.
        FolderDto dto = new FolderDto();
        // Set all the fields from source.
        dto.nodeName(source.nodeName()).nodeDescription(source.nodeDescription())
                .schemaName(source.schemaName()).groupRole(source.groupRole())
                .nodeFullpath(source.nodeFullpath())
                .tagPattern(source.tagPattern());
        return dto;
    }

    @Override
    public CrestFolders convertFrom(FolderDto source, Type<CrestFolders> destinationType,
                                 MappingContext mappingContext) {
        // Create the pojo.
        CrestFolders entity = new CrestFolders();
        // Set all the fields from source.
        entity.nodeName(source.getNodeName()).nodeDescription(source.getNodeDescription())
                .schemaName(source.getSchemaName()).groupRole(source.getGroupRole())
                .nodeFullpath(source.getNodeFullpath())
                .tagPattern(source.getTagPattern());
        return entity;
    }
}
