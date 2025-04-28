package br.com.group.developer.corporation.libparametrizador.config;

import br.com.group.developer.corporation.libparametrizador.config.properties.Parameterize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@Getter
@Setter
@Component
@Configuration
@ConfigurationProperties(value = "parameterization-properties", ignoreInvalidFields = true)
@Validated
public class ParameterizationProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 5494335064406159711L;

    @Pattern(regexp = "^([0-9]*)$", message = "O campo maxRetry deve conter apenas valores númericos")
    private String maxRetry = "3";

    @Pattern(regexp = "^([0-9]*)$", message = "O campo minutesTtl deve conter apenas valores númericos")
    private String minutesTtl = "5";

    private String url;

    private boolean enableContingencyConfigMap = false;

    @NotNull(message = "O campo clientId é obrigatório")
    @Pattern(regexp = "^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})$", message = "O campo clientId deve conter apenas valores do  tipo UUID")
    private String clientId;

    @NotNull(message = "O campo clientSecret é obrigatório")
    @Pattern(regexp = "^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})$", message = "O campo clientSecret deve conter apenas valores do tipo UUID")
    private String clientSecret;

    @NotNull(message = "O campo applicationName é obrigatório")
    private String applicationName;

    @NotNull(message = "O campo parameterize é obrigatório")
    private Parameterize parameterize;

    public String getUrl() {
        var environment = Optional.ofNullable(System.getenv("SPRING_PROFILES_ACTIVE"))
                .map(String::toLowerCase)
                .orElse("dev");

        String url = "http://cloud.%s.develop.corporation.com/service--parametrizador";

        return switch (environment.toUpperCase()){
            case "HML" ->  String.format(url, "hml");
            case "PRD" -> String.format(url,"prd");
            case "LOCAL" -> "http://localhost:5001/service--parametrizador";
            default -> String.format(url, "dev");
        };
    }
}
