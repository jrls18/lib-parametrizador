package br.com.group.developer.corporation.libparametrizador.schedule;


import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.exceptions.BadRequestParameterizeException;
import br.com.group.developer.corporation.libparametrizador.exceptions.InternalServerErrorParameterizeException;
import br.com.grupo.developer.corporation.libcommons.message.response.MessageResponse;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class CallApiServiceParametrizadorService {

    private final ConfigProperties configProperties;
    public static final String URL;

    static {

        var environment = Optional.ofNullable(System.getenv("SPRING_PROFILES_ACTIVE"))
                .map(String::toLowerCase)
                .orElse("dev");

        String url = "http://cloud.%s.develop.corporation.com/service--parametrizador/configurator/v1/execute/";

        if("hml".equalsIgnoreCase(environment))
            URL = String.format(url, "hml");
        else if("prd".equalsIgnoreCase(environment))
            URL = String.format(url,"prd");
        else if("local".equalsIgnoreCase(environment))
            URL = "http://localhost:5001/service--parametrizador/configurator/v1/execute/";
        else
            URL = String.format(url, "dev");
    }

    public Map<String, Object> getParameters(final String propertiesName,final Map<String, Object> map){

        return WebClient.builder()
                .filter(errorResponse())
                .baseUrl( StringUtils.isEmpty(configProperties.getUrlBase()) ? URL : configProperties.getUrlBase().concat("/configurator/v1/execute/"))
                .build().post().uri(propertiesName)
                .contentType(MediaType.APPLICATION_JSON)
                .header("client_id",configProperties.getClientId())
                .header("client_secret",configProperties.getClientSecret())
                .header("correlation_id", UUID.randomUUID().toString())
                .header("origin", configProperties.getApplicationName())
                .body(BodyInserters.fromValue(map))
                .retrieve()
                .bodyToMono(Map.class)
                .retryWhen(Retry.max(3).filter(reponse -> reponse instanceof InternalServerErrorParameterizeException))
                .block();
    }

    private ExchangeFilterFunction errorResponse(){
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {

            if(clientResponse.statusCode().is4xxClientError()){
                return clientResponse.bodyToMono(MessageResponse.class)
                        .flatMap(body -> {
                            return Mono.error(new BadRequestParameterizeException(body));
                        });
            }else if(clientResponse.statusCode().is5xxServerError()){
                return clientResponse.bodyToMono(String.class)
                        .flatMap(body -> {
                            return Mono.error(new InternalServerErrorParameterizeException("Erro ao realizar a chamada da api do parametrizador."));
                        });
            }

            return Mono.just(clientResponse);
        });
    }

}
