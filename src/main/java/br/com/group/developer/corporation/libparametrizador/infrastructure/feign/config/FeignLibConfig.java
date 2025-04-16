package br.com.group.developer.corporation.libparametrizador.infrastructure.feign.config;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.exceptions.*;
import br.com.grupo.developer.corporation.lib.spring.context.holder.infrastructure.ContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@EnableFeignClients(basePackages = "br.com.group.developer.corporation.libparametrizador.infrastructure.feign")
@Configuration
public class FeignLibConfig {

    private final Logger logger = LoggerFactory.getLogger(FeignLibConfig.class);

    private static final String X_REQUEST_START_TIME = "X-Request-Start-Time";

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecode();
    }

    @Bean
    public RequestInterceptor requestInterceptor(ConfigProperties configProperties) {
        return requestTemplate -> {
            if (Boolean.FALSE.equals(CollectionUtils.isEmpty(ContextHolder.get().getRequestUriHeaders()))) {
                for (Map.Entry<String, String> entry : ContextHolder.get().getRequestUriHeaders().entrySet()) {
                    requestTemplate.header(entry.getKey(), entry.getValue());
                }
            }
            requestTemplate.target(configProperties.getUrl());
            requestTemplate.header(X_REQUEST_START_TIME, String.valueOf(System.nanoTime()));
        };
    }

    public class CustomErrorDecode implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            return switch (response.status()) {
                case 401, 403 -> handleUnauthorized(response);
                case 408 -> handleTimeOut(response);
                case 400, 404, 422 -> handleClientErros(response);
                case 503 -> handleServiceUnavailable(response);
                default -> handleInternalServerError(response);
            };
        }

        private Exception handleInternalServerError(Response response) {
            logger.error("INTERNAL_SERVER_ERROR, FALHA AO CHAMAR A API URL: {}", response.request().url());
            return new InternalServerErrorLibException("INTERNAL_SERVER_ERROR, FALHA AO CHAMAR A API URL: " + response.request().url());
        }

        private Exception handleServiceUnavailable(Response response) {
            logger.warn("SERVICE_UNAVAILABLE, FALHA AO CHAMAR A API URL: {}", response.request().url());
            return new ServiceUnavailableLibException("SERVICE UNAVAILABLE, FALHA AO CHAMAR A API URL: " + response.request().url());
        }

        private Exception handleClientErros(Response response) {
            final String result = getValueResponse(response);
            logger.warn("BAD_REQUEST, ERRO DE NEGÓCIO DETALHES {} ", result);
            return new BadRequestLibException(result);
        }

        private Exception handleTimeOut(Response response) {
            logger.warn("TIME_OUT, FALHA AO CHAMAR A API URL: {} ", response.request().url());
            return new TimeOutLibException("TIMEOUT, FALHA AO CHAMAR A API URL: "+ response.request().url());
        }

        private Exception handleUnauthorized(Response response) {
            logger.warn("UNAUTHORIZED, FALHA AO CHAMAR A API URL: {}", response.request().url());
            return new NotAuthorizedLibException("UNAUTHORIZED, FALHA AO CHAMAR A API URL: " + response.request().url());
        }

        private String getValueResponse(Response response) {

            if (Objects.isNull(response))
                return StringUtils.EMPTY;

            try {
                return IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new InternalServerErrorLibException("Falha ao realizar a conversão da resposta da API: " + response.request().url());
            }
        }
    }

    public class ResponseInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {

            String startTimeHeader = template.request().headers().get(X_REQUEST_START_TIME).iterator().next();

            long startTime = Long.parseLong(startTimeHeader);
            long duration = System.nanoTime() - startTime;

            String url = template.request().url();

            logger.info("TEMPO_CHAMADA_API, URL:  {} DURAÇÂO: {}", url, duration );
        }
    }
}
