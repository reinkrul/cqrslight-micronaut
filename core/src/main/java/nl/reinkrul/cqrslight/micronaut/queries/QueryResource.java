package nl.reinkrul.cqrslight.micronaut.queries;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.reinkrul.cqrslight.micronaut.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;

@Controller("/query")
public class QueryResource {

    private static final Logger LOG = LoggerFactory.getLogger(QueryResource.class);

    private final QueryInvoker queryInvoker;

    @Inject
    public QueryResource(final QueryInvoker queryInvoker) {
        this.queryInvoker = queryInvoker;
    }

    @Get("/{alias}")
    @Consumes
    @Produces
    public HttpResponse invoke(@PathVariable("alias") final String alias, final HttpRequest request) {
        final Optional<Class> type = queryInvoker.lookup(alias);
        if (type.isPresent()) {
            try {
                final Map<String, String> parameters = StreamSupport.stream(request.getParameters().spliterator(), false)
                                                                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
                final Object command = new ObjectMapper().convertValue(parameters, type.get());
                // TODO: Return future result to browser
                return HttpResponse.ok(queryInvoker.invoke(command).get());
            } catch (Throwable e) {
                LOG.warn("Unable to unmarshal query {}", type, e);
                return HttpResponse.badRequest(new ErrorResponse("Unable to read query."));
            }
        } else {
            LOG.warn("Unknown query was specified: {}", alias);
            return HttpResponse.badRequest(new ErrorResponse("Unknown query: " + alias));
        }
    }
}
