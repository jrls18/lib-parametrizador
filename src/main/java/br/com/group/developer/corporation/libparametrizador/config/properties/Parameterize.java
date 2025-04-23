package br.com.group.developer.corporation.libparametrizador.config.properties;

import br.com.group.developer.corporation.libparametrizador.domain.validator.NotEmptyArray;
import br.com.group.developer.corporation.libparametrizador.domain.validator.NotEmptyDefaultValue;
import br.com.group.developer.corporation.libparametrizador.domain.validator.NotEmptySet;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Component
@Configuration
@ConfigurationProperties(value = "parameterization-properties.parameterize", ignoreInvalidFields = true)
@Validated
public class Parameterize implements Serializable {

    @Serial
    private static final long serialVersionUID = -8287119617527851614L;

    @NotEmptySet(message = "O campo 'parameters' é obrigatório")
    private Set<Parameter> parameters;

    @NotEmptyArray(message = "O campo 'filters' é obrigatório")
    private String[] filters;


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameter implements Serializable{

        @Serial
        private static final long serialVersionUID = -5403877080003904036L;

        @NotNull(message = "O campo key é obrigatório")
        private String key;

        @NotEmptyDefaultValue(message = "A properties 'enableContingencyConfigMap' está ativada então a propertie 'defaultValue' é obrigatória.")
        private Object defaultValue;
    }
}
