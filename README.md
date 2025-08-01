# Introdução
Lib resposável por fazer a consulta de properties no parametrizador.

### Maven
```
<dependency>
  <groupId>br.com.group.developer.corporation</groupId>
  <artifactId>lib-parametrizador</artifactId>
  <version>4.0.1</version>
</dependency>
```

### Gradle
```
implementation group: 'br.com.group.developer.corporation', name: 'lib-parametrizador', version: '4.0.1'
```

### Gradle Short
```
implementation 'br.com.group.developer.corporation:lib-parametrizador:4.0.1'
```

### Desenho funcional da lib
![image_fluxo.png](src%2Fmain%2Fresources%2Fimage_fluxo.png)


### Utilizando a lib no projeto
#### Projeto

**application.yml**<br />
**Obs** No application.yml é o ambiente produtivo e nela é resposável por bater no service e fazer a requição no parametrizador conforme os parametros abaixo:<br />
**Obs1:** Deve-se adicionar no *Environment variables:* *SPRING_PROFILES_ACTIVE=dev*
```
parameterizationProperties:
  maxRetry: "3" 
  minutesTtl: "5"
  enableContingencyConfigMap: true
  clientId: ${properties.configMap.clientId}
  clientSecret: ${properties.configMap.clientSecret}
  applicationName: ${spring.application.name}
  parameterize:
    parameters:
      - key: "disablesCriticalKafkaContingency"
        defaultValue: true
      - key: "enabledServiceExternalHost"
        defaultValue: true
      - key: "disablesCallApiCollaboratorContingency"
        defaultValue: true
      - key: "disablesCallApiCompanyContingency"
        defaultValue: true
    filters:
      - "serviceCompanyExternalHost"
      - "serviceCollaboratorExternalHost"
      - "serviceCompanyInternalHost"
      - "serviceCollaboratorInternalHost"
      - "disablesCallApiCollaboratorContingency"
      - "disablesCriticalKafkaContingency"
      - "disablesCallApiCompanyContingency"
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

### Aplicação
**Obs** Deve-se adicionar o *@EnableScheduling* no **App.class**
```
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