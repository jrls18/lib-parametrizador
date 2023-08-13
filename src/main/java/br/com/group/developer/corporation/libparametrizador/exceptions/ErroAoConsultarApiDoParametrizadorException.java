package br.com.group.developer.corporation.libparametrizador.exceptions;

public class ErroAoConsultarApiDoParametrizadorException extends RuntimeException{
    private static final long serialVersionUID = 2104130247871062121L;

    public ErroAoConsultarApiDoParametrizadorException(final String message){
        super(message);
    }
}
