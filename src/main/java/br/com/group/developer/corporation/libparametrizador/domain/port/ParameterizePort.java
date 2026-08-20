package br.com.group.developer.corporation.libparametrizador.domain.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface ParameterizePort {

    boolean getValueAsBoolean(final String key);

    String getValueAsString(final String key);

    Integer getValueAsInteger(final String key);

    Long getValueAsLong(final String key);

    Object getValueAsObject(final String key);

    Map<String, Object> getValueAsMap();

    Set<String> getValueAsStringSplits(final String key, final String delimiter);

    Double getValueAsDouble(String key);

    BigDecimal getValueAsBigDecimal(String key);

    LocalDate getValueAsLocalDate(
            String key,
            String pattern);

    LocalDateTime getValueAsLocalDateTime(
            String key,
            String pattern);

    <T> T getValueAsObject(String key, Class<T> clazz);


    <T> List<T> getValueAsList(
            String key,
            String delimiter,
            Function<String, T> mapper);
}
