package org.js.redux.helpers;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by bduisenov on 03/06/16.
 */
public class TodoActionBiConsumer extends TodoAction implements BiConsumer<Consumer<TodoAction>, Supplier<Todos>> {

    public TodoActionBiConsumer(ActionTypes type, String text) {
        super(type, text);
    }

    @Override
    public void accept(Consumer<TodoAction> dispatcher, Supplier<Todos> getState) {
        System.out.println("hi from biconsumer "  + getState.get() + " " );
    }
}
