package hep.crest.server.data.pojo;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for GlobalTag entity validation.
 * Tests that wildcard characters (* and %) are rejected in global tag names.
 */
class GlobalTagValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGlobalTagName_withValidName_shouldPassValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("valid-global-tag-name")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void testGlobalTagName_withAsterisk_shouldFailValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("invalid*global-tag")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<GlobalTag> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testGlobalTagName_withPercent_shouldFailValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("invalid%global-tag")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<GlobalTag> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testGlobalTagName_withBothWildcards_shouldFailValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("global*tag%with-wildcards")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<GlobalTag> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("GlobalTag name can only contain alphanumeric characters, hyphens, and underscores");
    }

    @Test
    void testGlobalTagName_withHyphensAndUnderscores_shouldPassValidation() {
        // given - only hyphens and underscores are allowed as special characters
        GlobalTag globalTag = new GlobalTag()
                .setName("global-tag_v1")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void testGlobalTagName_withDot_shouldFailValidation() {
        // given - dots are no longer allowed
        GlobalTag globalTag = new GlobalTag()
                .setName("global.tag")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testGlobalTagName_withAsteriskAtStart_shouldFailValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("*globaltag")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testGlobalTagName_withPercentAtEnd_shouldFailValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("globaltag%")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.TEST.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testGlobalTagName_withAlphanumeric_shouldPassValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("GlobalTag123")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.BULK.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void testGlobalTagName_withMultipleAsterisks_shouldFailValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("global**tag")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.MC.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void testGlobalTagName_withMultiplePercents_shouldFailValidation() {
        // given
        GlobalTag globalTag = new GlobalTag()
                .setName("global%%tag")
                .setValidity(BigDecimal.valueOf(1000))
                .setDescription("Test global tag")
                .setRelease("Release-1.0")
                .setWorkflow(GlobalTagWorkflowEnum.HLT.getCode())
                .setType('t');

        // when
        Set<ConstraintViolation<GlobalTag>> violations = validator.validate(globalTag);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }
}

