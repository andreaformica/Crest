package hep.crest.server.repositories.triggerdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for NoTriggerDb.
 * Verifies that the default implementation throws UnsupportedOperationException
 * when trigger DB is not configured.
 */
class NoTriggerDbTest {

    private NoTriggerDb noTriggerDb;

    @BeforeEach
    void setUp() {
        noTriggerDb = new NoTriggerDb();
    }

    @Test
    void testGetTriggerDBData_shouldThrowUnsupportedOperationException() {
        // given
        UrlComponents components = new UrlComponents("testSchema", "testTable", 123L);

        // when & then
        assertThatThrownBy(() -> noTriggerDb.getTriggerDBData(components))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("No trigger DB is configured.");
    }

    @Test
    void testGetTriggerDBData_withNullComponents_shouldThrowUnsupportedOperationException() {
        // when & then
        assertThatThrownBy(() -> noTriggerDb.getTriggerDBData(null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("No trigger DB is configured.");
    }

    @Test
    void testParseUrl_shouldThrowUnsupportedOperationException() {
        // given
        String url = "some/url/path";

        // when & then
        assertThatThrownBy(() -> noTriggerDb.parseUrl(url))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("No trigger DB is configured.");
    }

    @Test
    void testParseUrl_withNullUrl_shouldThrowUnsupportedOperationException() {
        // when & then
        assertThatThrownBy(() -> noTriggerDb.parseUrl(null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("No trigger DB is configured.");
    }

    @Test
    void testParseUrl_withEmptyUrl_shouldThrowUnsupportedOperationException() {
        // when & then
        assertThatThrownBy(() -> noTriggerDb.parseUrl(""))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("No trigger DB is configured.");
    }
}

