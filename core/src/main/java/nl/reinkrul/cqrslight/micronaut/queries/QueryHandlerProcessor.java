package nl.reinkrul.cqrslight.micronaut.queries;

import nl.reinkrul.cqrslight.micronaut.HandlerProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.micronaut.context.BeanContext;

@Singleton
public class QueryHandlerProcessor extends HandlerProcessor<QueryHandler> {

    private final QueryInvoker queryInvoker;

    @Inject
    public QueryHandlerProcessor(final BeanContext beanContext, final QueryInvoker queryInvoker) {
        super(beanContext, QueryHandler.class);
        this.queryInvoker = queryInvoker;
    }

    @Override
    protected void registerInvoker(Class<?> handlerType, Invoker invoker) {
        queryInvoker.addInvoker(handlerType, invoker);
    }

    @Override
    protected boolean handlerExists(String alias) {
        return queryInvoker.lookup(alias).isPresent();
    }
}
