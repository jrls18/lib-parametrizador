package br.com.group.developer.corporation.libparametrizador.domain.core;

import br.com.group.developer.corporation.libparametrizador.config.ParameterizationProperties;
import br.com.group.developer.corporation.libparametrizador.constants.KeyCacheConstant;
import br.com.group.developer.corporation.libparametrizador.domain.port.CacheGenericPort;
import br.com.group.developer.corporation.libparametrizador.domain.port.ParameterizePort;
import br.com.group.developer.corporation.libparametrizador.domain.provider.ParameterizeProvider;
import br.com.group.developer.corporation.libparametrizador.exceptions.BadRequestLibException;
import br.com.group.developer.corporation.libparametrizador.exceptions.InternalServerErrorLibException;
import br.com.group.developer.corporation.libparametrizador.exceptions.NotAuthorizedLibException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParameterizeCore implements ParameterizePort {

    private final CacheGenericPort cacheGenericPort;

    private final ParameterizeProvider parameterizeProvider;

    private final ParameterizationProperties properties;

    private final ObjectMapper objectMapper;

    private final Object lock = new Object();


    @Override
    public boolean getValueAsBoolean(String key) {

        if (StringUtils.isBlank(key))
            return false;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return false;

        return Boolean.parseBoolean(String.valueOf(cache.get(key)));
    }

    @Override
    public String getValueAsString(String key) {
        if (StringUtils.isBlank(key))
            return "";

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return "";

        return String.valueOf(cache.get(key));
    }

    @Override
    public Integer getValueAsInteger(String key) {
        if (StringUtils.isBlank(key))
            return 0;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return 0;

        return Integer.parseInt(String.valueOf(cache.get(key)));
    }

    @Override
    public Long getValueAsLong(String key) {
        if (StringUtils.isBlank(key))
            return 0L;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return 0L;

        return Long.parseLong(String.valueOf(cache.get(key)));
    }

    @Override
    public Object getValueAsObject(String key) {
        if (StringUtils.isBlank(key))
            return null;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return null;

        return cache.get(key);
    }

    @Override
    public Map<String, Object> getValueAsMap() {
        execute();
        return this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);
    }

    @Override
    public Set<String> getValueAsStringSplits(String key, String delimiter) {
        String value = getValueAsString(key);

        if (StringUtils.isBlank(value))
            return Set.of();

        return Arrays.stream(value.split(Pattern.quote(delimiter)))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    @Override
    public Double getValueAsDouble(String key) {

        if (StringUtils.isBlank(key))
            return 0.0;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return 0.0;

        return Double.parseDouble(String.valueOf(cache.get(key)));
    }

    @Override
    public BigDecimal getValueAsBigDecimal(String key) {
        if (StringUtils.isBlank(key))
            return BigDecimal.ZERO;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return BigDecimal.ZERO;

        try {
            return new BigDecimal(String.valueOf(cache.get(key)));
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public LocalDate getValueAsLocalDate(String key, String pattern) {

        if (StringUtils.isBlank(key))
            return null;

        if (StringUtils.isBlank(pattern))
            return null;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return null;

        return LocalDate.parse(
                String.valueOf(cache.get(key)),
                DateTimeFormatter.ofPattern(pattern)
        );
    }

    @Override
    public LocalDateTime getValueAsLocalDateTime(String key, String pattern) {
        if (StringUtils.isBlank(key))
            return null;

        if (StringUtils.isBlank(pattern))
            return null;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return null;

        return LocalDateTime.parse(
                String.valueOf(cache.get(key)),
                DateTimeFormatter.ofPattern(pattern)
        );
    }

    @Override
    public <T> T getValueAsObject(String key, Class<T> clazz) {
        if (StringUtils.isBlank(key))
            return null;

        execute();

        var cache = this.cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return null;

        try {
            return objectMapper.readValue(
                    String.valueOf(cache.get(key)),
                    clazz
            );
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(
                    "Erro ao converter parâmetro '" + key + "' para " + clazz.getSimpleName(),
                    ex
            );
        }
    }

    @Override
    public <T> List<T> getValueAsList(String key, String delimiter, Function<String, T> mapper) {
        if (StringUtils.isBlank(key))
            return List.of();

        if (StringUtils.isBlank(delimiter))
            return List.of();

        if (mapper == null)
            return List.of();

        execute();

        var cache = this.cacheGenericPort.getCache(
                KeyCacheConstant.NAME_CACHE_PROPERTIES);

        if (!cache.containsKey(key))
            return List.of();

        String value = String.valueOf(cache.get(key));

        if (StringUtils.isBlank(value))
            return List.of();

        return Arrays.stream(value.split(Pattern.quote(delimiter)))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(mapper)
                .toList();
    }


    private void execute() {
        synchronized (lock) {
            var cache = cacheGenericPort.getCache(KeyCacheConstant.NAME_CACHE_PROPERTIES);

            if (!cache.containsKey(KeyCacheConstant.NAME_KEY_DATE_TIME_TTL)) {
                getToggle();
                return;
            }

            LocalDateTime ttl =
                    (LocalDateTime) cache.get(KeyCacheConstant.NAME_KEY_DATE_TIME_TTL);

            if (LocalDateTime.now().isAfter(ttl))
                getToggle();
        }
    }


    private void getToggle() {

        Map<String, Object> result = getParameters();

        if (result.isEmpty()) {
            log.warn("Parameterization result is null or empty");
            return;
        }

        final String cacheName = KeyCacheConstant.NAME_CACHE_PROPERTIES;

        Map<String, Object> currentCache =
                cacheGenericPort.getCache(cacheName);

        Map<String, Object> backup = new HashMap<>();

        if (!currentCache.isEmpty())
            backup = new HashMap<>(currentCache);

        long ttlMinutes = Math.max(
                NumberUtils.toLong(properties.getMinutesTtl(), 5L),
                1L
        );

        try {

            // limpa antes de aplicar novo estado
            cacheGenericPort.clear(cacheName);

            // aplica novo cache
            result.forEach((key, value) -> {

                        if (key == null) {
                            log.warn("Skipping null key in parameterization result");
                            return;
                        }

                        cacheGenericPort.save(cacheName, key, value);
                    }
            );

            // TTL sempre baseado no refresh atual
            cacheGenericPort.save(
                    cacheName,
                    KeyCacheConstant.NAME_KEY_DATE_TIME_TTL,
                    LocalDateTime.now().plusMinutes(ttlMinutes)
            );

            log.debug(
                    "Parameterization cache updated successfully. Keys loaded: {}",
                    result.size()
            );

        } catch (Exception ex) {

            log.error(
                    "Error updating parameterization cache. Attempting rollback.",
                    ex
            );

            // se não tinha cache anterior, não tenta rollback
            if (backup.isEmpty()) {

                log.error("No previous cache available. Keeping system in safe empty state.");

                return; // <- IMPORTANTE: evita crash da aplicação
            }

            try {

                cacheGenericPort.clear(cacheName);

                backup.forEach((key, value) ->
                        cacheGenericPort.save(cacheName, key, value)
                );

                // garante que TTL continua válido
                cacheGenericPort.save(
                        cacheName,
                        KeyCacheConstant.NAME_KEY_DATE_TIME_TTL,
                        LocalDateTime.now().plusMinutes(ttlMinutes)
                );

                log.warn(
                        "Previous cache restored successfully. Next refresh in {} minute(s).",
                        ttlMinutes
                );

            } catch (Exception rollbackEx) {

                log.error(
                        "Rollback failed. System will remain without parameter cache.",
                        rollbackEx
                );

            }
        }
    }



    private Map<String, Object> getParameters() {

        try {

            var response = parameterizeProvider.getProperties();

            if (response == null) {
                log.warn("Parameterization provider returned null response");
                return getContingencyOrThrow(
                        "Provider returned null response");
            }

            var parameters = response.properties();

            if (parameters == null || parameters.isEmpty()) {
                log.warn("Parameterization provider returned empty parameters");
                return getContingencyOrThrow(
                        "Provider returned empty parameters");
            }

            return parameters;

        } catch (NotAuthorizedLibException ex) {

            log.error(
                    "Authentication/Authorization error while fetching parameterization",
                    ex);

            throw ex;

        } catch (BadRequestLibException ex) {

            log.error(
                    "Invalid request sent to parameterization API",
                    ex);

            throw ex;

        } catch (InternalServerErrorLibException ex) {

            log.error(
                    "Internal server error returned by parameterization API",
                    ex);

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "Parameterization unavailable. Activating contingency mode",
                    ex);

            return getContingencyOrThrow(
                    "Provider exception: " + ex.getClass().getSimpleName());
        }
    }

    private Map<String, Object> getContingencyOrThrow(String reason) {

        Map<String, Object> contingency =
                getParametersContingency(reason);

        if (contingency.isEmpty()) {

            throw new IllegalStateException(
                    "Parameterization provider is unavailable and no contingency parameters were configured.");
        }

        return contingency;
    }

    private Map<String, Object> getParametersContingency(String reason) {
        log.warn(
                "PARAMETERIZATION FALLBACK ACTIVATED. Reason: {}",
                reason);

        if (properties.getParameterize() == null
                || properties.getParameterize().getParameters() == null
                || properties.getParameterize().getParameters().isEmpty()) {

            return Map.of();
        }

        Map<String, Object> defaultValues = new HashMap<>();

        properties.getParameterize()
                .getParameters()
                .forEach(item ->
                        defaultValues.put(
                                item.getKey(),
                                item.getDefaultValue()));

        return defaultValues;
    }
}
