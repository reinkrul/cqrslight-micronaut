package nl.reinkrul.cqrslight.micronaut;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.reinkrul.cqrslight.micronaut.commands.CommandHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.event.ApplicationEventPublisher;

public class CreateTodoItemCommand {

    private static final Logger LOG = LoggerFactory.getLogger(CreateTodoItemCommand.class);

    private final String name;

    @JsonCreator
    public CreateTodoItemCommand(@JsonProperty("name") final String name) {
        this.name = name;
    }

    @CommandHandler
    public void handle(final ApplicationEventPublisher eventPublisher) {
        LOG.info("Item created: {}", name);
        eventPublisher.publishEvent(new ItemAddedEvent(this, name));
    }
}
