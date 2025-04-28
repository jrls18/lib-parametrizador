package br.com.group.developer.corporation.libparametrizador.infrastructure.cache;

import br.com.group.developer.corporation.libparametrizador.domain.port.CacheGenericPort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CacheGenericAdapter implements CacheGenericPort {

    private final CacheManager cacheManager;

    private final ConcurrentMapCacheManager concurrentMapCacheManager;

    @Override
    public Map<String, Object> getCache(String cacheName) {
        if (StringUtils.isBlank(cacheName))
            return Map.of();

        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null && cache.getNativeCache() instanceof Map<?, ?> nativeCache) {
            Map<String, Object> result = new HashMap<>();

            for (Map.Entry<?, ?> entry : nativeCache.entrySet()) {
                if (entry.getKey() instanceof String) {
                    result.put((String) entry.getKey(), entry.getValue());
                }
            }
            return result;
        }

        return Map.of();
    }

    @Override
    public void save(String cacheName, String key, Object value) {
        if (StringUtils.isBlank(cacheName))
            return;

        Cache cache = newCache(cacheName);

        if (cache != null)
            cache.put(key, value);
    }

    @Override
    public void clear(String cacheName) {
        if (StringUtils.isBlank(cacheName))
            return;

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null)
            cache.clear();
    }


    private Cache newCache(final String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            var getCacheExists = new HashSet<>(concurrentMapCacheManager.getCacheNames());
            getCacheExists.add(cacheName);
            concurrentMapCacheManager.setCacheNames(getCacheExists);
        }
        return cacheManager.getCache(cacheName);
    }

}
