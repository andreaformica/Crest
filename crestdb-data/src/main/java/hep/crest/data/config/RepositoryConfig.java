package hep.crest.data.config;

import hep.crest.data.monitoring.repositories.IMonitoringRepository;
import hep.crest.data.monitoring.repositories.JdbcMonitoringRepository;
import hep.crest.data.repositories.IovGroupsCustom;
import hep.crest.data.repositories.IovGroupsImpl;
import hep.crest.data.repositories.PayloadDataBaseCustom;
import hep.crest.data.repositories.PayloadDataDBImpl;
import hep.crest.data.repositories.PayloadDataPostgresImpl;
import hep.crest.data.repositories.PayloadDataSQLITEImpl;
import lombok.extern.slf4j.Slf4j;
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
 */
@Configuration
@ComponentScan("hep.crest.data")
@Slf4j
public class RepositoryConfig {


    /**
     * The properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * Create a helper bean.
     *
     * @return CrestTableNames
     */
    @Bean
    public CrestTableNames crestTableNames() {
        final CrestTableNames bean = new CrestTableNames();
        // Set default schema and table name taken from properties.
        if (!"none".equals(cprops.getSchemaname())) {
            bean.setDefaultTablename(cprops.getSchemaname());
        }
        return bean;
    }


    /**
     * Create a monitoring bean.
     *
     * @param mainDataSource the DataSource
     * @param ctn the helper for table names
     * @return IMonitoringRepository
     */
    @Bean
    public IMonitoringRepository iMonitoringRepository(@Qualifier("dataSource") DataSource mainDataSource,
                                                       @Qualifier("crestTableNames") CrestTableNames ctn) {
        final JdbcMonitoringRepository bean = new JdbcMonitoringRepository(mainDataSource);
        // Set default schema and table name taken from properties.
        bean.setCrestTableNames(ctn);
        return bean;
    }

    /**
     * Create group repository.
     *
     * @param mainDataSource the DataSource
     * @param ctn the helper for table names
     * @return IovGroupsCustom
     */
    @Bean(name = "iovgroupsrepo")
    public IovGroupsCustom iovgroupsRepository(@Qualifier("dataSource") DataSource mainDataSource,
                                               @Qualifier("crestTableNames") CrestTableNames ctn) {
        final IovGroupsImpl bean = new IovGroupsImpl(mainDataSource);
        // Set default schema and table name taken from properties.
        bean.setCrestTableNames(ctn);
        return bean;
    }

    /**
     * Create payload repo...for all but postgres and sqlite.
     *
     * @param mainDataSource the DataSource
     * @param ctn the helper for table names
     * @return PayloadDataBaseCustom
     */
    @Profile({"test", "default", "ssl", "mysql", "cmsprep", "oracle"})
    @Bean(name = "payloaddatadbrepo")
    public PayloadDataBaseCustom payloadDefaultRepository(
            @Qualifier("dataSource") DataSource mainDataSource,
            @Qualifier("crestTableNames") CrestTableNames ctn) {
        final PayloadDataDBImpl bean = new PayloadDataDBImpl(mainDataSource);
        // Set default schema and table name taken from properties.
        bean.setCrestTableNames(ctn);
        log.info("Creating default payload repository implementation.");
        return bean;
    }

    /**
     * Create payload repo...for postgres.
     *
     * @param mainDataSource the DataSource
     * @param ctn the helper for table names
     * @return PayloadDataBaseCustom
     */
    @Profile({"postgres", "pgsvom"})
    @Bean(name = "payloaddatadbrepo")
    public PayloadDataBaseCustom payloadPostgresRepository(
            @Qualifier("dataSource") DataSource mainDataSource,
            @Qualifier("crestTableNames") CrestTableNames ctn) {
        final PayloadDataPostgresImpl bean = new PayloadDataPostgresImpl(mainDataSource);
        // Set default schema and table name taken from properties.
        bean.setCrestTableNames(ctn);
        log.info("Creating postgres payload repository implementation.");
        return bean;
    }

    /**
     * Create payload repo...for sqlite.
     *
     * @param mainDataSource the DataSource
     * @param ctn the helper for table names
     * @return PayloadDataBaseCustom
     */
    @Profile("sqlite")
    @Bean(name = "payloaddatadbrepo")
    public PayloadDataBaseCustom payloadSqliteRepository(
            @Qualifier("dataSource") DataSource mainDataSource,
            @Qualifier("crestTableNames") CrestTableNames ctn) {
        final PayloadDataSQLITEImpl bean = new PayloadDataSQLITEImpl(mainDataSource);
        // Set default schema and table name taken from properties.
        bean.setCrestTableNames(ctn);
        log.info("Creating sqlite payload repository implementation.");
        return bean;
    }
}
