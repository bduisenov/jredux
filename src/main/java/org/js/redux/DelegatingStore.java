package org.js.redux;

/**
 * Created by bduisenov on 14/06/16.
 */
public class DelegatingStore implements Store {

    protected final Store store;

    public DelegatingStore(Store store) {
        if (store == null) {
            throw new NullPointerException("store must not be null");
        }
        this.store = store;
    }

    @Override
    public Action dispatch(Action action) {
        return store.dispatch(action);
    }

    @Override
    public State getState() {
        return store.getState();
    }

    @Override
    public Subscription subscribe(Listener listener) {
        return store.subscribe(listener);
    }

    @Override
    public void replaceReducer(Reducer nextReducer) {
        store.replaceReducer(nextReducer);
    }
}
