package org.js.redux.helpers;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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

    public static <S extends State, A extends Action, X> Function<Consumer<X>, Consumer<A>> thunk(Consumer<A> dispatch, Supplier<S> getState) {
        return next -> action -> {
            if (action instanceof BiConsumer) ((BiConsumer)action).accept(dispatch, getState);
            if (action instanceof BiFunction) ((BiFunction)action).apply(dispatch, getState);
            if (action instanceof Consumer) ((Consumer)action).accept(dispatch);
            if (action instanceof Function) ((Function)action).apply(dispatch);
            else next.accept((X)action);
        };
    }

}
