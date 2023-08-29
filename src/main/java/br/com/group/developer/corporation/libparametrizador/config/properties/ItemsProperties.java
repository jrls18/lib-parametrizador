package br.com.group.developer.corporation.libparametrizador.config.properties;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsProperties implements Serializable {
    private static final long serialVersionUID = 3301618063863578636L;

    private Set<Property> properties;

    private String[] filterCondition;
}
