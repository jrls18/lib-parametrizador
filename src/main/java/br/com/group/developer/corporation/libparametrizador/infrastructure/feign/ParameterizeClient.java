package br.com.group.developer.corporation.libparametrizador.infrastructure.feign;

import br.com.group.developer.corporation.libparametrizador.config.properties.FilterMultipleKey;
import br.com.group.developer.corporation.libparametrizador.infrastructure.feign.config.FeignLibConfig;
import br.com.group.developer.corporation.libparametrizador.infrastructure.feign.message.ParameterizeResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "parameterize-service", configuration = FeignLibConfig.class)
public interface ParameterizeClient {

    @Retry(name = "callApiGetParameterize")
    @PostMapping(value = "/chave/v1/filter/execute", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ParameterizeResponse getProperties(@RequestHeader("client_id") final String clientID,
                                       @RequestHeader("client_secret") final String clientSecret,
                                       @RequestHeader(value = "origin") final String applicationName,
                                       @RequestBody FilterMultipleKey filterMultipleChave
    );
}


