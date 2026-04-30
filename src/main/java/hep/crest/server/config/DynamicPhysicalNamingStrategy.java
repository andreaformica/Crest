package hep.crest.server.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
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
        if ("RUN_INFO_VIEW".equals(name.getText()) && crestProperties != null) {
            String dynamicTableName = crestProperties.getRunluminfoTable();
            log.info("Resolving table RUN_INFO_VIEW to dynamic table: {}", dynamicTableName);
            return Identifier.toIdentifier(dynamicTableName);
        }

        // Return the original identifier for all other tables
        return name;
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


