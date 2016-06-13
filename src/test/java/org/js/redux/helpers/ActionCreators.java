package org.js.redux.helpers;

import static org.js.redux.helpers.ActionTypes.ADD_TODO;
import static org.js.redux.helpers.ActionTypes.DISPATCH_IN_MIDDLE;
import static org.js.redux.helpers.ActionTypes.THROW_ERROR;
import static org.js.redux.helpers.ActionTypes.UNKNOWN_ACTION;

import org.js.redux.Action;

/**
 * Created by bduisenov on 08/06/16.
 */
public class ActionCreators {

    public static Action addTodo(String text) {
        return Action.of(ADD_TODO, text);
    }

    //TODO addTodoAsync

    //TODO addTodoIfEmpty

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
