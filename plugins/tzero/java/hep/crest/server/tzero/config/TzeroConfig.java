package hep.crest.server.tzero.config;

import hep.crest.server.tzero.repositories.ITzeroDb;
import hep.crest.server.tzero.repositories.TzeroDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@ComponentScan("hep.crest.server.tzero")
@Profile("tzero")
@EnableAspectJAutoProxy
@Slf4j
public class TzeroConfig {

    /**
     * Create a DataSource for tzero DB.
     *
     * @return DataSource
     */
    @Bean(name = "tzeroDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.tzero")
    public DataSource tzeroDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Create a repository for trigger DB data.
     *
     * @param tzeroDataSource
     * @return ITriggerDb
     */
    @Bean
    public ITzeroDb tzeroDbRepository(@Qualifier("tzeroDataSource") DataSource tzeroDataSource) {
        return new TzeroDb(tzeroDataSource);
    }

}
