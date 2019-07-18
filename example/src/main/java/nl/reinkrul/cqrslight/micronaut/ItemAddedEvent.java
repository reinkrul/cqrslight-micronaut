package nl.reinkrul.cqrslight.micronaut;

import io.micronaut.context.event.ApplicationEvent;

public class ItemAddedEvent extends ApplicationEvent {

    private final String name;

    public ItemAddedEvent(final Object source, final String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
