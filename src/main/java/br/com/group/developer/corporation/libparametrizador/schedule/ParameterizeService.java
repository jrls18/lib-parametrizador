package br.com.group.developer.corporation.libparametrizador.schedule;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.config.properties.Property;
import br.com.group.developer.corporation.libparametrizador.config.properties.RequestFields;
import br.com.group.developer.corporation.libparametrizador.exceptions.NotFoundException;
import br.com.group.developer.corporation.libparametrizador.http.service.ParametrizadorService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Component
@EnableCaching
public class ParameterizeService {
    private static final String CACHE_PARAMS_NAME = "Parametrize_CACHING";

    private final ConfigProperties properties;

    private final ParametrizadorService service;

    private final CacheManager cacheManager;

    public ParameterizeService(ConfigProperties properties,ParametrizadorService service,CacheManager cacheManager ){
        this.properties = properties;
        this.service = service;
        this.cacheManager = cacheManager;
    }


    private Map<String, Object> getProperties() throws NotFoundException, Exception {

        Map<String, Object> mapCaching = new HashMap<>();

        for (Property line: properties.getProperties()) {
           Map<String,Object> request = new HashMap<>(line.getRequestFields().size());

           for(RequestFields requestFields : line.getRequestFields()){
               request.put(requestFields.getKey(),requestFields.getValue());
           }

           Map<String,Object> response =  service.getParameters(line.getName(),request);

           for(String cacheConfiguration : line.getFieldCaching().getFields()){
               for (Map.Entry<String,Object> m : response.entrySet()){

                   if(m.getKey().equalsIgnoreCase("properties")){

                       Map<String,Object> objectMap = (Map<String, Object>) m.getValue();
                       for (Map.Entry<String,Object> item: objectMap.entrySet()) {
                           if(cacheConfiguration.equalsIgnoreCase(item.getKey()))
                               mapCaching.put(item.getKey(),item.getValue());
                       }

                   }else {
                       if(cacheConfiguration.equalsIgnoreCase(m.getKey()))
                           mapCaching.put(m.getKey(),m.getValue());
                   }
               }
           }
        }

        return mapCaching;
    }

    @CachePut(value = CACHE_PARAMS_NAME)
    private Map<String,Object> putOnCache(final Map<String,Object> map){
        return map;
    }

    @Cacheable(value = CACHE_PARAMS_NAME, unless = "#result == null")
    public Map<String, Object> getPropertiesCache() throws NotFoundException, Exception {
        try {
           return getProperties();
        }catch (Exception ex){
            return null;
        }
    }

    @CacheEvict(value = CACHE_PARAMS_NAME, condition = "#value != null", allEntries = true)
    @Scheduled(cron = "${parameterize.scheduleCron:0 0 0 * * *}", zone = "${parameterize.timezone:America/Fortaleza}")
    public void cacheScheduleStart() throws NotFoundException {
        String value = null;
        try{
            var map = getProperties();

            value = "OK";
            if(!map.isEmpty()) {
                log.info("INICIALIZADO A ATUALIZAÇÃO DO CASH");

                cacheManager.getCacheNames()
                        .parallelStream()
                        .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());

                putOnCache(map);
                log.info("CONCLUIDO A ATUALIZAÇÃO DO CASH");
            }
        }catch (NotFoundException | Exception ex){
           log.info("Tivemos um problema para consultar a api de parametrizador. Por conta disso não atualizamos o caching");
        }
    }
}
