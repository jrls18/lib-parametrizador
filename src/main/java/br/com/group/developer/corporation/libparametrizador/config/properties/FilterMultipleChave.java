package br.com.group.developer.corporation.libparametrizador.config.properties;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FilterMultipleChave implements Serializable {

    private static final long serialVersionUID = 8313052404429460189L;

    private FilterMultipleChave.Properties properties;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties implements Serializable{

        private static final long serialVersionUID = -5403877080003904036L;

        private String[] key;

        private String[] filter;
    }
}
