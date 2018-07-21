package com.oneandone.sshconfig.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class IpAddressValidator implements ConstraintValidator<IpAddress, Object>
{
    @Override
    public void initialize(IpAddress constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintContext)
    {
        if (value instanceof String) {
            return isValid((String)value, constraintContext);
        }
        if (value instanceof String[]) {
            return isValid(Arrays.asList((String[])value), constraintContext);
        }
        if (value instanceof List) {
            return isValid((List<String>)value, constraintContext);
        }
        return true;
    }

    public boolean isValid(List<String> value, ConstraintValidatorContext constraintContext)
    {
        return !value
                .stream()
                .map(val -> isValid(val, constraintContext))
                .filter(r -> r == false)
                .findFirst()
                .isPresent();
    }

    public boolean isValid(String value, ConstraintValidatorContext constraintContext)
    {
        boolean result = false;
        try {
            InetAddress.getByName(value);
            result = true;
        } catch (UnknownHostException e) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(e.getMessage());
        }
        return result;
    }
}
