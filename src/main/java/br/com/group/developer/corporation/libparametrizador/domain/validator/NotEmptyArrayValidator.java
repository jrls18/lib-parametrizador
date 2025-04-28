package br.com.group.developer.corporation.libparametrizador.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyArrayValidator implements ConstraintValidator<NotEmptyArray,String[]> {
    @Override
    public boolean isValid(String[] value, ConstraintValidatorContext context) {
        if(value == null)
            return false;
        return value.length > 0;
    }
}
