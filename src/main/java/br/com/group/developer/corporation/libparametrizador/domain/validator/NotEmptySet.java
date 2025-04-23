package br.com.group.developer.corporation.libparametrizador.domain.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotEmptySetValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptySet {

    String message() default "O conjunto não pode ser nulo ou vazio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
