package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public record ParameterizeDetails(
        @JsonProperty("properties")
        Map<String, Object> properties) implements Serializable {

    @Serial
    private static final long serialVersionUID = -5914561908766047397L;
}
