package br.com.group.developer.corporation.libparametrizador.config;

import br.com.group.developer.corporation.libparametrizador.exceptions.ErroAoConsultarApiDoParametrizadorException;
import br.com.group.developer.corporation.libparametrizador.schedule.ParameterizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(Mono.class)
@ConditionalOnProperty(prefix = "parameterize", name = "validaParametrizadorEstaUp", havingValue = "true")
public class StartValidParametrizeUpConfig {

    private final ParameterizeService service;

    @Bean
    public String getAllServiceCache(){
        try{
            service.paramaetrizadorEstaUp();
            return "OK";
        }catch (ErroAoConsultarApiDoParametrizadorException ex){
            log.warn(ex.getMessage());
            throw ex;
        }
    }
}
