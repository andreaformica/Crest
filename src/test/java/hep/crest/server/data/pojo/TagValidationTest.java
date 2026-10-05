package hep.crest.server.data.pojo;

import hep.crest.server.data.pojo.Tag;
import hep.crest.server.swagger.model.TagDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Tag entity validation.
 * Tests that wildcard characters (* and %) are rejected in tag names.
 */
class TagValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testTagName_withValidName_shouldPassValidation() {
        // given
        Tag tag = new Tag()
                .setName("valid-tag-name")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void testTagName_withAsterisk_shouldFailValidation() {
        // given
        Tag tag = new Tag()
                .setName("invalid*tag")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Tag> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testTagName_withPercent_shouldFailValidation() {
        // given
        Tag tag = new Tag()
                .setName("invalid%tag")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Tag> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testTagName_withBothWildcards_shouldFailValidation() {
        // given
        Tag tag = new Tag()
                .setName("tag*with%wildcards")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Tag> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Tag name can only contain alphanumeric characters, hyphens, dots, and underscores");
    }

    @Test
    void testTagName_withSpecialCharactersExceptWildcards_shouldPassValidation() {
        // given - only hyphens and underscores are allowed as special characters
        Tag tag = new Tag()
                .setName("tag-name_v1")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void testTagName_withDot_shouldPassValidation() {
        // given - dots are now allowed for hierarchical naming (e.g., DETECTOR.PIXEL.IBL)
        Tag tag = new Tag()
                .setName("tag.name")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void testTagName_withAsteriskAtStart_shouldFailValidation() {
        // given
        Tag tag = new Tag()
                .setName("*tagname")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testTagName_withPercentAtEnd_shouldFailValidation() {
        // given
        Tag tag = new Tag()
                .setName("tagname%")
                .setTimeType("time")
                .setObjectType("json")
                .setSynchronization(TagDto.SynchronizationEnum.ALL.toString())
                .setDescription("Test tag")
                .setLastValidatedTime(BigInteger.ZERO)
                .setEndOfValidity(BigInteger.ZERO);

        // when
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }
}

