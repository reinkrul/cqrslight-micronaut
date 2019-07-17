package nl.reinkrul.cqrslight.micronaut;

public class TestCommand {

    private String message;

    public TestCommand(String message) {
        this.message = message;
    }

    @CommandHandler
    public void handle(MessagePrinter messagePrinter) {
        messagePrinter.print(message);
    }
}
