package br.com.group.developer.corporation.libparametrizador.http.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParametrizadorService {

    private final WebClient webClient;

    public Map<String, Object> getParameters(final String propertiesName,final Map<String, Object> map){

       return webClient
                .post()
                .uri(propertiesName)
                .body(BodyInserters.fromValue(map))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
