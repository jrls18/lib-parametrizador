package br.com.group.developer.corporation.libparametrizador.infrastructure.httpfeign.config;

import br.com.group.developer.corporation.libparametrizador.constants.KeyCacheConstant;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public ConcurrentMapCacheManager cacheManager(){
        return new ConcurrentMapCacheManager(KeyCacheConstant.NAME_CACHE_PROPERTIES);
    }
}
