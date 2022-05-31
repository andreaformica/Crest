package hep.crest.server.config;

import hep.crest.data.config.CrestProperties;
import hep.crest.data.config.CrestTableNames;
import hep.crest.server.repositories.TagMetaDBImpl;
import hep.crest.server.repositories.TagMetaDataBaseCustom;
import hep.crest.server.repositories.TagMetaPostgresImpl;
import hep.crest.server.repositories.TagMetaSQLITEImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Repository configuration.
 * 
 * @author formica
 *
 */
@Configuration
@ComponentScan("hep.crest")
public class AtlasRepositoryConfig {

    /**
     * The properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * @param mainDataSource
     *            the DataSource
     * @param ctn the helper for table names
     * @return TagMetaDataBaseCustom
     */
    @Profile({ "test", "default", "prod", "h2", "oracle", "ssl", "dev", "mysql" })
    @Bean(name = "tagmetarepo")
    public TagMetaDataBaseCustom tagmetaDefaultRepository(
            @Qualifier("dataSource") DataSource mainDataSource,
            @Qualifier("crestTableNames") CrestTableNames ctn) {
        final TagMetaDBImpl bean = new TagMetaDBImpl(mainDataSource);
        bean.setCrestTableNames(ctn);
        return bean;
    }

    /**
     * @param mainDataSource
     *            the DataSource
     * @param ctn the helper for table names
     * @return TagMetaDataBaseCustom
     */
    @Profile({ "postgres" })
    @Bean(name = "tagmetarepo")
    public TagMetaDataBaseCustom tagmetaPostgresRepository(
            @Qualifier("dataSource") DataSource mainDataSource,
            @Qualifier("crestTableNames") CrestTableNames ctn) {
        final TagMetaPostgresImpl bean = new TagMetaPostgresImpl(mainDataSource);
        bean.setCrestTableNames(ctn);

        return bean;
    }

    /**
     * @param mainDataSource
     *            the DataSource
     * @param ctn the helper for table names
     * @return TagMetaDataBaseCustom
     */
    @Profile({ "sqlite" })
    @Bean(name = "tagmetarepo")
    public TagMetaDataBaseCustom tagmetaSqliteRepository(
            @Qualifier("dataSource") DataSource mainDataSource,
            @Qualifier("crestTableNames") CrestTableNames ctn) {

        final TagMetaSQLITEImpl bean = new TagMetaSQLITEImpl(mainDataSource);
        bean.setCrestTableNames(ctn);

        return bean;
    }
}
