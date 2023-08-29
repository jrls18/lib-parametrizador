package br.com.group.developer.corporation.libparametrizador.schedule;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.constants.ParametrizeConstants;
import br.com.group.developer.corporation.libparametrizador.exceptions.ErroAoConsultarApiDoParametrizadorException;
import br.com.group.developer.corporation.libparametrizador.exceptions.NaoExisteParametroConfiguradoNoParametrizadorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
class ParametrizadorRepository {

    private final ConfigProperties configProperties;

    private final CallApiServiceParametrizadorService service;

    private final CacheManager cacheManager;

    @Cacheable(value = ParametrizeConstants.CACHE_PARAMS_NAME,unless = "#result == null")
    public Map<String, Object> getPropertiesParametrizeCache() {
        return getAtualizaPropertiesParametrize();
    }

    public void cleanCache(){
        try {
            var map = getAtualizaPropertiesParametrize();
            if(!map.isEmpty()) {
                log.info("INICIALIZADO A ATUALIZAÇÃO DO CASH");

                removeCache();

                putOnCache(map);

                log.info("CONCLUIDO A ATUALIZAÇÃO DO CASH");
            }
        }catch (NaoExisteParametroConfiguradoNoParametrizadorException |
                ErroAoConsultarApiDoParametrizadorException ex){
            log.warn("FALHA AO CHAMAR O SERVICE--PARAMETRIZADOR Estamos montando o cache Detalhes: " + ex.getMessage());
        }
    }

    public void removeCache(){
        cacheManager.getCacheNames()
                .parallelStream()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    @CacheEvict(value = ParametrizeConstants.CACHE_PARAMS_NAME, allEntries = true)
    @CachePut(value = ParametrizeConstants.CACHE_PARAMS_NAME)
    public Map<String,Object> putOnCache(final Map<String,Object> map){
       return map;
    }

    public Map<String, Object> getAtualizaPropertiesParametrize(){
        Map<String, Object> mapCaching = null;

        try{
            if(Objects.nonNull(configProperties.getItemsProperties()) &&
                    Boolean.FALSE.equals(CollectionUtils.isEmpty(configProperties.getItemsProperties().getProperties())) &&
                    Objects.nonNull(configProperties.getItemsProperties().getFilterCondition()) &&
                    configProperties.getItemsProperties().getFilterCondition().length > 0
            ){
                mapCaching = new HashMap<>();

                Map<String,Object> items = service.getParameters(configProperties.getItemsProperties());

                Map<String, Object> objectMap = (Map<String, Object>) items.get("properties");

                mapCaching.putAll(objectMap);
            }
        }catch (Exception ex){
            throw new ErroAoConsultarApiDoParametrizadorException("MANTEMOS O CACHE pois deu um erro ao consultar api do SERVICE--PARAMETRIZADOR detalhes: " + ex.getMessage());
        }

        return mapCaching;
    }

}
