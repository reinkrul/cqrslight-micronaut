package nl.reinkrul.cqrslight.micronaut;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.micronaut.aop.Adapter;
import io.micronaut.context.annotation.Executable;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value = RUNTIME)
@Target(value = { ANNOTATION_TYPE, METHOD })
@Executable(processOnStartup = true)
public @interface CommandHandler {
}
