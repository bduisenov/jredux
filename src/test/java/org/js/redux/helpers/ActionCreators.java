package org.js.redux.helpers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by bduisenov on 02/06/16.
 */
public class ActionCreators {

    public static TodoAction addTodo(String text) {
        return new TodoAction(ActionTypes.ADD_TODO, text);
    }

    public static Function<Consumer<TodoAction>, Future<Void>> addTodoAsync(String text) {
        return dispatch -> CompletableFuture.completedFuture(text) //
                .thenAccept(text1 -> dispatch.accept(addTodo(text1)));
    }

}
