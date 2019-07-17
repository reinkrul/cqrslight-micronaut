package nl.reinkrul.cqrslight.micronaut;

import javax.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Singleton
public class CommandInvoker {

    private final Map<Class, Consumer<Object>> invokers = new HashMap<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    public Future invoke(final Object command) {
        final Class<?> type = command.getClass();
        final Consumer<Object> invoker = invokers.get(type);
        if (invoker == null) {
            throw new IllegalStateException("Command not registered: " + type);
        }
        executor.execute(() -> invoker.accept(command));
        return CompletableFuture.completedFuture("finished");
    }

    void addInvoker(final Class command, final Consumer<Object> invoker) {
        invokers.put(command, invoker);
    }
}
