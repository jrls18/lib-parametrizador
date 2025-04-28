package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.adapter;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.ParameterizeClient;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message.ParameterizeRequest;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message.ParameterizeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class ParameterizeClientAdapter {

    private final ParameterizeClient parameterizeClient;

    private final ParameterizationProperties properties;

    public ParameterizeResponse getProperties() {

        Set<String> param = new HashSet<>(properties.getParameterize().getFilters().length);

        properties.getParameterize().getParameters().forEach(items -> param.add(items.getKey()));

        var parameters = new ParameterizeRequest.Properties(param.toArray(new String[0]), properties.getParameterize().getFilters());

        return parameterizeClient.getProperties(new ParameterizeRequest(parameters));
    }

}
