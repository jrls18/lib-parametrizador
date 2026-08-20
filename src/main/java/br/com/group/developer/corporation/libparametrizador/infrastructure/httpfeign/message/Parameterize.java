package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message;

import java.io.Serial;
import java.io.Serializable;

public record Parameterize(Properties properties) implements Serializable {

    @Serial
    private static final long serialVersionUID = -133852230117213931L;

   public record Properties (String[] key,
                       String[] filter
                       ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 2197987066960874244L;
    }
}
