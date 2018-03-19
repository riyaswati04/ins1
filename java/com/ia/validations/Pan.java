package com.ia.validations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.ia.validators.PanValidator;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = PanValidator.class)
public @interface Pan {

    Class<?>[] groups() default {};

    String message() default "invalid Pan Number.";

    Class<? extends Payload>[] payload() default {};

}
