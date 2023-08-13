package br.com.group.developer.corporation.libparametrizador.exceptions;

public class NaoExisteParametroConfiguradoNoParametrizadorException extends RuntimeException{
    private static final long serialVersionUID = 2104130247871062121L;

    public NaoExisteParametroConfiguradoNoParametrizadorException(final String message){
        super(message);
    }
}
