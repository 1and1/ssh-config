package com.oneandone.sshconfig.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/** The validator for the IpAddress itself. */
public final class IpAddressValidator
        implements ConstraintValidator<IpAddress, Object> {
    @Override
    public void initialize(final IpAddress constraintAnnotation) {
        // ignore constraint annotation for now
    }

    @Override
    public boolean isValid(
            final Object value,
            final ConstraintValidatorContext constraintContext) {
        if (value instanceof String) {
            return isValid((String) value, constraintContext);
        }
        if (value instanceof String[]) {
            return isValid(Arrays.asList((String[]) value), constraintContext);
        }
        if (value instanceof List) {
            return isValid((List<String>) value, constraintContext);
        }
        return true;
    }

    /** Check whether address list is valid.
     * @param value the list of addresses.
     * @param constraintContext the context to write responses to.
     * @return {@code true} if valid, {@code false} if invalid.
     * */
    public boolean isValid(
            final List<String> value,
            final ConstraintValidatorContext constraintContext) {
        return !value
                .stream()
                .map(val -> isValid(val, constraintContext))
                .filter(r -> !r)
                .findFirst()
                .isPresent();
    }

    /** Check whether address list is valid.
     * @param value the address as a String.
     * @param constraintContext the context to write responses to.
     * @return {@code true} if valid, {@code false} if invalid.
     * */
    public boolean isValid(
            final String value,
            final ConstraintValidatorContext constraintContext) {
        boolean result = false;
        try {
            InetAddress.getByName(value);
            result = true;
        } catch (UnknownHostException e) {
            constraintContext.buildConstraintViolationWithTemplate(
                    e.getMessage());
        }
        return result;
    }
}
