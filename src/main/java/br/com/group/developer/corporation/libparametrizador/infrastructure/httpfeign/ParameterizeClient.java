package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import br.com.group.developer.corporation.libparametrizador.domain.provider.ParameterizeProvider;
import br.com.group.developer.corporation.libparametrizador.exceptions.*;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message.Parameterize;
import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message.ParameterizeDetails;
import br.com.grupo.developer.corporation.lib.spring.context.holder.infrastructure.ContextHolder;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ParameterizeClient implements ParameterizeProvider {

    private final Logger logger = LoggerFactory.getLogger(ParameterizeClient.class);

    private static final String DEFAULT_HOST =
            "http://service--platform.platform.svc.cluster.local";

    private static final String ENDPOINT =
            "/service--platform/api/parameterizations/internal/v1/properties";

    private final ParameterizationProperties properties;
    private final WebClient webClient;

    // -------------------------
    // URL resolver (cluster/local)
    // -------------------------
    private String uriCustom() {

        String baseUrl = StringUtils.removeEnd(
                StringUtils.defaultIfBlank(
                        properties.getUriBase(),
                        DEFAULT_HOST
                ),
                "/"
        );

        return baseUrl + ENDPOINT;
    }

    // -------------------------
    // API CALL
    // -------------------------
    @Override
    @Retry(name = "callApiGetParameterize")
    public ParameterizeDetails getProperties() {

        Set<String> param = new HashSet<>(properties.getParameterize().getFilters().length);

        properties.getParameterize()
                .getParameters()
                .forEach(items -> param.add(items.getKey()));

        var parameters = new Parameterize.Properties(
                param.toArray(new String[0]),
                properties.getParameterize().getFilters()
        );

        return webClient
                .post()
                .uri(uriCustom())
                .contentType(MediaType.APPLICATION_JSON)
                .header("client_id", properties.getClientId())
                .header("client_secret", properties.getClientSecret())
                .header("correlation_id", ContextHolder.get().getCorrelationId())
                .header("requestingApplication", properties.getApplicationName())
                .body(BodyInserters.fromValue(new Parameterize(parameters)))
                .exchangeToMono(response -> {

                    int status = response.statusCode().value();

                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(ParameterizeDetails.class);
                    }

                    return response.bodyToMono(String.class)
                            .flatMap(body -> {

                                logger.error(
                                        "ERROR calling parameterize API. status={}, body={}",
                                        status,
                                        body
                                );

                                return switch (status) {

                                    case 401, 403 ->
                                            Mono.error(new NotAuthorizedLibException("UNAUTHORIZED: " + uriCustom()));

                                    case 408 ->
                                            Mono.error(new TimeOutLibException("TIMEOUT: " + uriCustom()));

                                    case 503 ->
                                            Mono.error(new ServiceUnavailableLibException("SERVICE_UNAVAILABLE: " + uriCustom()));

                                    case 500 ->
                                            Mono.error(new InternalServerErrorLibException("INTERNAL_SERVER_ERROR: " + uriCustom()));

                                    default ->
                                            Mono.error(new BadRequestLibException(body));
                                };
                            });
                })
                .block();
    }

}
