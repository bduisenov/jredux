package org.js.redux.helpers;

import java.util.function.Consumer;

import org.js.redux.Action;
import org.js.redux.MiddlewareAPI;
import org.js.redux.State;

/**
 * Created by bduisenov on 02/06/16.
 */
public class Middleware {

    private Middleware() {
        //
    }

    public static <S extends State, A extends Action> Consumer<Consumer<MiddlewareAPI<S, A>>> thunk(MiddlewareAPI<S, A> middlewareAPI) {
        return action -> action.accept(middlewareAPI);
    }

}
