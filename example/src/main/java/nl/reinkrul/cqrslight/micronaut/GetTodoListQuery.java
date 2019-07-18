package nl.reinkrul.cqrslight.micronaut;

import nl.reinkrul.cqrslight.micronaut.queries.Query;
import nl.reinkrul.cqrslight.micronaut.queries.QueryHandler;

import java.util.List;

public class GetTodoListQuery extends Query<List<String>> {

    @QueryHandler
    public List<String> invoke(final TodoListView view) {
        return view.get();
    }
}
