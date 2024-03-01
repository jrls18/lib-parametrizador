# Introdução
Lib resposável por fazer a consulta de properties no parametrizador.

### Maven
```
<dependency>
  <groupId>br.com.group.developer.corporation</groupId>
  <artifactId>lib-parametrizador</artifactId>
  <version>3.0.0</version>
</dependency>
```

### Gradle
```
implementation group: 'br.com.group.developer.corporation', name: 'lib-parametrizador', version: '3.0.0'
```

### Gradle Short
```
implementation 'br.com.group.developer.corporation:lib-parametrizador:3.0.0'
```

### Desenho funcional da lib
![image_fluxo.png](src%2Fmain%2Fresources%2Fimage_fluxo.png)


### Utilizando a lib no projeto
#### Projeto

**application.yml**<br />
**Obs** No application.yml é o ambiente produtivo e nela é resposável por bater no service e fazer a requição no parametrizador conforme os parametros abaixo:<br />
**Obs1:** Deve-se adicionar no *Environment variables:* *SPRING_PROFILES_ACTIVE=dev*
```
parameterize:
  clientId: ${properties.configMap.clientId}
  clientSecret: ${properties.configMap.clientSecret}
  applicationName: ${spring.application.name}
  scheduleCron: "0 */5 * * * *"
  validaParametrizadorEstaUp: true
  timezone: America/Fortaleza
  filterMultipleKey:
    properties:
      key:
        - "disablesCriticalKafkaContingency"
        - "serviceCollaborator"
        - "enabledServiceExternalHost"
        - "disablesCallApiDocumentContingency"
        - "disablesCallApiCompanyContingency"
        - "urlServiceExternal"
        - "urlServiceInternal"
      filter:
        - "serviceCompanyExternalHost"
        - "serviceCompanyInternalHost"
        - "serviceDocumentExternalHost"
        - "serviceDocumentInternalHost"
```

### Definições dos campos

| campo                      | default | Obrigatório | Obs                                                                                                                                                                      |
|----------------------------|---------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| validaParametrizadorEstaUp | [S]     | [N]         | Default false. Valida as properties na subida da aplicação.                                                                                                              |
| clientId                   | [N]     | [S]         | Chave cadastrada no authorizador para  aplicação                                                                                                                         |
| clientSecret               | [N]     | [S]         | Chave cadastrada no authorizador para  aplicação                                                                                                                         |
| applicationName            | [N]     | [N]         | Nome da apllicação nos logs irá aprecer nos header como origem                                                                                                           |
| scheduleCron               | [S]     | [N]         | Cron por default está para rodar a cada 0 0 0 * * *                                                                                                                      |
| timezone                   | [S]     | [N]         | Por default será sempre America/Fortaleza                                                                                                                                |
| key                        | [N]     | [S]         | Chave configurada no parametrizador porém em array de texto                                                                                                              |
| filter                     | [S]     | [N]         | Default nulo. Para chaves com json e deseja somente pegar um unico valor utiliza essa properties para pegar de um json com várias chaves e pegar somente o que vc deseja. |


**application-local.yml**<br />
**Obs** Parametros para o ambiente mockado logado.<br />
*OBS1:* Deve-se adicionar no *Environment variables:* *SPRING_PROFILES_ACTIVE=local*
```
parameterize:
  isMock: true
  propertiesMock:
    fields:
      - key: serviceDocumentInternalHost
        value: http://cloud.local.develop.corporation.com/service--documents
      - key: serviceDocumentExternalHost
        value: http://localhost:5001/service--documents
      - key: serviceCompanyInternalHost
        value: http://cloud.local.develop.corporation.com/service--company
      - key: serviceCompanyExternalHost
        value: http://localhost:5000/service--company
      - key: enabledServiceExternalHost
        value: "true"
      - key: disablesKafkaContingency
        value: "false"
      - key: disablesCriticalKafkaContingency
        value: "false"
      - key: disablesCallApiDocumentContingency
        value: "false"
      - key: disablesCallApiCompanyContingency
        value: "true"
      - key: sizePage
        value: "50"
      - key: qtdRetry
        value: "3"
```


### Aplicação
**Obs** Deve-se adicionar o *@EnableScheduling* no **App.class**
```
@EnableScheduling
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```



### Reference Documentation
Repositório do service: https://gitlab.com/corporate-service/service-management-parametrizador
URL do serice: {{url_base}}/service--parametrizador/configurator/v1/filter/execute