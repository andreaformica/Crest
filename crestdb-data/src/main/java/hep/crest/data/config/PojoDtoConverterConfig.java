package hep.crest.data.config;

import hep.crest.data.config.converters.DateToOffDateTimeConverter;
import hep.crest.data.config.converters.FolderConverter;
import hep.crest.data.config.converters.GlobalTagConverter;
import hep.crest.data.config.converters.GlobalTagMapConverter;
import hep.crest.data.config.converters.IovConverter;
import hep.crest.data.config.converters.TagConverter;
import hep.crest.data.config.converters.TimestampToOffDateTimeConverter;
import hep.crest.data.runinfo.pojo.RunInfo;
import hep.crest.swagger.model.RunInfoDto;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author formica
 */
@Configuration
public class PojoDtoConverterConfig {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PojoDtoConverterConfig.class);

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
        // Init mapper for runinfo.
        this.initRunInfoMap(mapperFactory);
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
     * @return MapperFacade
     */
    @Bean(name = "mapper")
    @Autowired
    public MapperFacade createMapperFacade(
            @Qualifier("mapperFactory") MapperFactory mapperFactory) {
        return mapperFactory.getMapperFacade();
    }
}
