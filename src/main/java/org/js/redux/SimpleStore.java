package org.js.redux;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bduisenov on 01/06/16.
 */
class SimpleStore<S extends State, A extends Action> implements Store<S, A> {

    private S state;

    private final Reducer<S, A> reducer;

    private final List<Listener> listeners = new LinkedList<>();

    private final AtomicBoolean isDispatching = new AtomicBoolean(false);

    SimpleStore(S state, Reducer<S, A> reducer) {
        this.state = state;
        this.reducer = reducer;
    }

    @Override
    public void dispatch(A action) {
        if (isDispatching.get()) {
            throw new IllegalStateException("#dispatch() was called in another thread. This implementation is not thread safe.");
        }
        isDispatching.set(true);
        state = reducer.apply(state, action);
        listeners.forEach(Listener::onStateChanged);
        isDispatching.set(false);
    }

    @Override
    public S getState() {
        return state;
    }

    @Override
    public Subscription subscribe(Listener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

}
