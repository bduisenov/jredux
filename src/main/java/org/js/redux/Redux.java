package org.js.redux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bduisenov on 01/06/16.
 */
public class Redux {

    private Redux() {
        //
    }

    public static <S extends State, A extends Action> Store<S, A> createSimpleStore(S initial, Reducer<S, A> reducer) {
        if (reducer == null) {
            throw new NullPointerException("reducer must not be null");
        }

        return new SimpleStore<>(initial, reducer);
    }

    public static <S extends State, A extends Action> Store<S, A> createConcurrentStore(S initial, Reducer<S, A> reducer) {
        if (reducer == null) {
            throw new NullPointerException("reducer must not be null");
        }

        return new ThreadSafeStore<>(initial, reducer);
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
        List<Reducer<S, A>> rs = new ArrayList<>(reducers);
        return (state, action) -> rs.stream() //
                .reduce(state, (acc, reducer) -> reducer.apply(acc, action), (acc, newState) -> newState);
    }

}
