package org.js.redux.helpers;

import java.util.function.BiConsumer;
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
            System.out.println("thunk");
            if (action instanceof BiConsumer) ((BiConsumer)action).accept(dispatch, getState);
            else next.accept((X)action);
        };
    }

    public static void main(String[] args) {
        Function<Consumer<Action>, Consumer<Action>> thunk = thunk(action -> System.out.println("action"), () -> null);
        Consumer<Action> next = thunk.apply(action -> {
            System.out.println(action);
        });
        next.accept(new Action() {

            @Override
            public String toString() {
                return "action";
            }
        });

    }

}
