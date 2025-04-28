package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import br.com.group.developer.corporation.libparametrizador.exceptions.*;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message.ParameterizeRequest;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message.ParameterizeResponse;
import br.com.grupo.developer.corporation.lib.spring.context.holder.infrastructure.ContextHolder;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ParameterizeClient {

    private final Logger logger = LoggerFactory.getLogger(ParameterizeClient.class);


    private final ParameterizationProperties properties;

    private static final String ENDPOINT = "/chave/v1/filter/execute";

    @Retry(name = "callApiGetParameterize")
    public ParameterizeResponse getProperties(final ParameterizeRequest request) {

        return WebClient.builder()
                .filter(errorResponse())
                .baseUrl(getUrl())
                .build().post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("client_id", ContextHolder.get().getClientId())
                .header("client_secret", ContextHolder.get().getClientSecret())
                .header("correlation_id", ContextHolder.get().getCorrelationId())
                .header("origin", properties.getApplicationName())
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(ParameterizeResponse.class)
                .block();
    }

    private ExchangeFilterFunction errorResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {

            if (clientResponse.statusCode().is4xxClientError()) {
                switch (clientResponse.statusCode().value()) {
                    case 401, 403 -> handleUnauthorized();
                    case 408 -> handleTimeOut();
                    default -> clientResponse.bodyToMono(String.class)
                            .flatMap(payload -> {
                                logger.warn("BAD_REQUEST, ERRO DE NEGÓCIO DETALHES {} ", payload);
                                return Mono.error(new BadRequestLibException(payload));
                            });
                }
            } else if (clientResponse.statusCode().is5xxServerError()) {

                if (clientResponse.statusCode().value() == 503) {
                    handleServiceUnavailable();
                } else {
                    Mono.error(new InternalServerErrorLibException("INTERNAL_SERVER_ERROR, FALHA AO CHAMAR A API URL: ".concat(getUrl())));
                }
            }

            return Mono.just(clientResponse);
        });
    }

    private String getUrl() {
        return properties.getUrl().concat(ENDPOINT);
    }

    private void handleServiceUnavailable() {
        logger.warn("SERVICE_UNAVAILABLE, FALHA AO CHAMAR A API URL: {}", getUrl());
        throw new ServiceUnavailableLibException("SERVICE UNAVAILABLE, FALHA AO CHAMAR A API URL: " + getUrl());
    }

    private void handleUnauthorized() {
        logger.warn("UNAUTHORIZED, FALHA AO CHAMAR A API URL: {}", getUrl());
        throw new NotAuthorizedLibException("UNAUTHORIZED, FALHA AO CHAMAR A API URL: ".concat(getUrl()));
    }

    private void handleTimeOut() {
        logger.warn("TIME_OUT, FALHA AO CHAMAR A API URL: {} ", getUrl());
        throw new TimeOutLibException("TIMEOUT, FALHA AO CHAMAR A API URL: ".concat(getUrl()));
    }

}
