package org.js.redux.helpers;

import java.util.function.BiFunction;
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

    public static <S extends State, A extends Action, X, Y> Function<Function<X, A>, Function<A, A>> thunk(Function<A, A> dispatch, Supplier<S> getState) {
        return new Function<Function<X, A>, Function<A, A>>() {

            @Override
            public Function<A, A> apply(Function<X, A> next) {
                return new Function<A, A>() {

                    @Override
                    public A apply(A action) {
                        if (action instanceof BiFunction) return (A)((BiFunction)action).apply(dispatch, getState);
                        if (action instanceof Function) return (A)((Function)action).apply(dispatch);
                        return next.apply((X)action);
                    }
                };
            }
        };
        /*
        return next -> action -> {
            if (action instanceof BiFunction) return (A)((BiFunction)action).apply(dispatch, getState);
            if (action instanceof Function) return (A)((Function)action).apply(dispatch);
            return next.apply((X)action);
        };
*/
    }

}
