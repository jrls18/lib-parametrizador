package br.com.group.developer.corporation.libparametrizador.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterizeKey implements Serializable {

    @Serial
    private static final long serialVersionUID = -8287119617527851614L;

    private Set<Properties> properties;

    private String[] filter;


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties implements Serializable{

        @Serial
        private static final long serialVersionUID = -5403877080003904036L;

        private String key;

        private Object defaultValue;
    }
}
