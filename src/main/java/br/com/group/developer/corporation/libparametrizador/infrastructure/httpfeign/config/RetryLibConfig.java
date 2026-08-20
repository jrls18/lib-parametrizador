package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.config;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import br.com.group.developer.corporation.libparametrizador.exceptions.*;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryLibConfig {

    @Bean
    public Retry libRetryParameterize(ParameterizationProperties properties) {
        return Retry.of("callApiGetParameterize",
                RetryConfig.custom()
                        .maxAttempts(NumberUtils.toInt(properties.getMaxRetry(), 3))

                        .waitDuration(Duration.ofMillis(200))

                        // retry só em erros transitórios
                        .retryExceptions(
                                ServiceUnavailableLibException.class,
                                TimeOutLibException.class,
                                InternalServerErrorLibException.class
                        )

                        // nunca retry nesses
                        .ignoreExceptions(
                                NotAuthorizedLibException.class,
                                BadRequestLibException.class
                        )

                        .failAfterMaxAttempts(true)
                        .build()
        );
    }
}
