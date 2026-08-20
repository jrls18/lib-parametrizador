package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient parameterizeWebClient() {
        return WebClient.builder()
                .build();
    }
}
