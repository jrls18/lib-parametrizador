package br.com.group.developer.corporation.libparametrizador.infrastructure.feign.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterizeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -5914561908766047397L;

    private Map<String,Object> properties;
}
