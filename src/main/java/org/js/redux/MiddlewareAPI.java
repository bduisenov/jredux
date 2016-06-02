package org.js.redux;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by bduisenov on 02/06/16.
 */
public class MiddlewareAPI<S extends State, A extends Action> {

    public final Supplier<S> getState;

    public final Consumer<A> dispatch;

    public MiddlewareAPI(Consumer<A> dispatch, Supplier<S> getState) {
        this.dispatch = dispatch;
        this.getState = getState;
    }

}
