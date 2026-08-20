package br.com.group.developer.corporation.libparametrizador.domain.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequiredDefaultValueValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyDefaultValue {

    String message() default "defaultValue is mandatory when contingency mode is enabled";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
