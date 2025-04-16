package br.com.group.developer.corporation.libparametrizador.domain.port;

import java.util.Map;

public interface ParameterizePort {

    boolean getValueAsBoolean(final String key);

    String getValueAsString(final String key);

    Integer getValueAsInteger(final String key);

    Long getValueAsLong(final String key);

    Object getValueAsObject(final String key);

    Map<String, Object> getValueAsMap();

}
