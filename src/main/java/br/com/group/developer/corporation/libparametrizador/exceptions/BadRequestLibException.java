package br.com.group.developer.corporation.libparametrizador.exceptions;

import java.io.Serial;


public class BadRequestLibException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5113995341890780494L;

    public BadRequestLibException(final String payload){
        super(payload);
    }
}
