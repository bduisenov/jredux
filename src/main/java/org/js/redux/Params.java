package org.js.redux;

import java.util.function.Function;

/**
 * Created by bduisenov on 02/06/16.
 */
public class Params<S extends State, A extends Action> {

    public final Reducer<S, A> reducer;

    public final S preloadedState;

    public final Function<Function<S, A>, Store<S, A>> enhancer;

    public Params(Reducer<S, A> reducer, S preloadedState, Function<Function<S, A>, Store<S, A>> enhancer) {
        this.reducer = reducer;
        this.preloadedState = preloadedState;
        this.enhancer = enhancer;
    }

    public Params(Reducer<S, A> reducer, S preloadedState) {
        this.reducer = reducer;
        this.preloadedState = preloadedState;
        this.enhancer = null;
    }

    public Params(Reducer<S, A> reducer) {
        this.reducer = reducer;
        this.preloadedState = null;
        this.enhancer = null;
    }

}
