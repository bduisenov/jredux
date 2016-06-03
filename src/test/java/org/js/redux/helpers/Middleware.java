package org.js.redux.helpers;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.js.redux.Action;
import org.js.redux.State;

/**
 * Created by bduisenov on 02/06/16.
 */
public class Middleware {

    private Middleware() {
        //
    }

    public static <S extends State, A extends Action> Function<Consumer<A>, Function<A, A>> thunk(Consumer<A> dispatch, Supplier<S> getState) {
        return next -> action -> {
            next.accept(action);
            return action;
        };
    }

}
