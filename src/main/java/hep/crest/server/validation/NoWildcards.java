package hep.crest.server.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to ensure a string contains only alphanumeric characters, hyphens, dots, and underscores.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoWildcardsValidator.class)
@Documented
public @interface NoWildcards {

    /**
     * Default message when validation fails.
     * @return the error message
     */
    String message() default "Field can only contain alphanumeric characters, hyphens, dots, and underscores";

    /**
     * Validation groups.
     * @return the groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload.
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};
}

