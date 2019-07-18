package nl.reinkrul.cqrslight.micronaut.commands;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.reinkrul.cqrslight.micronaut.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Optional;
import java.util.UUID;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

@Controller("/command")
public class CommandResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommandResource.class);

    private final CommandInvoker commandInvoker;

    @Inject
    public CommandResource(final CommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    @Post("/{alias}")
    @Consumes
    @Produces
    public HttpResponse invoke(@PathVariable("alias") final String alias, @Body final String input) {
        final Optional<Class> commandType = commandInvoker.lookup(alias);
        if (commandType.isPresent()) {
            try {
                final Object command = new ObjectMapper().readValue(input, commandType.get());
                commandInvoker.invoke(command);
                return HttpResponse.created(UUID.randomUUID().toString());
            } catch (Throwable e) {
                LOG.warn("Unable to unmarshal command {}", commandType, e);
                return HttpResponse.badRequest(new ErrorResponse("Unable to read command."));
            }
        } else {
            LOG.warn("Unknown command was specified: {}", alias);
            return HttpResponse.badRequest(new ErrorResponse("Unknown command: " + alias));
        }
    }

}
