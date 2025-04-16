package br.com.group.developer.corporation.libparametrizador.domain.port;

import java.util.Map;

public interface CacheGenericPort {

    Map<String, Object> getCache(final String cacheName);

    void save(final String cacheName, final String key, final Object value);

    void clear(final String cacheName);
}
