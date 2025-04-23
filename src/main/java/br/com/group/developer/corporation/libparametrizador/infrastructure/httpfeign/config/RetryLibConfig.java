package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.config;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import br.com.group.developer.corporation.libparametrizador.exceptions.*;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryLibConfig {

    @Bean
    public Retry libRetryParameterize(ParameterizationProperties properties) {
        return Retry.of("callApiGetParameterize",
                RetryConfig.custom().maxAttempts(Integer.parseInt(properties.getMaxRetry()))
                        .retryExceptions(ServiceUnavailableLibException.class,
                                TimeOutLibException.class,
                                InternalServerErrorLibException.class)
                        .ignoreExceptions(NotAuthorizedLibException.class,
                                BadRequestLibException.class)
                        .failAfterMaxAttempts(true)
                        .build());
    }
}
