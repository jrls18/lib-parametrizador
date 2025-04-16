package br.com.group.developer.corporation.libparametrizador.domain.core;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizeProperties;
import br.com.group.developer.corporation.libparametrizador.constants.KeyCacheConstant;
import br.com.group.developer.corporation.libparametrizador.domain.port.CacheGenericPort;
import br.com.group.developer.corporation.libparametrizador.domain.port.ParameterizePort;
import br.com.group.developer.corporation.libparametrizador.infrastructure.feign.adapter.ParameterizeClientAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ParameterizeCore implements ParameterizePort {

    private final CacheGenericPort cacheGenericPort;

    private final ParameterizeClientAdapter parameterizeClientAdapter;

    private final ParameterizeProperties properties;

    @Override
    public boolean getValueAsBoolean(String key) {

        if(StringUtils.isBlank(key))
            return false;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if(Boolean.FALSE.equals(cache.containsKey(key)))
            return false;

        return Boolean.parseBoolean(String.valueOf(cache.get(key)));
    }

    @Override
    public String getValueAsString(String key) {
        if(StringUtils.isBlank(key))
            return "";

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if(Boolean.FALSE.equals(cache.containsKey(key)))
            return "";

        return String.valueOf(cache.get(key));
    }

    @Override
    public Integer getValueAsInteger(String key) {
        if(StringUtils.isBlank(key))
            return 0;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if(Boolean.FALSE.equals(cache.containsKey(key)))
            return 0;

        return Integer.parseInt(String.valueOf(cache.get(key)));
    }

    @Override
    public Long getValueAsLong(String key) {
        if(StringUtils.isBlank(key))
            return 0L;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if(Boolean.FALSE.equals(cache.containsKey(key)))
            return 0L;

        return Long.parseLong(String.valueOf(cache.get(key)));
    }

    @Override
    public Object getValueAsObject(String key) {
        if(StringUtils.isBlank(key))
            return  null;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if(Boolean.FALSE.equals(cache.containsKey(key)))
            return null;

        return cache.get(key);
    }

    @Override
    public Map<String, Object> getValueAsMap() {
        execute();
        return this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);
    }


    private void execute(){
        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if(Boolean.FALSE.equals(cache.containsKey(KeyCacheConstant.NAME_KEY_DATE_TIME_TTL))){
            getToggle();
            return;
        }

        LocalDateTime dateTimeTtl = (LocalDateTime) cache.get(KeyCacheConstant.NAME_KEY_DATE_TIME_TTL);

        if(LocalDateTime.now().isAfter(dateTimeTtl))
            getToggle();
    }

    private void getToggle() {

        var result = parameterizeClientAdapter.getProperties();

        if(result == null)
            return;

        cacheGenericPort.clear(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        result.getProperties().forEach((key, value) -> cacheGenericPort.save(KeyCacheConstant.NAME_CACHE_PROPERTIES, key, value));

        cacheGenericPort.save(KeyCacheConstant.NAME_CACHE_PROPERTIES, KeyCacheConstant.NAME_KEY_DATE_TIME_TTL,
                LocalDateTime.now().plusMinutes(Long.parseLong(properties.getMinutesTtl())));

    }
}
