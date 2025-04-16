package br.com.group.developer.corporation.libparametrizador.infrastructure.feign.adapter;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.infrastructure.feign.ParameterizeClient;
import br.com.group.developer.corporation.libparametrizador.infrastructure.feign.message.ParameterizeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParameterizeClientAdapter {

    private final ParameterizeClient parameterizeClient;

    private final ConfigProperties properties;

    public ParameterizeResponse getProperties() {
        return parameterizeClient.getProperties(properties.getClientId(),
                properties.getClientSecret(),
                properties.getApplicationName(),
                properties.getFilterMultipleKey());
    }

}
