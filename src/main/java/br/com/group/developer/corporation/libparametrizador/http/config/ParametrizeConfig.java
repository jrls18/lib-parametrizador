package br.com.group.developer.corporation.libparametrizador.http.config;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.exceptions.BadRequestParameterizeException;
import br.com.group.developer.corporation.libparametrizador.exceptions.InternalServerErrorParameterizeException;
import br.com.grupo.developer.corporation.libcommons.message.response.MessageResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Optional;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ParametrizeConfig {
    public static final String URL;

    private final ConfigProperties configProperties;

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

    @Bean
    public WebClient getWebClient()
    {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(10))
                                        .addHandlerLast(new WriteTimeoutHandler(10))));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl(URL)
                .clientConnector(connector)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("client_id",configProperties.getClientId())
                .defaultHeader("client_secret",configProperties.getClientSecret())
                .defaultHeader("correlation_id", UUID.randomUUID().toString())
                .defaultHeader("origin", configProperties.getApplicationName())
                .filter(errorResponse())
                .build();
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
