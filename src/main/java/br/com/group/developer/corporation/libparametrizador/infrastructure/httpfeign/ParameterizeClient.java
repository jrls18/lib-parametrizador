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
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class ParameterizeClient implements ParameterizeProvider {

    private final Logger logger = LoggerFactory.getLogger(ParameterizeClient.class);

    private static final String GENERIC_URL = "/service--platform/api/parameterizations/internal/v1/properties";
    private static final String URL;

    private final ParameterizationProperties properties;

    static {

        var environment = Optional.ofNullable(System.getenv("SPRING_PROFILES_ACTIVE"))
                .map(String::toLowerCase)
                .orElse("dev");

        String url = "http://cloud.%s.develop.corporation.com".concat(GENERIC_URL);

        if("hml".equalsIgnoreCase(environment))
            URL = String.format(url, "hml");
        else if("prd".equalsIgnoreCase(environment))
            URL = String.format(url,"prd");
        else if("local".equalsIgnoreCase(environment))
            URL = "http://localhost:5002".concat(GENERIC_URL);
        else
            URL = String.format(url, "dev");
    }

    private String uriCustom() {

        if (StringUtils.isBlank(properties.getUriBase())
                || StringUtils.isBlank(properties.getPort())) {
            return URL;
        }

        final String uriBase = properties.getUriBase();

        if (uriBase.startsWith("http://") || uriBase.startsWith("https://")) {
            return String.format(
                    "%s:%s%s",
                    uriBase,
                    properties.getPort(),
                    GENERIC_URL
            );
        }

        final String protocol = properties.isEnabledHttps()
                ? "https"
                : "http";

        return String.format(
                "%s://%s:%s%s",
                protocol,
                uriBase,
                properties.getPort(),
                GENERIC_URL
        );
    }


    @Override
    @Retry(name = "callApiGetParameterize")
    public ParameterizeDetails getProperties() {

        Set<String> param = new HashSet<>(properties.getParameterize().getFilters().length);

        properties.getParameterize().getParameters().forEach(items -> param.add(items.getKey()));

        var parameters = new Parameterize.Properties(param.toArray(new String[0]), properties.getParameterize().getFilters());

        return WebClient.builder()
                .filter(errorResponse())
                .baseUrl(uriCustom())
                .build().post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("client_id", properties.getClientId())
                .header("client_secret", properties.getClientSecret())
                .header("correlation_id", ContextHolder.get().getCorrelationId())
                .header("requestingApplication ", properties.getApplicationName())
                .body(BodyInserters.fromValue(new Parameterize(parameters)))
                .retrieve()
                .bodyToMono(ParameterizeDetails.class)
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
                    Mono.error(new InternalServerErrorLibException("INTERNAL_SERVER_ERROR, FALHA AO CHAMAR A API URL: ".concat(uriCustom())));
                }
            }

            return Mono.just(clientResponse);
        });
    }


    private void handleServiceUnavailable() {
        logger.warn("SERVICE_UNAVAILABLE, FALHA AO CHAMAR A API URL: {}", uriCustom());
        throw new ServiceUnavailableLibException("SERVICE UNAVAILABLE, FALHA AO CHAMAR A API URL: " + uriCustom());
    }

    private void handleUnauthorized() {
        logger.warn("UNAUTHORIZED, FALHA AO CHAMAR A API URL: {}", uriCustom());
        throw new NotAuthorizedLibException("UNAUTHORIZED, FALHA AO CHAMAR A API URL: ".concat(uriCustom()));
    }

    private void handleTimeOut() {
        logger.warn("TIME_OUT, FALHA AO CHAMAR A API URL: {} ", uriCustom());
        throw new TimeOutLibException("TIMEOUT, FALHA AO CHAMAR A API URL: ".concat(uriCustom()));
    }

}
