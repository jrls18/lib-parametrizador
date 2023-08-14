package br.com.group.developer.corporation.libparametrizador.config;

import br.com.group.developer.corporation.libparametrizador.exceptions.ErroAoConsultarApiDoParametrizadorException;
import br.com.group.developer.corporation.libparametrizador.schedule.ParameterizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@Configuration
@RequiredArgsConstructor
public class StartValidParametrizeUpConfig {

    private final ParameterizeService service;

    @Bean
    @ConditionalOnProperty(prefix = "parameterize", name = "validaParametrizadorEstaUp", havingValue = "true")
    public void getAllServiceCache(){
        try{
            service.paramaetrizadorEstaUp();
        }catch (ErroAoConsultarApiDoParametrizadorException ex){
            log.warn(ex.getMessage());
            throw ex;
        }
    }
}
