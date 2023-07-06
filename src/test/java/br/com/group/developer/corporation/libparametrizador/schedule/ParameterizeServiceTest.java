package br.com.group.developer.corporation.libparametrizador.schedule;

import br.com.group.developer.corporation.libparametrizador.config.ConfigProperties;
import br.com.group.developer.corporation.libparametrizador.config.properties.Property;
import br.com.group.developer.corporation.libparametrizador.config.properties.RequestFields;
import br.com.group.developer.corporation.libparametrizador.config.properties.ResponseCacheConfiguration;
import br.com.group.developer.corporation.libparametrizador.exceptions.NotFoundException;
import br.com.group.developer.corporation.libparametrizador.http.service.ParametrizadorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.CacheManager;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParameterizeServiceTest {

    ParameterizeService service;

    @Mock
    ParametrizadorService parametrizadorService;

    @Mock
    CacheManager cacheManager;

    @Test
    public void teste() throws NotFoundException, Exception {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map =  mapper.readValue("{\"codigo\":\"a0f2cb31-df17-48d0-8d1b-057b07dcb907\",\"nome\":\"properties-service-conta-corrente-service\",\"properties\":{\"urlServiceTeste\":\"http://teste.com\",\"urlServiceCliente\":\"http://google.com\"}}",Map.class);

        when(parametrizadorService.getParameters(anyString(), anyMap())).thenReturn(map);

        ConfigProperties properties = new ConfigProperties();
        properties.setClientId(UUID.randomUUID().toString());
        properties.setClientSecret(UUID.randomUUID().toString());

        Property property = new Property();
        property.setName("properties-service-conta-corrente-service");

        RequestFields requestFields = new RequestFields();
        requestFields.setKey("nome-service");
        requestFields.setValue("urlServiceInternal");
        property.setRequestFields(Set.of(requestFields));

        ResponseCacheConfiguration responseCacheConfiguration = new ResponseCacheConfiguration();
        responseCacheConfiguration.setFields(Set.of("urlServiceCliente"));
        property.setFieldCaching(responseCacheConfiguration);


        properties.setProperties(Set.of(property));


        service = new ParameterizeService(properties,parametrizadorService, cacheManager);


        Map<String,Object> value = service.getPropertiesCache();

        assertNotNull(value);
    }





}