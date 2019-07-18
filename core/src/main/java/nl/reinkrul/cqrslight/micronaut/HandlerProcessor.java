package nl.reinkrul.cqrslight.micronaut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Qualifier;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.micronaut.context.BeanContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.inject.qualifiers.Qualifiers;

public abstract class HandlerProcessor<T extends Annotation> implements ExecutableMethodProcessor<T> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerProcessor.class);

    private final BeanContext beanContext;
    private final Class<T> type;

    public HandlerProcessor(final BeanContext beanContext, final Class<T> type) {
        this.beanContext = beanContext;
        this.type = type;
    }

    @Override
    public void process(final BeanDefinition<?> beanDefinition, final ExecutableMethod<?, ?> method) {
        final Class<?> handlerType = beanDefinition.getBeanType();
        LOG.info("Registering {}: {}", type.getSimpleName(), handlerType);
        if (handlerExists(getAlias(handlerType))) {
            throw new IllegalArgumentException("Ambiguous command alias: " + handlerType);
        }
        final List<Object> arguments = Arrays.stream(method.getArguments())
                                             .map(argument -> {
                                                 final Object bean = getInjectableBeanFromArgument(argument);
                                                 if (bean == null) {
                                                     throw new IllegalStateException("Could not find bean to inject for argument of type "
                                                             + argument.getType() + " for " + handlerType + " " + beanDefinition);
                                                 }
                                                 return bean;
                                             }).collect(Collectors.toList());
        registerInvoker(handlerType, new Invoker(method, arguments));
    }

    protected abstract void registerInvoker(Class<?> handlerType, Invoker invoker);

    protected abstract boolean handlerExists(final String alias);

    protected String getAlias(final Class<?> handlerType) {
        return handlerType.getSimpleName();
    }

    private <T> T getInjectableBeanFromArgument(final Argument<T> argument) {
        if (argument.getAnnotation(Qualifier.class) == null) {
            return beanContext.getBean(argument.getType());
        } else {
            return beanContext.getBean(argument.getType(), Qualifiers.byAnnotation(argument.getAnnotationMetadata(), Qualifier.class));
        }
    }

    protected class Invoker implements Function {
        private final ExecutableMethod method;
        private final List<Object> arguments;

        public Invoker(final ExecutableMethod method, final List<Object> arguments) {
            this.method = method;
            this.arguments = arguments;
        }

        @Override
        public Object apply(final Object command) {
            return method.invoke(command, arguments.toArray(new Object[0]));
        }
    }
}
