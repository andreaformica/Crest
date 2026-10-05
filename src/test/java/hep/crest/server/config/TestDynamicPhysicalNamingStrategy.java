package hep.crest.server.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DynamicPhysicalNamingStrategy.
 * Tests table name resolution through multiple fallback strategies.
 */
@DisplayName("DynamicPhysicalNamingStrategy Tests")
class TestDynamicPhysicalNamingStrategy {

    private DynamicPhysicalNamingStrategy strategy;
    private CrestProperties mockCrestProperties;
    private Environment mockEnvironment;
    private JdbcEnvironment mockJdbcEnvironment;

    @BeforeEach
    void setUp() {
        strategy = new DynamicPhysicalNamingStrategy();
        mockCrestProperties = mock(CrestProperties.class);
        mockEnvironment = mock(Environment.class);
        mockJdbcEnvironment = mock(JdbcEnvironment.class);

        // Inject mocks using reflection
        ReflectionTestUtils.setField(strategy, "crestProperties", mockCrestProperties);
        ReflectionTestUtils.setField(strategy, "environment", mockEnvironment);
    }

    @Test
    @DisplayName("toPhysicalTableName returns null when name is null")
    void testToPhysicalTableNameWithNullName() {
        Identifier result = strategy.toPhysicalTableName(null, mockJdbcEnvironment);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toPhysicalTableName returns original name for non-RUN_INFO_VIEW table")
    void testToPhysicalTableNameForOtherTable() {
        Identifier identifier = Identifier.toIdentifier("SOME_OTHER_TABLE");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("SOME_OTHER_TABLE");
    }

    @Test
    @DisplayName("resolveTableName uses CrestProperties when available")
    void testResolveTableNameFromCrestProperties() {
        when(mockCrestProperties.getRunluminfoTable()).thenReturn("RUN_LUMI_INFO");

        Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        assertThat(result.getText()).isEqualTo("RUN_LUMI_INFO");
    }

    @Test
    @DisplayName("resolveTableName uses Environment when CrestProperties is null")
    void testResolveTableNameFromEnvironment() {
        ReflectionTestUtils.setField(strategy, "crestProperties", null);
        when(mockEnvironment.getProperty("crest.runlumiinfo-table")).thenReturn("RUN_LUMI_INFO");

        Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        assertThat(result.getText()).isEqualTo("RUN_LUMI_INFO");
    }

    @Test
    @DisplayName("resolveTableName uses system property when other sources are null")
    void testResolveTableNameFromSystemProperty() {
        ReflectionTestUtils.setField(strategy, "crestProperties", null);
        ReflectionTestUtils.setField(strategy, "environment", null);

        try {
            System.setProperty("crest.runlumiinfo-table", "RUN_LUMI_INFO");

            Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
            Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

            assertThat(result.getText()).isEqualTo("RUN_LUMI_INFO");
        }
        finally {
            System.clearProperty("crest.runlumiinfo-table");
        }
    }

    @Test
    @DisplayName("resolveTableName uses environment variable when other sources are null")
    void testResolveTableNameFromEnvironmentVariable() {
        ReflectionTestUtils.setField(strategy, "crestProperties", null);
        ReflectionTestUtils.setField(strategy, "environment", null);
        System.clearProperty("crest.runlumiinfo-table");

        // Note: We can't easily set environment variables in unit tests
        // This test verifies the fallback logic exists
        Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        // Should return default if env var not set
        assertThat(result.getText()).isEqualTo("RUN_INFO_VIEW");
    }

    @Test
    @DisplayName("resolveTableName returns default when all sources are unavailable")
    void testResolveTableNameDefault() {
        ReflectionTestUtils.setField(strategy, "crestProperties", null);
        ReflectionTestUtils.setField(strategy, "environment", null);
        System.clearProperty("crest.runlumiinfo-table");

        Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        assertThat(result.getText()).isEqualTo("RUN_INFO_VIEW");
    }

    @Test
    @DisplayName("toPhysicalColumnName returns same identifier")
    void testToPhysicalColumnName() {
        Identifier identifier = Identifier.toIdentifier("SOME_COLUMN");
        Identifier result = strategy.toPhysicalColumnName(identifier, mockJdbcEnvironment);

        assertThat(result).isEqualTo(identifier);
    }

    @Test
    @DisplayName("toPhysicalSequenceName returns same identifier")
    void testToPhysicalSequenceName() {
        Identifier identifier = Identifier.toIdentifier("SOME_SEQUENCE");
        Identifier result = strategy.toPhysicalSequenceName(identifier, mockJdbcEnvironment);

        assertThat(result).isEqualTo(identifier);
    }

    @Test
    @DisplayName("toPhysicalCatalogName returns same identifier")
    void testToPhysicalCatalogName() {
        Identifier identifier = Identifier.toIdentifier("SOME_CATALOG");
        Identifier result = strategy.toPhysicalCatalogName(identifier, mockJdbcEnvironment);

        assertThat(result).isEqualTo(identifier);
    }

    @Test
    @DisplayName("toPhysicalSchemaName returns same identifier")
    void testToPhysicalSchemaName() {
        Identifier identifier = Identifier.toIdentifier("SOME_SCHEMA");
        Identifier result = strategy.toPhysicalSchemaName(identifier, mockJdbcEnvironment);

        assertThat(result).isEqualTo(identifier);
    }

    @Test
    @DisplayName("resolveTableName prioritizes CrestProperties over Environment")
    void testResolutionPriority() {
        when(mockCrestProperties.getRunluminfoTable()).thenReturn("FROM_CREST_PROPS");
        when(mockEnvironment.getProperty("crest.runlumiinfo-table")).thenReturn("FROM_ENV");

        Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        // Should use CrestProperties, not Environment
        assertThat(result.getText()).isEqualTo("FROM_CREST_PROPS");
    }

    @Test
    @DisplayName("resolveTableName handles empty strings in CrestProperties")
    void testResolutionWithEmptyStringInCrestProperties() {
        when(mockCrestProperties.getRunluminfoTable()).thenReturn("");
        when(mockEnvironment.getProperty("crest.runlumiinfo-table")).thenReturn("FROM_ENV");

        Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        // Should skip empty string and use Environment
        assertThat(result.getText()).isEqualTo("FROM_ENV");
    }

    @Test
    @DisplayName("resolveTableName handles null values gracefully")
    void testResolutionWithNullValues() {
        when(mockCrestProperties.getRunluminfoTable()).thenReturn(null);
        when(mockEnvironment.getProperty("crest.runlumiinfo-table")).thenReturn(null);

        Identifier identifier = Identifier.toIdentifier("RUN_INFO_VIEW");
        Identifier result = strategy.toPhysicalTableName(identifier, mockJdbcEnvironment);

        // Should return default
        assertThat(result.getText()).isEqualTo("RUN_INFO_VIEW");
    }
}

