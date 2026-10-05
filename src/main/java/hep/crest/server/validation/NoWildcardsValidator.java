package hep.crest.server.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the NoWildcards annotation.
 * Checks that a string contains only alphanumeric characters, hyphens, and underscores.
 * Rejects any special characters including wildcards (* and %) and other symbols.
 */
public class NoWildcardsValidator implements ConstraintValidator<NoWildcards, String> {

    @Override
    public void initialize(NoWildcards constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are considered valid (use @NotNull separately if needed)
        if (value == null) {
            return true;
        }

        // Check if the string contains only alphanumeric, hyphen, underscore, or dot
        // Pattern: ^[a-zA-Z0-9_.-]+$
        // Allows: letters, digits, hyphens (-), underscores (_), and dots (.)
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '-' && c != '_' && c != '.') {
                return false;
            }
        }

        return true;
    }
}

