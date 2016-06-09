package org.js.redux.helpers;

import java.util.function.Function;

import org.js.redux.Dispatch;
import org.js.redux.MiddlewareAPI;

/**
 * Created by bduisenov on 09/06/16.
 */
public class MiddlewareHelper {

    public static Function<Dispatch, Dispatch> thunk(MiddlewareAPI middlewareAPI) {
        return next -> action -> {
            // TODO action === 'function'
            return next.apply(action);
        };
    }

}
