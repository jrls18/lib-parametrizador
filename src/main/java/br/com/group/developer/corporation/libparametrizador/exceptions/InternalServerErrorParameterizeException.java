package br.com.group.developer.corporation.libparametrizador.exceptions;

public class InternalServerErrorParameterizeException extends Exception{
    private static final long serialVersionUID = 2104130247871062121L;

    public InternalServerErrorParameterizeException(final String message){
        super(message);
    }
}
