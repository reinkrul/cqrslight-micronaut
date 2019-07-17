package nl.reinkrul.cqrslight.micronaut;

import javax.inject.Inject;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class TestResource {

    private final CommandInvoker commandInvoker;

    @Inject
    public TestResource(final CommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    @Get
    public void test() {
        commandInvoker.invoke(new TestCommand("hello!"));
    }
}
