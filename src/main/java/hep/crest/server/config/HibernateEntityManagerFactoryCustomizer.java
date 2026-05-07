package hep.crest.server.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Component that sets up Hibernate properties from application.properties
 * early in the Spring bootstrap process.
 * <p>
 * Uses @PostConstruct to read configuration from application.properties
 * and set as system property for DynamicPhysicalNamingStrategy to use
 * during SessionFactory initialization.
 */
@Slf4j
@Component
public class HibernateEntityManagerFactoryCustomizer {

    /**
     * Spring Environment to read application properties.
     */
    @Autowired
    private Environment environment;

    /**
     * Initialize and configure Hibernate properties after bean construction.
     * Called by Spring after all dependencies are injected.
     */
    @PostConstruct
    public void initializeHibernateProperties() {
        try {
            log.debug("Initializing Hibernate properties from application.properties");
            
            // Read the table name configuration from application.properties
            String tableName = environment.getProperty(
                    "crest.runlumiinfo-table",
                    "RUN_INFO_VIEW"  // Default value
            );

            log.info("Configuring Hibernate with RunLumiInfo table name: {} (from application.properties)", tableName);

            // Set as system property so DynamicPhysicalNamingStrategy can access it
            // This needs to be set BEFORE SessionFactory tries to initialize
            System.setProperty("crest.runlumiinfo-table", tableName);

            log.debug("System property 'crest.runlumiinfo-table' set to: {}", tableName);
        }
        catch (Exception e) {
            log.warn("Could not configure Hibernate properties - will fall back to defaults", e);
        }
    }
}




