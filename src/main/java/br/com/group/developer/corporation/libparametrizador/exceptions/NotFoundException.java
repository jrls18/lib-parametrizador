package br.com.group.developer.corporation.libparametrizador.exceptions;


public class NotFoundException extends Throwable{
    private static final long serialVersionUID = 4820983178596826509L;

    public NotFoundException(String s){
        super(s);
    }
}
