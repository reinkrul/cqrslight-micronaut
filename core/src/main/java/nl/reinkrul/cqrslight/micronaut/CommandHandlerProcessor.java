package nl.reinkrul.cqrslight.micronaut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.micronaut.context.BeanContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.inject.qualifiers.Qualifiers;

@Singleton
public class CommandHandlerProcessor implements ExecutableMethodProcessor<CommandHandler> {

    private static final Logger LOG = LoggerFactory.getLogger(CommandHandlerProcessor.class);

    private final BeanContext beanContext;
    private final CommandInvoker commandInvoker;

    @Inject
    public CommandHandlerProcessor(final BeanContext beanContext, final CommandInvoker commandInvoker) {
        this.beanContext = beanContext;
        this.commandInvoker = commandInvoker;
    }

    @Override
    public void process(final BeanDefinition<?> beanDefinition, final ExecutableMethod<?, ?> method) {
        final Class<?> commandType = beanDefinition.getBeanType();
        LOG.info("Registering command: {}", commandType);

        final List<Object> arguments = Arrays.stream(method.getArguments())
                                             .map(argument -> {
                                                 final Object bean = getArgument(argument);
                                                 if (bean == null) {
                                                     throw new IllegalStateException("Could not find bean to inject for argument of type " + argument
                                                             .getType() + " for command " + beanDefinition);
                                                 }
                                                 return bean;
                                             }).collect(Collectors.toList());

        commandInvoker.addInvoker(commandType, new Invoker(method, arguments));
    }

    private <T> T getArgument(final Argument<T> argument) {
        if (argument.getAnnotation(Qualifier.class) == null) {
            return beanContext.getBean(argument.getType());
        } else {
            return beanContext.getBean(argument.getType(), Qualifiers.byAnnotation(argument.getAnnotationMetadata(), Qualifier.class));
        }
    }

    private class Invoker implements Consumer<Object> {
        private final ExecutableMethod method;
        private final List<Object> arguments;

        public Invoker(final ExecutableMethod method, final List<Object> arguments) {
            this.method = method;
            this.arguments = arguments;
        }

        @Override
        public void accept(final Object command) {
            method.invoke(command, arguments.toArray(new Object[0]));
        }
    }
}
