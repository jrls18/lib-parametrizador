package br.com.group.developer.corporation.libparametrizador.domain.validator;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequiredDefaultValueValidator implements ConstraintValidator<NotEmptyDefaultValue, String> {

    private final ParameterizationProperties properties;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!properties.isEnableContingencyConfigMap()) {
            return true;
        }

        return StringUtils.isNotBlank(value);
    }
}
