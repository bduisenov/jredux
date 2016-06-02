package org.js.redux.helpers;

import java.util.function.Consumer;
import java.util.function.Function;

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

    public static <S extends State, A extends Action> Function<Consumer<A>, Consumer<A>> thunk(MiddlewareAPI<S, A> middlewareAPI) {
        return new Function<Consumer<A>, Consumer<A>>() {

            @Override
            public Consumer<A> apply(Consumer<A> aConsumer) {
                return aConsumer.accept();
            }
        };
    }

}
