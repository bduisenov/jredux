package org.js.redux;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by bduisenov on 01/06/16.
 */
class ThreadSafeStore<S extends State, A extends Action> implements Store<S, A> {

    private S state;

    private final Reducer<S, A> reducer;

    private final Queue<Listener> listeners = new ConcurrentLinkedQueue<>();

    private ReadWriteLock rwl = new ReentrantReadWriteLock(true);

    ThreadSafeStore(S state, Reducer<S, A> reducer) {
        this.state = state;
        this.reducer = reducer;
    }

    @Override
    public void dispatch(A action) {
        rwl.writeLock().lock();
        try {
            state = reducer.apply(state, action);
        } finally {
            rwl.writeLock().unlock();
        }
        listeners.forEach(Listener::onStateChanged);
    }

    @Override
    public S getState() {
        rwl.readLock().lock();
        S state = this.state;
        rwl.readLock().unlock();
        return state;
    }

    @Override
    public Subscription subscribe(Listener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
