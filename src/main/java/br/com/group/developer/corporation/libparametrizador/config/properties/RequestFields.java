package br.com.group.developer.corporation.libparametrizador.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestFields implements Serializable {
    private static final long serialVersionUID = -1958341906511275094L;

    private String key;

    private String value;
}
