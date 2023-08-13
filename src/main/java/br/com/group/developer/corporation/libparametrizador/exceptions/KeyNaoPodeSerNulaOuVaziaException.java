package br.com.group.developer.corporation.libparametrizador.exceptions;

public class KeyNaoPodeSerNulaOuVaziaException extends RuntimeException{
    private static final long serialVersionUID = 2104130247871062121L;

    public KeyNaoPodeSerNulaOuVaziaException(final String message){
        super(message);
    }
}
