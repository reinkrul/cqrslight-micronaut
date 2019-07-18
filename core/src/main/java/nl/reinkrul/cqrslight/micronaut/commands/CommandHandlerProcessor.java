package nl.reinkrul.cqrslight.micronaut.commands;

import nl.reinkrul.cqrslight.micronaut.HandlerProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.micronaut.context.BeanContext;

@Singleton
public class CommandHandlerProcessor extends HandlerProcessor<CommandHandler> {

    private final CommandInvoker commandInvoker;

    @Inject
    public CommandHandlerProcessor(final BeanContext beanContext, final CommandInvoker commandInvoker) {
        super(beanContext, CommandHandler.class);
        this.commandInvoker = commandInvoker;
    }

    @Override
    protected void registerInvoker(final Class<?> handlerType, final Invoker invoker) {
        commandInvoker.addInvoker(handlerType, invoker);
    }

    @Override
    protected boolean handlerExists(final String alias) {
        return commandInvoker.lookup(alias).isPresent();
    }
}
