package com.oneandone.sshconfig.validation;


import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;

/**
 * Validates an object tree using Bean validation.
 *
 * @author Stephan Fuhrmann
 */
@Slf4j
public class ValidationDelegate {

    /**
     * Bean validator to use.
     */
    private final Validator validator;

    /**
     * Creates a new instance.
     */
    public ValidationDelegate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /** Formats a violation to a String.
     * @param u the violation to format.
     * @return the resulting String.
     * @param <T> the objects class being formatted.
     * */
    private <T> String format(final ConstraintViolation<T> u) {
        return String.format("Validation error for %s %s. Value: %s.",
                u.getPropertyPath().toString(),
                u.getMessage(),
                u.getInvalidValue());
    }

    /**
     * Verifies the given object. Throws an exception in case of a
     * validation error.
     *
     * @param <T> the class of the object to check and the constraint
     *            violations.
     * @param o   the object to validate.
     * @throws ValidationException if validation returns an error.
     */
    public final <T> void verify(final T o) throws ValidationException {
        final Set<ConstraintViolation<T>> violations = validate(o);
        final Optional<ConstraintViolation<T>> first =
                violations
                        .stream()
                        .findFirst();
        if (first.isPresent()) {
            throw new ValidationException(format(first.get()));
        }
    }

    /**
     * Validates the given object. Logs all constraint violations.
     *
     * @param <T> the class of the object to check and the constraint
     *            violations.
     * @param o   the object to validate.
     * @return the set of constraint violations detected.
     */
    public final <T> Set<ConstraintViolation<T>> validate(final T o) {
        final Set<ConstraintViolation<T>> violations = validator.validate(o);
        if (violations.size() > 0) {
            log.error("Got {} validation errors", violations.size());
            violations.forEach(u -> {
                log.error(format(u));
            });
            log.error("Got {} validation errors", violations.size());
        } else {
            log.debug("Object validated");
        }
        return violations;
    }
}
