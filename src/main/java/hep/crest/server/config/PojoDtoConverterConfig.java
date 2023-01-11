package hep.crest.server.config;

import hep.crest.server.converters.DateToOffDateTimeConverter;
import hep.crest.server.converters.FolderConverter;
import hep.crest.server.converters.GlobalTagConverter;
import hep.crest.server.converters.GlobalTagMapConverter;
import hep.crest.server.converters.IovConverter;
import hep.crest.server.converters.TagConverter;
import hep.crest.server.converters.TagMetaConverter;
import hep.crest.server.converters.TimestampToOffDateTimeConverter;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.runinfo.pojo.RunLumiInfo;
import hep.crest.server.swagger.model.PayloadDto;
import hep.crest.server.swagger.model.RunLumiInfoDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author formica
 */
@Configuration
@Slf4j
public class PojoDtoConverterConfig {


    /**
     * @return MapperFactory
     */
    @Bean(name = "mapperFactory")
    public MapperFactory createOrikaMapperFactory() {
        // Mapper factory with strategy for property resolution.
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
                .propertyResolverStrategy(new BuilderPropertyResolver())
                .build();
        // Converter factory.
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        // Register converter for Date->OffesetDateTime.
        converterFactory.registerConverter(new DateToOffDateTimeConverter());
        // Register converter for Timestamp->OffesetDateTime.
        converterFactory.registerConverter(new TimestampToOffDateTimeConverter());
        // Register converter for GlobalTag.
        converterFactory.registerConverter(new GlobalTagConverter());
        // Register converter for GlobalTagMap.
        converterFactory.registerConverter(new GlobalTagMapConverter());
        // Register converter for Tag.
        converterFactory.registerConverter(new TagConverter());
        // Register converter for Iov.
        converterFactory.registerConverter(new IovConverter());
        // Register converter for Folder.
        converterFactory.registerConverter(new FolderConverter());
        // Register converter for TagMeta
        converterFactory.registerConverter(new TagMetaConverter());
        // Init mapper for runinfo.
        this.initRunInfoMap(mapperFactory);
        // init payload mapper.
        this.initPayloadMap(mapperFactory);
        return mapperFactory;
    }

    /**
     * @param mapperFactory the MapperFactory
     * @return
     */
    protected void initRunInfoMap(MapperFactory mapperFactory) {
        mapperFactory.classMap(RunLumiInfo.class, RunLumiInfoDto.class)
                .byDefault().register();
    }

    /**
     * @param mapperFactory the MapperFactory
     * @return
     */
    protected void initPayloadMap(MapperFactory mapperFactory) {
        mapperFactory.classMap(Payload.class, PayloadDto.class)
                .byDefault().register();
    }


    /**
     * @param mapperFactory the MapperFactory
     * @return MapperFacade
     */
    @Bean(name = "mapper")
    @Autowired
    public MapperFacade createMapperFacade(
            @Qualifier("mapperFactory") MapperFactory mapperFactory) {
        return mapperFactory.getMapperFacade();
    }
}
