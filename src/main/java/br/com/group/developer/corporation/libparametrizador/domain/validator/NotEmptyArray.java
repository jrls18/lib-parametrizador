package br.com.group.developer.corporation.libparametrizador.domain.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotEmptyArrayValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyArray {

    String message() default "O array não pode ser nulo ou vazio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
