package br.com.group.developer.corporation.libparametrizador.exceptions;

import br.com.grupo.developer.corporation.libcommons.message.response.MessageResponse;
import lombok.Getter;
import lombok.Setter;

public class BadRequestParameterizeException extends RuntimeException {
    private static final long serialVersionUID = -4348584238685073190L;

    @Getter
    @Setter
    private MessageResponse messageResponse;

    public BadRequestParameterizeException(final MessageResponse messageResponse){
        this.messageResponse = messageResponse;
    }

}
