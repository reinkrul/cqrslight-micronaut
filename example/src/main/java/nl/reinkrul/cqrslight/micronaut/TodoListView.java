package nl.reinkrul.cqrslight.micronaut;

import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import io.micronaut.context.event.ApplicationEventListener;

@Singleton
public class TodoListView implements ApplicationEventListener<ItemAddedEvent> {

    private final List<String> items = new ArrayList<>();

    public TodoListView() {
        items.add("Default item 1");
        items.add("Default item 2");
    }

    public List<String> get() {
        return items;
    }

    @Override
    public void onApplicationEvent(final ItemAddedEvent event) {
        items.add(event.getName());
    }

    @Override
    public boolean supports(final ItemAddedEvent event) {
        return true;
    }
}
