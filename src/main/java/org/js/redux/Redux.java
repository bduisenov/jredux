package org.js.redux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
            /*
             * return ((Function<Function<S, A>, Store<S, A>>)preloadedState) // .apply((Function<S,
             * A>) s -> createStore(reducer, s));
             */
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

            return new Store<S, A>() {

                @Override
                public S getState() {
                    return store.getState();
                }

                @Override
                public void dispatch(A action) {
                    //chain.
                }

                @Override
                public Subscription subscribe(Listener listener) {
                    return store.subscribe(listener);
                }
            };
        };
    }

    /**
     * Composes single-argument functions from right to left. The rightmost function can take
     * multiple arguments as it provides the signature for the resulting composite function.
     * 
     * @param func1
     *            The functions to compose.
     * @return A function obtained by composing the argument functions from right to left. For
     *         example, compose(f, g, h) is identical to doing (...args) => f(g(h(...args))).
     */

    public static <A> Function<A, A> compose(Function<A, A> func1) {
        return func1;
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<X, A> func2) {
        return args -> foldRight(func2.apply(args), Arrays.asList(func1));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<X, A> func3) {
        return args -> foldRight(func3.apply(args), Arrays.asList(func1, func2));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3, Function<X, A> func4) {
        return args -> foldRight(func4.apply(args), Arrays.asList(func1, func2, func3));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3, Function<A, A> func4, Function<X, A> func5) {
        return args -> foldRight(func5.apply(args), Arrays.asList(func1, func2, func3, func4));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3, Function<A, A> func4, Function<A, A> func5, Function<X, A> func6) {
        return args -> foldRight(func6.apply(args), Arrays.asList(func1, func2, func3, func4, func5));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3, Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<X, A> func7) {
        return args -> foldRight(func7.apply(args), Arrays.asList(func1, func2, func3, func4, func5, func6));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3, Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<A, A> func7, Function<X, A> func8) {
        return args -> foldRight(func8.apply(args), Arrays.asList(func1, func2, func3, func4, func5, func6, func7));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3, Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<A, A> func7, Function<A, A> func8, Function<X, A> func9) {
        return args -> foldRight(func9.apply(args), Arrays.asList(func1, func2, func3, func4, func5, func6, func7, func8));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3, Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<A, A> func7, Function<A, A> func8, Function<A, A> func9, Function<X, A> func10) {
        return args -> foldRight(func10.apply(args), Arrays.asList(func1, func2, func3, func4, func5, func6, func7, func8, func9));
    }

    @SafeVarargs
    public static <A> Function<A, A> compose(Function<A, A>... funcs) {
        return args -> foldRight(args, Arrays.asList(funcs));
    }

    private static <A> A foldRight(A acc, List<Function<A, A>> xs) {
        if (xs.isEmpty()) {
            return acc;
        } else if (xs.size() == 1) {
            return xs.get(0).apply(acc);
        } else {
            Function<A, A> last = xs.get(xs.size() - 1);
            return foldRight(last.apply(acc), xs.subList(0, xs.size() - 1));
        }
    }

    private static <S> S foldLeft(S acc, List<Function<S, S>> xs) {
        if (xs.isEmpty()) {
            return acc;
        } else {
            Function<S, S> head = xs.get(0);
            return foldLeft(head.apply(acc), xs.subList(1, xs.size() - 1));
        }
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
