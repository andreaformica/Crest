package hep.crest.data.config;

import hep.crest.data.config.converters.DateToOffDateTimeConverter;
import hep.crest.data.config.converters.GlobalTagConverter;
import hep.crest.data.config.converters.GlobalTagMapConverter;
import hep.crest.data.config.converters.IovConverter;
import hep.crest.data.config.converters.TagConverter;
import hep.crest.data.config.converters.TimestampToOffDateTimeConverter;
import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Payload;
import hep.crest.data.pojo.Tag;
import hep.crest.data.runinfo.pojo.RunLumiInfo;
import hep.crest.data.security.pojo.CrestFolders;
import hep.crest.swagger.model.FolderDto;
import hep.crest.swagger.model.GlobalTagDto;
import hep.crest.swagger.model.GlobalTagMapDto;
import hep.crest.swagger.model.IovDto;
import hep.crest.swagger.model.PayloadDto;
import hep.crest.swagger.model.RunLumiInfoDto;
import hep.crest.swagger.model.TagDto;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;

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
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
                .propertyResolverStrategy(new BuilderPropertyResolver())
                .build();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new DateToOffDateTimeConverter());
        converterFactory.registerConverter(new TimestampToOffDateTimeConverter());
        converterFactory.registerConverter(new GlobalTagConverter());
        converterFactory.registerConverter(new GlobalTagMapConverter());
        converterFactory.registerConverter(new TagConverter());
        converterFactory.registerConverter(new IovConverter());
        this.initPayloadMap(mapperFactory);
        this.initRunInfoMap(mapperFactory);
        this.initFolderMap(mapperFactory);
        return mapperFactory;
    }

    /**
     * @param mapperFactory the MapperFactory
     * @return
     */
    protected void initGlobalTagMap(MapperFactory mapperFactory) {
        // Not used, we prefer then implementation provided above.
        mapperFactory.classMap(GlobalTag.class, GlobalTagDto.class).exclude("globalTagMaps").byDefault()
                .customize(new CustomMapper<GlobalTag, GlobalTagDto>() {
                    @Override
                    public void mapAtoB(GlobalTag a, GlobalTagDto b, MappingContext context) {
                        log.info("Orika: mapping global tag {} into dto {}", a, b);
                        // Use insertion time provided when not null.
                        if (a.insertionTime() != null) {
                            Long instimemilli = (a.insertionTime().getTime());
                            b.insertionTimeMilli(instimemilli);
                        }
                        // Use snapshot time provided when not null.
                        if (a.insertionTime() != null) {
                            Long snaptimemilli = (a.insertionTime().getTime());
                            b.snapshotTimeMilli(snaptimemilli);
                        }
                    }

                    @Override
                    public void mapBtoA(GlobalTagDto a, GlobalTag b, MappingContext context) {
                        // ignore the millisec fields.
                        log.info("Orika: mapping global tag dto {} into entity {}", a, b);
                    }
                }).register();
    }

    /**
     * @param mapperFactory the MapperFactory
     * @return
     */
    protected void initGlobalTagMapsMap(MapperFactory mapperFactory) {
        // Not used, we prefer then implementation provided above.
        mapperFactory.classMap(GlobalTagMap.class, GlobalTagMapDto.class)
                .field("id.globalTagName", "globalTagName").field("id.record", "record")
                .field("id.label", "label").field("tag.name", "tagName").register();
    }

    /**
     * @param mapperFactory the MapperFactory
     * @return
     */
    protected void initTagMap(MapperFactory mapperFactory) {
        // Not used, we prefer then implementation provided above.
        mapperFactory.classMap(Tag.class, TagDto.class).field("objectType", "payloadSpec")
                .exclude("globalTagMaps").byDefault().register();
    }

    /**
     * @param mapperFactory the MapperFactory
     * @return
     */
    protected void initIovMap(MapperFactory mapperFactory) {
        // Not used, we prefer then implementation provided above.
        mapperFactory.classMap(Iov.class, IovDto.class).field("id.since", "since")
                .field("id.insertionTime", "insertionTime")
                .field("payloadHash", "payloadHash")
                .register();
    }

    /**
     * @param mapperFactory the MapperFactory
     * @return
     */
    protected void initPayloadMap(MapperFactory mapperFactory) {
        // Provide a dedicated mapper, using UTC as time zone.
        mapperFactory.classMap(Payload.class, PayloadDto.class).byDefault()
                .customize(new CustomMapper<Payload, PayloadDto>() {
                    @Override
                    public void mapAtoB(Payload a, PayloadDto b, MappingContext context) {
                        try {
                            Instant it = Instant.ofEpochMilli(a.insertionTime().getTime());
                            b.hash(a.hash()).version(a.version())
                                    .objectType(a.objectType())
                                    .data(a.data().getBytes(1, (int) a.data().length()))
                                    .streamerInfo(a.streamerInfo().getBytes(1,
                                            (int) a.streamerInfo().length()))
                                    .size(a.size()).insertionTime(it.atOffset(ZoneOffset.UTC));
                        }
                        catch (final SQLException e) {
                            log.error("SQL exception in mapping pojo and dto for payload...: {}",
                                    e.getMessage());
                        }
                    }
                }).register();
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
    protected void initFolderMap(MapperFactory mapperFactory) {
        // Use the default orika mapping.
        mapperFactory.classMap(CrestFolders.class, FolderDto.class).byDefault().register();
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
