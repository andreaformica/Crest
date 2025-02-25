package hep.crest.server.config;

import hep.crest.server.repositories.triggerdb.ITriggerDb;
import hep.crest.server.repositories.triggerdb.TriggerDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
@ComponentScan("hep.crest.server")
@Profile("triggerdb")
@Slf4j
public class TriggerRepositoryConfig {

    /**
     * Create a DataSource for trigger DB.
     *
     * @return DataSource
     */
    @Bean(name = "triggerDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.trigger")
    public DataSource triggerDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Create a repository for trigger DB data.
     *
     * @param triggerDataSource
     * @return ITriggerDb
     */
    @Bean
    public ITriggerDb triggerDbRepository(@Qualifier("triggerDataSource") DataSource triggerDataSource) {
        return new TriggerDb(triggerDataSource);
    }

}
