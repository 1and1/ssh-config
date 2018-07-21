package com.oneandone.sshconfig.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** IpAddress validation annotation for checking valid IP addresses.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Documented
@Constraint(validatedBy = IpAddressValidator.class)
public @interface IpAddress {
    /** The default message for a wrong IP address.
     * @return the default value.
     * */
    String message() default "IP address is invalid";
    /** The default groups.
     * @return the default value.
     * */
    Class<?>[] groups() default {};
    /** The default payload.
     * @return the default value.
     * */
    Class<? extends Payload>[] payload() default {};
}
