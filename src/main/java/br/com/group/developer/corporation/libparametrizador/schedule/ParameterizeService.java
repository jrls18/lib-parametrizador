package br.com.group.developer.corporation.libparametrizador.schedule;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.config.properties.Property;
import br.com.group.developer.corporation.libparametrizador.config.properties.RequestFields;
import br.com.group.developer.corporation.libparametrizador.exceptions.BadRequestParameterizeException;
import br.com.group.developer.corporation.libparametrizador.exceptions.InternalServerErrorParameterizeException;
import br.com.group.developer.corporation.libparametrizador.exceptions.NaoExisteMockConfiguradoException;
import br.com.group.developer.corporation.libparametrizador.exceptions.NotFoundParameterizeException;
import br.com.group.developer.corporation.libparametrizador.http.service.ParametrizadorService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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


    private Map<String, Object> getProperties() throws BadRequestParameterizeException, NotFoundParameterizeException, InternalServerErrorParameterizeException, NaoExisteMockConfiguradoException {

        Map<String, Object> mapCaching = new HashMap<>();

        if(properties.getIsMock() &&
                System.getenv("SPRING_PROFILES_ACTIVE").equalsIgnoreCase("local")){

            if(Objects.isNull(properties.getPropertiesMock()) || CollectionUtils.isEmpty(properties.getPropertiesMock().getFields()))
                throw new NaoExisteMockConfiguradoException("Processo de Mock está ativo e não existe parametros configurado para ser mockado.");

            for (RequestFields mockFields: properties.getPropertiesMock().getFields()) {
                mapCaching.put(mockFields.getKey(), mockFields.getValue());
            }

        }else {
            getServiceParametrize(mapCaching);
        }

        return mapCaching;
    }

    private void getServiceParametrize(Map<String, Object> mapCaching) {

       if(Objects.nonNull(properties.getProperties())){
           if(CollectionUtils.isEmpty(properties.getProperties())){
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
           }
       }
    }

    @CachePut(value = CACHE_PARAMS_NAME)
    private Map<String,Object> putOnCache(final Map<String,Object> map){
        return map;
    }

    @Cacheable(value = CACHE_PARAMS_NAME, unless = "#result == null")
    public Map<String, Object> getPropertiesCache() {
        try {
           return getProperties();
        }catch (BadRequestParameterizeException |
                NotFoundParameterizeException |
                InternalServerErrorParameterizeException |
                NaoExisteMockConfiguradoException ex){

            log.warn("FALHA AO CHAMAR O SERVICE--PARAMETRIZADOR Estamos montando o cache. Detalhes dos erro: " + ex);
            return null;
        }
    }

    public String getValueKey(final String key){
       if(Boolean.FALSE.equals(StringUtils.isEmpty(key))){
           if(this.getPropertiesCache().containsKey(key)){
               var value = this.getPropertiesCache().get(key);
               return value.toString();
           }else {
               return null;
           }
       }
       return null;
    }

    @CacheEvict(value = CACHE_PARAMS_NAME, condition = "#value != null", allEntries = true)
    @Scheduled(cron = "${parameterize.scheduleCron:0 0 0 * * *}", zone = "${parameterize.timezone:America/Fortaleza}")
    public void cacheScheduleStart() {
        String value = null;
        try {
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
        }catch (NaoExisteMockConfiguradoException |
                BadRequestParameterizeException |
                NotFoundParameterizeException | InternalServerErrorParameterizeException ex){
            log.warn("FALHA AO CHAMAR O SERVICE--PARAMETRIZADOR Estamos montando o cache");
        }
    }
}
