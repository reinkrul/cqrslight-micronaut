package nl.reinkrul.cqrslight.micronaut;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class HandlerInvoker {

    private final Map<Class, Function<?, ?>> invokers = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public <T, R> Future<R> invoke(final T command) {
        final Class<?> type = command.getClass();
        // TODO: Fix unsafe cast
        final Function<T, R> invoker = (Function<T, R>) invokers.get(type);
        if (invoker == null) {
            throw new IllegalStateException("Command not registered: " + type);
        }
        return executor.submit(() -> invoker.apply(command));
    }

    public void addInvoker(final Class command, final Function<?, ?> invoker) {
        invokers.put(command, invoker);
    }

    public Optional<Class> lookup(final String alias) {
        return invokers.keySet().stream().filter(c -> getAlias(c).equalsIgnoreCase(alias)).findFirst();
    }

    // TODO: Move this somewhere else
    public String getAlias(final Class command) {
        return command.getSimpleName();
    }
}
