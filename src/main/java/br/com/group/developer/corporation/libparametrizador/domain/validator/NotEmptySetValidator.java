package br.com.group.developer.corporation.libparametrizador.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class NotEmptySetValidator implements ConstraintValidator<NotEmptySet, Set<?>> {

    @Override
    public boolean isValid(Set<?> value, ConstraintValidatorContext context) {
        return !Boolean.TRUE.equals(CollectionUtils.isEmpty(value));
    }
}
