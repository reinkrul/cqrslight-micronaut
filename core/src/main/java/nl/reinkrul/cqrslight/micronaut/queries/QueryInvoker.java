package nl.reinkrul.cqrslight.micronaut.queries;

import nl.reinkrul.cqrslight.micronaut.HandlerInvoker;

import javax.inject.Singleton;

import java.util.concurrent.Future;

@Singleton
public class QueryInvoker extends HandlerInvoker {

    public <R> Future<R> invoke(final Query<R> query) {
        return super.invoke(query);
    }
}
