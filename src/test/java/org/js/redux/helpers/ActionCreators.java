package org.js.redux.helpers;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by bduisenov on 02/06/16.
 */
public class ActionCreators {

    public static TodoAction addTodo(String text) {
        return new TodoAction(ActionTypes.ADD_TODO, text);
    }

    static class todoAsync extends TodoAction implements Consumer<Consumer<TodoAction>> {

        todoAsync(String text) {
            this.text = text;
        }

        @Override
        public void accept(Consumer<TodoAction> dispatch) {
            CompletableFuture.completedFuture(text) //
                    .thenAccept(text1 -> dispatch.accept(addTodo(text1)));
        }
    }

    // (dispatch) => Promise => resolve => dispatch(addTodo(text))
    public static TodoAction addTodoAsync(String text) {
        return new todoAsync(text);
    }

    // (dispatch, getState) => dispatch(addTodo)
    public static BiConsumer<Consumer<TodoAction>, Supplier<Todos>> addTodoIfEmpty(String text) {
        return (dispatch, getState) -> {
            if (getState.get() == null) {
                dispatch.accept(addTodo(text));
            }
        };
    }
}
