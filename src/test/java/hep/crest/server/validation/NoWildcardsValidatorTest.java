package hep.crest.server.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for NoWildcardsValidator.
 * Tests validation of strings to ensure only alphanumeric, hyphen, and underscore characters are allowed.
 */
class NoWildcardsValidatorTest {

    private NoWildcardsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NoWildcardsValidator();
        validator.initialize(null);
    }

    @Test
    void testIsValid_withValidString_shouldReturnTrue() {
        // given
        String validName = "my-tag-name";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withAlphanumeric_shouldReturnTrue() {
        // given
        String validName = "TagName123";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withHyphens_shouldReturnTrue() {
        // given
        String validName = "tag-name-v1";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withUnderscores_shouldReturnTrue() {
        // given
        String validName = "tag_name_v1";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withMixedValid_shouldReturnTrue() {
        // given
        String validName = "Tag_Name-123";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withDot_shouldReturnTrue() {
        // given - dots are now allowed for hierarchical naming (e.g., DETECTOR.PIXEL.IBL)
        String validName = "tag.name";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withAsterisk_shouldReturnFalse() {
        // given
        String invalidName = "tag*name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withPercent_shouldReturnFalse() {
        // given
        String invalidName = "tag%name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withBothWildcards_shouldReturnFalse() {
        // given
        String invalidName = "tag*name%";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withSpace_shouldReturnFalse() {
        // given
        String invalidName = "tag name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withSlash_shouldReturnFalse() {
        // given
        String invalidName = "tag/name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withBackslash_shouldReturnFalse() {
        // given
        String invalidName = "tag\\name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withColon_shouldReturnFalse() {
        // given
        String invalidName = "tag:name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withSemicolon_shouldReturnFalse() {
        // given
        String invalidName = "tag;name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withAt_shouldReturnFalse() {
        // given
        String invalidName = "tag@name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withHash_shouldReturnFalse() {
        // given
        String invalidName = "tag#name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withDollar_shouldReturnFalse() {
        // given
        String invalidName = "tag$name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withParentheses_shouldReturnFalse() {
        // given
        String invalidName = "tag(name)";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withBrackets_shouldReturnFalse() {
        // given
        String invalidName = "tag[name]";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withNull_shouldReturnTrue() {
        // given - null is valid (use @NotNull separately if needed)
        String nullName = null;

        // when
        boolean result = validator.isValid(nullName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withEmptyString_shouldReturnTrue() {
        // given
        String emptyName = "";

        // when
        boolean result = validator.isValid(emptyName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withOnlyHyphens_shouldReturnTrue() {
        // given
        String validName = "---";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withOnlyUnderscores_shouldReturnTrue() {
        // given
        String validName = "___";

        // when
        boolean result = validator.isValid(validName, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testIsValid_withPlus_shouldReturnFalse() {
        // given
        String invalidName = "tag+name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testIsValid_withEquals_shouldReturnFalse() {
        // given
        String invalidName = "tag=name";

        // when
        boolean result = validator.isValid(invalidName, null);

        // then
        assertThat(result).isFalse();
    }
}

