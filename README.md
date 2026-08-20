# 📦 Lib Parameterize (v6.0.0)

## 📌 Introdução

A **Lib Parameterize** é uma biblioteca interna para centralizar e padronizar o acesso a parâmetros dinâmicos de configuração de aplicações.

Ela permite:

- Buscar parâmetros remotamente via API
- Cache com TTL automático
- Fallback para valores de contingência
- Conversão tipada (String, Integer, Boolean, List, Map, Date, etc.)
- Suporte a parsing customizado de listas e objetos
- Retry automático em falhas de comunicação

---

## 🚀 Utilização

### Maven
```
<dependency>
  <groupId>br.com.group.developer.corporation</groupId>
  <artifactId>lib-parametrizador</artifactId>
  <version>6.0.0</version>
</dependency>
```

---

### Gradle
```
implementation group: 'br.com.group.developer.corporation', name: 'lib-parametrizador', version: '6.0.0'
```

---

### Gradle Short
```
implementation 'br.com.group.developer.corporation:lib-parametrizador:6.0.0'
```

---

## 🚀 Como habilitar a lib

Para ativar a lib no seu projeto Spring Boot, utilize a annotation:

```java
import br.com.group.developer.corporation.libparametrizador.config.EnabledParameterize;

@EnabledParameterize
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}   
```
---

## ⚙️ Configuração (application.yml)

```yaml

parameterization-properties:
  max-retry: 3
  minutes-ttl: 5
  uri-base: "service--platform.platform.svc.cluster.local"
  port: "5000"
  enabled-https: false
  enable-contingency-config-map: true

  client-id: "00000000-0000-0000-0000-000000000000"
  client-secret: "00000000-0000-0000-0000-000000000000"
  application-name: "my-app"

  parameterize:
    filters:
      - "ENV"
      - "REGION"
    parameters:
      - key: "feature.newFlow"
        defaultValue: "false"
      - key: "timeout.api"
        defaultValue: "5000"
 ```
---

## 🔧 O que a lib faz internamente

A lib segue um fluxo controlado de carregamento, cache e fallback para garantir resiliência e disponibilidade.

---

## ✔ Fluxo principal

```text
1. Consulta API de parametrização
2. Aplica retry em falhas transitórias:
   - 408 Timeout
   - 503 Service Unavailable
   - 500 Internal Server Error
3. Cacheia valores em memória
4. Aplica TTL automático
5. Retorna valores para consumo da aplicação

```

---
## ⚠️ Fallback de contingência

O fallback de contingência é utilizado quando a API de parametrização não está disponível ou retorna dados inválidos.

### 🔄 Quando o fallback é acionado

O fallback é utilizado nos seguintes cenários:

```text
- API retorna erro (qualquer exceção)
- API retorna resposta nula
- API retorna parâmetros vazios
```

## 📚 Uso da API (ParameterizePort)

A lib expõe a interface `ParameterizePort`, responsável por fornecer acesso tipado aos parâmetros dinâmicos carregados via API ou fallback.

---

## 📌 Interface completa

```java id="interface-parameterizeport"
public interface ParameterizePort {

    boolean getValueAsBoolean(String key);

    String getValueAsString(String key);

    Integer getValueAsInteger(String key);

    Long getValueAsLong(String key);

    Double getValueAsDouble(String key);

    BigDecimal getValueAsBigDecimal(String key);

    Object getValueAsObject(String key);

    Map<String, Object> getValueAsMap();

    Set<String> getValueAsStringSplits(String key, String delimiter);

    LocalDate getValueAsLocalDate(String key, String pattern);

    LocalDateTime getValueAsLocalDateTime(String key, String pattern);

    <T> T getValueAsObject(String key, Class<T> clazz);

    <T> List<T> getValueAsList(
            String key,
            String delimiter,
            Function<String, T> mapper
    );
}
```

## 🔹 Exemplos de uso

Abaixo estão exemplos práticos de utilização da API `ParameterizePort` em diferentes cenários.

---

### ✔ Boolean (Feature Flag)

```java id="ex-boolean"
public void executeBusinessLogic() {
    boolean enabled = parameterizePort.getValueAsBoolean("feature.newFlow");

    if (enabled) {
        // executa nova regra de negócio
    } else {
        throw new RuntimeException("Feature flag is disabled");
    }
}

```

### ✔ ‘String’

```java
public void executeBusinessLogic() {
    String appName = parameterizePort.getValueAsString("app.name");

    System.out.println(appName);
}
```
---

### ✔ ‘Integer’

```java 
public void executeBusinessLogic() {
    Integer timeout = parameterizePort.getValueAsInteger("timeout.api");

    System.out.println(timeout);
}
```
---

### ✔ ‘Long’

```java
public void executeBusinessLogic() {
    Long limit = parameterizePort.getValueAsLong("limit.max");

    System.out.println(limit);
}
```
---


### ✔ ‘Double’

```java
public void executeBusinessLogic() {
    Double tax = parameterizePort.getValueAsDouble("tax.rate");

    System.out.println(tax);
}
```
---

### ✔ ‘BigDecimal’

```java
public void executeBusinessLogic() {
    BigDecimal price = parameterizePort.getValueAsBigDecimal("product.price");

    System.out.println(price);
}
```
---

### ✔ ‘Object’ (genérico)

```java
public void executeBusinessLogic() {
    Object value = parameterizePort.getValueAsObject("raw.value");

    System.out.println(value);
}
```
---

### ✔ ‘Object tipado’ (JSON → POJO)

```java
public void executeBusinessLogic() {
    MyConfig config = parameterizePort.getValueAsObject(
            "app.config",
            MyConfig.class
    );

    System.out.println(config);
}
```
---

### ✔ ‘Map completo’

```java
public void executeBusinessLogic() {
    Map<String, Object> params = parameterizePort.getValueAsMap();

    params.forEach((key, value) ->
            System.out.println(key + " = " + value)
    );
}
```
---

### ✔ ‘Set (split por delimitador)’

```java
public void executeBusinessLogic() {
    Set<String> regions = parameterizePort.getValueAsStringSplits(
            "app.regions",
            ";"
    );

    System.out.println(regions);
}
```
---

### ✔ ‘List com mapper customizado’

```java
public void executeBusinessLogic() {
    List<Integer> ids = parameterizePort.getValueAsList(
            "app.ids",
            ",",
            Integer::valueOf
    );

    System.out.println(ids);
}
```
---


### ✔ ‘LocalDate’

```java
public void executeBusinessLogic() {
    LocalDate date = parameterizePort.getValueAsLocalDate(
            "app.date",
            "yyyy-MM-dd"
    );

    System.out.println(date);
}
```
---

### ✔ ‘LocalDateTime’

```java
public void executeBusinessLogic() {
    LocalDateTime dateTime = parameterizePort.getValueAsLocalDateTime(
            "app.datetime",
            "yyyy-MM-dd HH:mm:ss"
    );

    System.out.println(dateTime);
}
```
---

## 🎯 Conclusão

A **Lib Parameterize** foi projetada para simplificar e padronizar o gerenciamento de configurações dinâmicas em aplicações distribuídas, garantindo segurança, resiliência e performance.

Com ela, é possível:

- Centralizar parâmetros de configuração em um único ponto
- Reduzir dependências diretas de arquivos de configuração ou chamadas manuais de API
- Garantir alta disponibilidade através de cache com TTL
- Manter continuidade da aplicação com fallback de contingência
- Aplicar retry automático em falhas transitórias
- Trabalhar com tipos fortemente tipados de forma simples e segura

---

## 🚀 Benefícios principais

✔ Redução de acoplamento com sistemas externos  
✔ Melhor performance através de cache local  
✔ Maior resiliência com fallback automático  
✔ Padronização no acesso a parâmetros  
✔ Menor risco de falhas em runtime  
✔ Facilidade de evolução e manutenção

---

## ⚠️ Considerações finais

- Em cenários críticos, sempre defina `defaultValue` para parâmetros importantes
- Evite depender diretamente da API de parametrização em loops ou fluxos de alta frequência
- Utilize feature flags para controle seguro de comportamento em produção
- Monitore falhas de fallback para identificar instabilidades externas

---

## 🧠 Resumo

A lib foi construída com foco em:

> **Resiliência + Performance + Simplicidade + Segurança**

Garantindo que sua aplicação continue funcionando mesmo diante de falhas externas.