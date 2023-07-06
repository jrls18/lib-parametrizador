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
public class Property implements Serializable {
    private static final long serialVersionUID = -4267155262109239617L;

    private String name;

    private Set<RequestFields> requestFields;

    private ResponseCacheConfiguration fieldCaching;
}
