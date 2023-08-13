package br.com.group.developer.corporation.libparametrizador.schedule;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.config.properties.RequestFields;
import br.com.group.developer.corporation.libparametrizador.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
@RequiredArgsConstructor
public class ParameterizeService {

    private final ConfigProperties properties;

    private final ParametrizadorRepository repository;


    private Map<String, Object> getProperties()  {

        Map<String, Object> mapCaching = null;

        try {
            if(properties.getIsMock() &&
                    System.getenv("SPRING_PROFILES_ACTIVE").equalsIgnoreCase("local")){

                if(Objects.isNull(properties.getPropertiesMock()) || CollectionUtils.isEmpty(properties.getPropertiesMock().getFields()))
                    throw new NaoExisteMockConfiguradoException("Processo de Mock está ativo e não existe parametros configurado para ser mockado.");

                mapCaching = new HashMap<>();

                for (RequestFields mockFields: properties.getPropertiesMock().getFields()) {
                    mapCaching.put(mockFields.getKey(), mockFields.getValue());
                }

            }else {
                mapCaching = this.repository.getPropertiesParametrizeCache();

                if(Objects.isNull(mapCaching) || mapCaching.isEmpty())
                    throw new NaoExisteParametroConfiguradoNoParametrizadorException("Não existe parametros configurado no paramaetrizador.");
            }
        }catch (BadRequestParameterizeException |
                NaoExisteMockConfiguradoException |
                NaoExisteParametroConfiguradoNoParametrizadorException |
                ErroAoConsultarApiDoParametrizadorException ex){
            log.warn("FALHA AO CHAMAR O SERVICE--PARAMETRIZADOR Estamos montando o cache. Detalhes dos erro: " + ex);
            return null;
        }

        return mapCaching;
    }



    public String getPropertiesString(final String key){
        if(StringUtils.isEmpty(key))
            throw new KeyNaoPodeSerNulaOuVaziaException("Key informada não deve ser nula ou vazia.");

        Map<String,Object> map = this.getProperties();

        if(Objects.isNull(map) || Boolean.FALSE.equals(map.containsKey(key)))
            throw new NaoExisteParametroConfiguradoNoParametrizadorException("A key '" + key +"' não existe configurado.");

       return map.get(key).toString();
    }

    public Object getPropertiesObject(final String key){
        if(StringUtils.isEmpty(key))
            throw new KeyNaoPodeSerNulaOuVaziaException("Key informada não deve ser nula ou vazia.");

        Map<String,Object> map = this.getProperties();

        if(Objects.isNull(map) || Boolean.FALSE.equals(map.containsKey(key)))
            throw new NaoExisteParametroConfiguradoNoParametrizadorException("A key '" + key +"' não existe configurado.");

        return map.get(key);
    }

    @Scheduled(cron = "${parameterize.scheduleCron:0 0 0 * * *}", zone = "${parameterize.timezone:America/Fortaleza}")
    public void cleanCache() {
       repository.cleanCache();
    }

    public void paramaetrizadorEstaUp(){
        if(Objects.isNull(getProperties()))
            throw new ErroAoConsultarApiDoParametrizadorException("Estamos com problema ao consultar o SERVICE--PARAMETRIZADOR.");
    }
}
