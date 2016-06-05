package org.js.redux.helpers;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by bduisenov on 02/06/16.
 */
public class ActionCreators {

    public static TodoAction addTodo(String text) {
        return new TodoAction(ActionTypes.ADD_TODO, text);
    }

    // (dispatch) => Promise => resolve => dispatch(addTodo(text))
    public static Function<Function<TodoAction, TodoAction>, CompletableFuture<TodoAction>> addTodoAsync(String text) {
        return dispatch -> CompletableFuture.completedFuture(text) //
                .thenApply(text1 -> dispatch.apply(addTodo(text1)));
    }

    static class todoIfEmpty extends TodoAction implements BiFunction<Function<TodoAction, TodoAction>, Supplier<Todos>, TodoAction> {

        todoIfEmpty(String text) {
            this.text = text;
        }

        @Override
        public TodoAction apply(Function<TodoAction, TodoAction> dispatch, Supplier<Todos> getState) {
            if (getState.get() == null) {
                return dispatch.apply(addTodo(text));
            }
            return null;
        }
    }

    // (dispatch, getState) => dispatch(addTodo)
    public static todoIfEmpty addTodoIfEmpty(String text) {
        return new todoIfEmpty(text);
    }
}
