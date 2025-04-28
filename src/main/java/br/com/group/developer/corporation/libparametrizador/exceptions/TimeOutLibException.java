package br.com.group.developer.corporation.libparametrizador.exceptions;


import java.io.Serial;


public class TimeOutLibException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7315948963190169883L;

    public TimeOutLibException(final String message){
        super(message);
    }
}
