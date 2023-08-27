package br.com.group.developer.corporation.libparametrizador.config;


import br.com.group.developer.corporation.libparametrizador.config.properties.MockPropertiesFields;
import br.com.group.developer.corporation.libparametrizador.config.properties.Property;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "parameterize", ignoreInvalidFields = true)
public class ConfigProperties implements Serializable {
    private static final long serialVersionUID = 7857030974398301533L;

    private String clientId;

    private String clientSecret;

    private String applicationName;

    private String urlBase;

    private Boolean isMock = false;

    private boolean validaParametrizadorEstaUp = false;

    private MockPropertiesFields propertiesMock;

    private Set<Property> properties;

}
