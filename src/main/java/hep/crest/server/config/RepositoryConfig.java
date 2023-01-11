package hep.crest.server.config;

import hep.crest.server.data.repositories.IovGroupsCustom;
import hep.crest.server.data.repositories.IovGroupsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Repository configuration.
 *
 * @author formica
 */
@Configuration
@ComponentScan("hep.crest.server")
@Slf4j
public class RepositoryConfig {

    /**
     * Create a helper bean.
     * @param cprops the properties.
     * @return CrestTableNames
     */
    @Bean
    public CrestTableNames crestTableNames(@Autowired CrestProperties cprops) {
        final CrestTableNames bean = new CrestTableNames();
        // Set default schema and table name taken from properties.
        if (!"none".equals(cprops.getSchemaname())) {
            bean.setDefaultTablename(cprops.getSchemaname());
        }
        return bean;
    }

    /**
     * Create a IovGroups Repository bean.
     *
     * @param mainDataSource the DataSource
     * @param crestTableNames the helper for table names
     * @return IovGroupsCustom
     */
    @Bean
    public IovGroupsCustom iovGroupsRepository(@Qualifier("dataSource") DataSource mainDataSource,
                                               @Qualifier("crestTableNames") CrestTableNames crestTableNames) {
        final IovGroupsImpl bean = new IovGroupsImpl(mainDataSource);
        // Set default schema and table name taken from properties.
        bean.setCrestTableNames(crestTableNames);
        return bean;
    }

}
