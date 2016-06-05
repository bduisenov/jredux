package org.js.redux;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Created by bduisenov on 01/06/16.
 */
class SimpleStore<S extends State, A extends Action> implements Store<S, A> {

    private S currentState;

    private final Reducer<S, A> currentReducer;

    private final List<Listener> listeners = new LinkedList<>();

    private final AtomicBoolean isDispatching = new AtomicBoolean(false);

    SimpleStore(S state, Reducer<S, A> reducer) {
        this.currentState = state;
        this.currentReducer = reducer;
    }

    @Override
    public A dispatch(A action) {
        if (isDispatching.get()) {
            throw new IllegalStateException("Reducers may not dispatch actions.");
        }
        isDispatching.set(true);
        try {
            currentState = currentReducer.apply(currentState, action);
        } finally {
            isDispatching.set(false);
        }
        listeners.forEach(Listener::onStateChanged);

        return action;
    }

    @Override
    public <R> R dispatch(Function<Function<A, A>, R> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public S getState() {
        return currentState;
    }

    @Override
    public Subscription subscribe(Listener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

}
