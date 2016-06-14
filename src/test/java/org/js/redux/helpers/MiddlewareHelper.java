package org.js.redux.helpers;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.js.redux.Action;
import org.js.redux.Dispatch;
import org.js.redux.MiddlewareAPI;
import org.js.redux.State;

/**
 * Created by bduisenov on 09/06/16.
 */
public class MiddlewareHelper {

    public static Function<Dispatch, Dispatch> thunk(MiddlewareAPI middlewareAPI) {
        return next -> new Dispatch() {

            @Override
            public <T> T apply(Action action) {
                if (action instanceof BiFunction) {
                    return ((BiFunction<Dispatch, Supplier<State>, T>) action).apply(middlewareAPI.dispatch(), middlewareAPI::getState);
                }
                return next.apply(action);
            }
        };
    }

}
