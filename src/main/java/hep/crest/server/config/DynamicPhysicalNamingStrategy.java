package hep.crest.server.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Custom Hibernate PhysicalNamingStrategy to dynamically resolve table names.
 * This allows switching between different table names (e.g., RUN_INFO_VIEW vs RUN_LUMI_INFO)
 * without code duplication or maintaining multiple entity classes.
 */
@Slf4j
@Component
public class DynamicPhysicalNamingStrategy implements PhysicalNamingStrategy {

    /**
     * Configuration properties to determine which table name to use for RunLumiInfo.
     */
    @Autowired(required = false)
    private CrestProperties crestProperties;

    /**
     * Spring Environment to read application properties.
     */
    @Autowired(required = false)
    private Environment environment;

    /**
     * Convert a table name to its physical equivalent.
     *
     * @param name    the logical table name
     * @param context the JDBC environment context
     * @return the physical table name
     */
    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        if (name == null) {
            return null;
        }

        // Check if this is the RunLumiInfo table that needs dynamic name resolution
        if ("RUN_INFO_VIEW".equals(name.getText())) {
            String dynamicTableName = resolveTableName();
            log.info("Resolving table RUN_INFO_VIEW to dynamic table: {}", dynamicTableName);
            return Identifier.toIdentifier(dynamicTableName);
        }

        // Return the original identifier for all other tables
        return name;
    }

    /**
     * Resolve the table name using multiple fallback strategies.
     * Tries in order: CrestProperties bean, Spring Environment, system property, environment variable, default.
     * 
     * @return the resolved table name
     */
    private String resolveTableName() {
        // Strategy 1: Try Spring CrestProperties bean (most reliable)
        if (crestProperties != null) {
            String table = crestProperties.getRunluminfoTable();
            if (table != null && !table.isEmpty()) {
                log.debug("Resolved table name from CrestProperties: {}", table);
                return table;
            }
        }

        // Strategy 2: Try Spring Environment (from application.properties)
        // This works at method call time, so application.properties are available
        if (environment != null) {
            String table = environment.getProperty("crest.runlumiinfo-table");
            if (table != null && !table.isEmpty()) {
                log.debug("Resolved table name from Spring Environment: {}", table);
                return table;
            }
        }

        // Strategy 3: Try JVM system property (-Dcrest.runlumiinfo-table=...)
        String systemProperty = System.getProperty("crest.runlumiinfo-table");
        if (systemProperty != null && !systemProperty.isEmpty()) {
            log.debug("Resolved table name from system property: {}", systemProperty);
            return systemProperty;
        }

        // Strategy 4: Try environment variable (CREST_RUNLUMIINFO_TABLE)
        String envVariable = System.getenv("CREST_RUNLUMIINFO_TABLE");
        if (envVariable != null && !envVariable.isEmpty()) {
            log.debug("Resolved table name from environment variable: {}", envVariable);
            return envVariable;
        }

        // Strategy 5: Default fallback
        log.debug("Using default table name: RUN_INFO_VIEW");
        return "RUN_INFO_VIEW";
    }

    /**
     * Convert a column name to its physical equivalent.
     * No custom logic needed for columns.
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    /**
     * Convert a sequence name to its physical equivalent.
     * No custom logic needed for sequences.
     */
    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    /**
     * Convert a catalog name to its physical equivalent.
     * No custom logic needed for catalogs.
     */
    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    /**
     * Convert a schema name to its physical equivalent.
     * No custom logic needed for schemas.
     */
    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
        return name;
    }
}


