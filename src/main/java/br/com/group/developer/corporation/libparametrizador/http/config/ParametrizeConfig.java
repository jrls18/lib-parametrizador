package br.com.group.developer.corporation.libparametrizador.http.config;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ParametrizeConfig {
    public static final String URL;

    private final ConfigProperties configProperties;


    static {

        var environment = Optional.ofNullable(System.getenv("PARAMETRIZADOR_ENVIRONMENT"))
                .map(String::toLowerCase)
                .orElse("dev");

        //String url = "https://apps-comp-%s.teste.com.br/parametrize/configure/execute/";

        String url = "http://localhost:5001/service--parametrizador/configurator/v1/execute/";

        if("hml".equalsIgnoreCase(environment))
            URL = String.format(url, "hml");
        else if("prd".equalsIgnoreCase(environment))
            URL = String.format(url,"prd");
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
                .baseUrl("http://localhost:5001/service--parametrizador/configurator/v1/execute/")
                .clientConnector(connector)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("client_id",configProperties.getClientId())
                .defaultHeader("client_secret",configProperties.getClientSecret())
                .defaultHeader("correlation_id", UUID.randomUUID().toString())
                .build();
    }

    @Bean
    private static CircuitBreaker circuitBreaker(){
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .waitDurationInOpenState(Duration.ofSeconds(2))
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .permittedNumberOfCallsInHalfOpenState(5)
                .minimumNumberOfCalls(80)
                .recordExceptions(
                        HttpTimeoutException.class,
                        HttpConnectTimeoutException.class,
                        IllegalArgumentException.class,
                        SecurityException.class,
                        SSLHandshakeException.class,
                        SSLException.class,
                        ConnectException.class
                )
                .ignoreExceptions(InterruptedException.class)
                .build();

        return CircuitBreaker.of("parametrizador-cb", config);
    }

    @Bean
    private static Bulkhead bulkhead(){
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(20)
                .maxWaitDuration(Duration.ofMillis(500))
                .build();
        return Bulkhead.of("parametrizador-blk",config);
    }

    @Bean
    private static Retry retry(){
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(300))
                .retryExceptions(
                        ConnectException.class
                )
                .ignoreExceptions(HttpTimeoutException.class,
                        HttpConnectTimeoutException.class,
                        IllegalArgumentException.class,
                        SecurityException.class,
                        SSLHandshakeException.class,
                        SSLException.class
                )
                .failAfterMaxAttempts(true)
                .build();

        return Retry.of("parametrizador-retry", config);
    }

}
