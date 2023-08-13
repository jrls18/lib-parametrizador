package br.com.group.developer.corporation.libparametrizador.schedule;


import br.com.group.developer.corporation.libparametrizador.exceptions.InternalServerErrorParameterizeException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.util.Map;

@Service
@RequiredArgsConstructor
class CallApiServiceParametrizadorService {

    private final WebClient webClient;

    public Map<String, Object> getParameters(final String propertiesName,final Map<String, Object> map){
       return webClient
                .post()
                .uri(propertiesName)
                .body(BodyInserters.fromValue(map))
                .retrieve()
                .bodyToMono(Map.class)
                .retryWhen(Retry.max(3).filter(reponse -> reponse instanceof InternalServerErrorParameterizeException))
                .block();
    }
}
