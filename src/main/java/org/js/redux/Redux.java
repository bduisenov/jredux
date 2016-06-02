package org.js.redux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by bduisenov on 01/06/16.
 */
public class Redux {

    private Redux() {
        //
    }

    public static <S extends State, A extends Action> Store<S, A> createStore(Params<S, A> params) {
        return createStore(params.reducer, params.preloadedState, params.enhancer);
    }

    /**
     * 
     * @param reducer
     *            A function that returns the next state tree, given the current state tree and the
     *            action to handle
     * @param preloadedState
     *            The initial state. You may optionally specify it to hydrate the state from the
     *            server in universal apps, or to restore a previously serialized user session. If
     *            you use `combineReducers` to produce the root reducer function, this must be an
     *            object with the same shape as `combineReducers` keys.
     * @return A Redux store that lets you read the state, dispatch actions and subscribe to
     *         changes.
     */
    public static <S extends State, A extends Action> Store<S, A> createStore(Reducer<S, A> reducer, S preloadedState) {
        return createStore(reducer, preloadedState, null);
    }

    /**
     * 
     * @param reducer
     *            A function that returns the next state tree, given the current state tree and the
     *            action to handle
     * @param preloadedState
     *            The initial state. You may optionally specify it to hydrate the state from the
     *            server in universal apps, or to restore a previously serialized user session. If
     *            you use `combineReducers` to produce the root reducer function, this must be an
     *            object with the same shape as `combineReducers` keys.
     * @param enhancer
     *            The store enhancer. You may optionally specify it to enhance the store with
     *            third-party capabilities such as middleware, time travel, persistence, etc. The
     *            only store enhancer that ships with Redux is `applyMiddleware()`.
     * @return A Redux store that lets you read the state, dispatch actions and subscribe to
     *         changes.
     */
    public static <S extends State, A extends Action> Store<S, A> createStore(Reducer<S, A> reducer, S preloadedState,
            Function<Function<S, A>, Store<S, A>> enhancer) {
        if (preloadedState instanceof Function && enhancer == null) {
            /*return ((Function<Function<S, A>, Store<S, A>>)preloadedState) //
                    .apply((Function<S, A>) s -> createStore(reducer, s));*/
        }

        return new SimpleStore<>(preloadedState, reducer);
    }

    public static <S extends State, A extends Action> Store<S, A> createConcurrentStore(Reducer<S, A> reducer,
            S preloadedState) {
        if (reducer == null) {
            throw new NullPointerException("reducer must not be null");
        }

        return new ThreadSafeStore<>(preloadedState, reducer);
    }

    @SafeVarargs
    public static <S extends State, A extends Action> Function<Function<Params<S, A>, Store<S, A>>, Function<Params<S, A>, Store<S, A>>> applyMiddleware(
            Function<MiddlewareAPI<S, A>, Consumer<Consumer<MiddlewareAPI<S, A>>>>... middlewares) {
        if (middlewares == null) {
            throw new NullPointerException("middlewares must not be null");
        }
        return createStore -> params -> {
            Store<S, A> store = createStore.apply(params);
            Consumer<A> dispatch = store::dispatch;
            MiddlewareAPI<S, A> middlewareAPI = new MiddlewareAPI<>(dispatch, store::getState);

            List<Consumer<Consumer<MiddlewareAPI<S, A>>>> chain = Arrays.asList(middlewares).stream()//
                    .map(middleware -> middleware.apply(middlewareAPI)).collect(Collectors.toList());

            return new Store<S, A>() {

                @Override
                public S getState() {
                    return store.getState();
                }

                @Override
                public void dispatch(A action) {
                    chain.forEach(c -> c.accept());
                }

                @Override
                public Subscription subscribe(Listener listener) {
                    return store.subscribe(listener);
                }
            };
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
        List<Reducer<S, A>> rs = new ArrayList<>(reducers);
        return (state, action) -> rs.stream() //
                .reduce(state, (acc, reducer) -> reducer.apply(acc, action), (acc, newState) -> newState);
    }

}
