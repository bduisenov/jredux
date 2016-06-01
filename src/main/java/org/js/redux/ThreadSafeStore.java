package org.js.redux;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by bduisenov on 01/06/16.
 */
class ThreadSafeStore<S extends State, A extends Action> implements Store<S, A> {

    private S state;

    private final Reducer<S, A> reducer;

    private final Queue<Listener> listeners = new ConcurrentLinkedQueue<>();

    private ReentrantLock rl = new ReentrantLock(true);

    ThreadSafeStore(S state, Reducer<S, A> reducer) {
        this.state = state;
        this.reducer = reducer;
    }

    @Override
    public void dispatch(A action) {
        rl.lock();
        try {
            state = reducer.apply(state, action);
        } finally {
            rl.unlock();
        }
        listeners.forEach(Listener::onStateChanged);
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
