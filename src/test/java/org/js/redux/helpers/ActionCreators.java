package org.js.redux.helpers;

import static org.js.redux.helpers.ActionTypes.ADD_TODO;
import static org.js.redux.helpers.ActionTypes.DISPATCH_IN_MIDDLE;
import static org.js.redux.helpers.ActionTypes.THROW_ERROR;
import static org.js.redux.helpers.ActionTypes.UNKNOWN_ACTION;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.js.redux.Action;
import org.js.redux.ActionFunction;
import org.js.redux.Dispatch;
import org.js.redux.State;

/**
 * Created by bduisenov on 08/06/16.
 */
public class ActionCreators {

    public static Action addTodo(String text) {
        return Action.of(ADD_TODO, text);
    }

    public static ActionFunction<CompletableFuture<Action>> addTodoAsync(String text) {
        return new ActionFunction<CompletableFuture<Action>>() {

            @Override
            public CompletableFuture<Action> apply(Dispatch dispatch, Supplier<State> getState) {
                return CompletableFuture.completedFuture(text) //
                        .thenApply(text -> dispatch.apply(addTodo(text)));
            }
        };
    }

    public static ActionFunction<Action> addTodoIfEmpty(String text) {
        return new ActionFunction<Action>() {

            @Override
            public Action apply(Dispatch dispatch, Supplier<State> getState) {
                if (getState.get().isEmpty()) {
                    return dispatch.apply(addTodo(text));
                }
                return null;
            }
        };
    }

    public static Action dispatchInMiddle(Runnable boundDispatchFn) {
        return Action.of(DISPATCH_IN_MIDDLE, boundDispatchFn);
    }

    public static Action throwError() {
        return Action.of(THROW_ERROR);
    }

    public static Action unknownAction() {
        return Action.of(UNKNOWN_ACTION);
    }

}
