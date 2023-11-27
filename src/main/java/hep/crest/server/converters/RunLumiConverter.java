package hep.crest.server.converters;

import hep.crest.server.data.runinfo.pojo.RunLumiId;
import hep.crest.server.data.runinfo.pojo.RunLumiInfo;
import hep.crest.server.swagger.model.RunLumiInfoDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Converter for RunLumi.
 * It takes a RunLumi pojo and transform it to a Dto or viceversa.
 * The ID for the run lumi is extracted separately.
 */
public class RunLumiConverter extends BidirectionalConverter<RunLumiInfo, RunLumiInfoDto> {

    @Override
    public RunLumiInfoDto convertTo(RunLumiInfo source, Type<RunLumiInfoDto> destinationType,
                                     MappingContext mappingContext) {
        RunLumiInfoDto dto = new RunLumiInfoDto();
        // Get the Id. It is used to extract fields and set them in the DTO.
        RunLumiId id = source.id();
        // set all fields from source.
        BigDecimal st = (source.starttime() != null)
                ? BigDecimal.valueOf(source.starttime().longValueExact()) : null;
        BigDecimal et = (source.endtime() != null)
                ? BigDecimal.valueOf(source.endtime().longValueExact()) : null;
        BigDecimal run = (id.runNumber() != null)
                ? BigDecimal.valueOf(id.runNumber().longValueExact()) : null;
        BigDecimal lb = (id.lb() != null)
                ? BigDecimal.valueOf(id.lb().longValueExact()) : null;
        dto.runNumber(run).lb(lb)
                .starttime(st)
                .endtime(et);

        return dto;
    }

    @Override
    public RunLumiInfo convertFrom(RunLumiInfoDto source, Type<RunLumiInfo> destinationType,
                                    MappingContext mappingContext) {
        RunLumiInfo entity = new RunLumiInfo();
        // Create a map ID from the fields in DTO.
        BigInteger st = (source.getStarttime() != null) ? source.getStarttime().toBigInteger() : null;
        BigInteger et = (source.getEndtime() != null) ? source.getEndtime().toBigInteger() : null;
        BigInteger run = (source.getRunNumber() != null) ? source.getRunNumber().toBigInteger() : null;
        BigInteger lb = (source.getLb() != null) ? source.getLb().toBigInteger() : null;
        RunLumiId id = new RunLumiId().runNumber(run).lb(lb);
        // Create the entity.
        entity.starttime(st).endtime(et);
        // set the id source.
        entity.id(id);
        entity.insertionTime(null);
        return entity;
    }
}
