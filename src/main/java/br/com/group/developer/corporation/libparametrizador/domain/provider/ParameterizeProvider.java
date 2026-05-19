package br.com.group.developer.corporation.libparametrizador.domain.provider;

import br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.message.ParameterizeDetails;

public interface ParameterizeProvider {

    ParameterizeDetails getProperties();
}
