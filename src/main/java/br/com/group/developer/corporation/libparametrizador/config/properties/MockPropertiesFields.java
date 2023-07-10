package br.com.group.developer.corporation.libparametrizador.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MockPropertiesFields implements Serializable {
    @Serial
    private static final long serialVersionUID = 1753929111402261495L;

    private Set<RequestFields> fields;
}
