package br.com.group.developer.corporation.libparametrizador.http.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response implements Serializable {
    private static final long serialVersionUID = 1932692629871641548L;

    private String codigo;
    private String nome;
    private Map<String,Object> properties;
}
