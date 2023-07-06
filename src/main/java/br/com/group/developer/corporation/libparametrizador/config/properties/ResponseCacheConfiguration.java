package br.com.group.developer.corporation.libparametrizador.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCacheConfiguration implements Serializable {
    private static final long serialVersionUID = -3953191866911282957L;

    private Set<String> fields;
}
