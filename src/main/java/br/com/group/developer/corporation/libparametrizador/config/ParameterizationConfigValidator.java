package br.com.group.developer.corporation.libparametrizador.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParameterizationConfigValidator {
    private final ParameterizationProperties properties;

    @PostConstruct
    public void validateParameterizationProperties() {

        if (properties.getParameterize() == null ||
                properties.getParameterize().getParameters() == null) {
            throw new IllegalStateException("Parameterize configuration is missing");
        }

        properties.getParameterize()
                .getParameters()
                .forEach(p -> {

                    if (StringUtils.isBlank(p.getKey())) {
                        throw new IllegalStateException(
                                "Parameter key cannot be null or blank");
                    }

                    if (p.getDefaultValue() == null) {
                        throw new IllegalStateException(
                                "DefaultValue cannot be null for key: " + p.getKey());
                    }
                });
    }
}
