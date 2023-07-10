package br.com.group.developer.corporation.libparametrizador.exceptions;


public class NaoExisteMockConfiguradoException extends Exception{
    private static final long serialVersionUID = 7659806411762844128L;

    public NaoExisteMockConfiguradoException(final String message){
        super(message);
    }

}
