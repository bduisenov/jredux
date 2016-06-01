package org.js.redux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bduisenov on 01/06/16.
 */
public class Redux {

    private Redux() {
        //
    }

    public static <S extends State, A extends Action> Store<S, A> createStore(S initial, Reducer<S, A> reducer) {
        if (reducer == null) {
            throw new NullPointerException("reducer must not be null");
        }

        return new Store<S, A>() {

            private S state = initial;

            private final List<Listener> listeners = new LinkedList<>();

            @Override
            public void dispatch(A action) {
                state = reducer.apply(state, action);
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
        };
    }

    public static <S extends State, A extends Action> Reducer<S, A> combineReducers(Reducer<S, A>... reducers) {
        return combineReducers(Arrays.asList(reducers));
    }

    public static <S extends State, A extends Action> Reducer<S, A> combineReducers(Iterator<Reducer<S, A>> reducers) {
        List<Reducer<S, A>> result = new ArrayList<>();
        reducers.forEachRemaining(result::add);
        return combineReducers(result);
    }

    public static <S extends State, A extends Action> Reducer<S, A> combineReducers(Collection<Reducer<S, A>> reducers) {
        return (state, action) -> reducers.stream() //
                .reduce(state, (acc, reducer) -> reducer.apply(acc, action), (acc, newState) -> newState);
    }

}
