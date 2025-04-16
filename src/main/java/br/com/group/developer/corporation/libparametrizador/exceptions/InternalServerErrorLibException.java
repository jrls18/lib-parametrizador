package br.com.group.developer.corporation.libparametrizador.exceptions;

import java.io.Serial;

public class InternalServerErrorLibException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 5457248049795130426L;

    public InternalServerErrorLibException(final String message){
        super(message);
    }
}
