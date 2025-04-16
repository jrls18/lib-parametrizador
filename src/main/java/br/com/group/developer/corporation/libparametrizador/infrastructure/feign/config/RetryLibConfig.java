package br.com.group.developer.corporation.libparametrizador.infrastructure.feign.config;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.exceptions.BadRequestLibException;
import br.com.group.developer.corporation.libparametrizador.exceptions.InternalServerErrorLibException;
import br.com.group.developer.corporation.libparametrizador.exceptions.NotAuthorizedLibException;
import br.com.group.developer.corporation.libparametrizador.exceptions.ServiceUnavailableLibException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeoutException;

@Configuration
public class RetryLibConfig {

    @Bean
    public Retry libRetryParameterize(ConfigProperties properties) {
        return Retry.of("callApiGetParameterize",
                RetryConfig.custom().maxAttempts(Integer.parseInt(properties.getMaxRetry()))
                        .retryExceptions(ServiceUnavailableLibException.class,
                                TimeoutException.class,
                                InternalServerErrorLibException.class)
                        .ignoreExceptions(NotAuthorizedLibException.class,
                                BadRequestLibException.class)
                        .failAfterMaxAttempts(true)
                        .build());
    }
}
