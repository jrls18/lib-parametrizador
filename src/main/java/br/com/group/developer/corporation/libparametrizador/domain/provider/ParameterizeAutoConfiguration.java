package br.com.group.developer.corporation.libparametrizador.domain.provider;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.ParameterizeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(ParameterizeProvider.class)
@RequiredArgsConstructor
public class ParameterizeAutoConfiguration {

    private final ParameterizationProperties parameterizationProperties;

    @Bean
    public ParameterizeProvider parameterizeProvider() {
        return new ParameterizeClient(parameterizationProperties);
    }
}
