package nl.reinkrul.cqrslight.micronaut;

import javax.inject.Singleton;

@Singleton
public class MessagePrinter {

    public void print(String message) {
        System.out.println(message);
    }
}
