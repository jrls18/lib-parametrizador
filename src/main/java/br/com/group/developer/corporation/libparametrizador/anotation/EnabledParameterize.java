package br.com.group.developer.corporation.libparametrizador.anotation;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan("br.com.group.developer.corporation.libparametrizador")
public @interface EnabledParameterize {
}
