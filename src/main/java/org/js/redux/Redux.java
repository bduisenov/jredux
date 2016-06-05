package org.js.redux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.js.redux.utils.TypeResolver;

/**
 * Created by bduisenov on 01/06/16.
 */
public class Redux {

    private static String undefinedStateErrorMessage = "Given action %s, reducer %s returned undefined. "
            + "To ignore an action, you must explicitly return the previous state.";

    private static String undefinedInitialStateMessage = "Reducer %s returned undefined during initialization. "
            + "If the state passed to the reducer is undefined, you must "
            + "explicitly return the initial state. The initial state may " + "not be undefined";

    private static String unexpectedStateShapeWarningMessage = "Store does not have a valid reducer. Make sure "
            + "the argument passed  to combineReducers is an object whose values are reducers.";

    private Redux() {
        //
    }

    /*public static <S extends State, A extends Action> Store<S, A> createStore(S state, A action) {
        return createStore(params.reducer, params.preloadedState);
    }*/

    public static <S extends State, A extends Action> Store<S, A> createStore(Reducer<S, A> reducer) {
        return createStore(reducer, null, null);
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

    public static <S extends State, A extends Action> Store<S, A> createStore(
            Reducer<S, A> reducer,
            Enhancer<S, A> enhancer) {
        return createStore(reducer, null, enhancer);
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
    public static <S extends State, A extends Action> Store<S, A> createStore(Reducer<S, A> reducer, S preloadedState, Enhancer<S, A> enhancer) {
        if (enhancer != null) {
             return enhancer.apply(Redux::createStore).apply(reducer, preloadedState);
        }

        return new SimpleStore<>(preloadedState, reducer);
    }

    public static <S extends State, A extends Action> Enhancer<S, A> applyMiddleware(
            BiFunction<Function<A, A>, Supplier<S>, Function<Function<A, A>, Function<A, A>>> middleware1) {
        if (middleware1 == null) {
            throw new NullPointerException("middlewares must not be null");
        }
        return createStore -> (reducer, action) -> {
            Store<S, A> store =  createStore.apply(reducer, action);
            return new Store<S, A>() {

                @Override
                public S getState() {
                    return store.getState();
                }

                @Override
                public A dispatch(A action) {
                    return compose( //
                            middleware1.apply(this::dispatch, store::getState)) //
                            .apply(store::dispatch) //
                            .apply(action);
                }

                @Override
                public <R> R dispatch(Function<Function<A, A>, R> action) {
                    return action.apply(this::dispatch);
                }

                @Override
                public Subscription subscribe(Listener listener) {
                    return store.subscribe(listener);
                }
            };
        };
    }

    public static <S extends State, A extends Action, T, R> Enhancer<S, A> applyMiddleware(
            BiFunction<Function<A, A>, Supplier<S>, Function<Function<A, A>, Function<A, A>>> middleware1,
            BiFunction<Function<A, A>, Supplier<S>, Function<Function<A, A>, Function<A, A>>> middleware2) {
        if (middleware1 == null || middleware2 == null) {
            throw new NullPointerException("middlewares must not be null");
        }
        return createStore -> (reducer, action) -> {
            Store<S, A> store = createStore.apply(reducer, action);
            return new Store<S, A>() {

                @Override
                public S getState() {
                    return store.getState();
                }

                @Override
                public <R> R dispatch(Function<Function<A, A>, R> action) {
                    compose( //
                            middleware1.apply(store::dispatch, store::getState), //
                            middleware2.apply(store::dispatch, store::getState)) //
                            .apply(store::dispatch); //
                    return action.apply(this::dispatch);
                }

                @Override
                public A dispatch(A action) {
                    return compose( //
                            middleware1.apply(store::dispatch, store::getState), //
                            middleware2.apply(store::dispatch, store::getState)) //
                            .apply(store::dispatch) //
                            .apply(action);
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

    public static <X, A> Function<X, A> compose(Function<X, A> func1) {
        return func1;
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<X, A> func2) {
        return args -> foldRight(func2.apply(args), Collections.singletonList(func1));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<X, A> func3) {
        return args -> foldRight(func3.apply(args), Arrays.asList(func1, func2));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3,
            Function<X, A> func4) {
        return args -> foldRight(func4.apply(args), Arrays.asList(func1, func2, func3));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3,
            Function<A, A> func4, Function<X, A> func5) {
        return args -> foldRight(func5.apply(args), Arrays.asList(func1, func2, func3, func4));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3,
            Function<A, A> func4, Function<A, A> func5, Function<X, A> func6) {
        return args -> foldRight(func6.apply(args), Arrays.asList(func1, func2, func3, func4, func5));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3,
            Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<X, A> func7) {
        return args -> foldRight(func7.apply(args), Arrays.asList(func1, func2, func3, func4, func5, func6));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3,
            Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<A, A> func7, Function<X, A> func8) {
        return args -> foldRight(func8.apply(args), Arrays.asList(func1, func2, func3, func4, func5, func6, func7));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3,
            Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<A, A> func7, Function<A, A> func8,
            Function<X, A> func9) {
        return args -> foldRight(func9.apply(args), Arrays.asList(func1, func2, func3, func4, func5, func6, func7, func8));
    }

    public static <X, A> Function<X, A> compose(Function<A, A> func1, Function<A, A> func2, Function<A, A> func3,
            Function<A, A> func4, Function<A, A> func5, Function<A, A> func6, Function<A, A> func7, Function<A, A> func8,
            Function<A, A> func9, Function<X, A> func10) {
        return args -> foldRight(func10.apply(args),
                Arrays.asList(func1, func2, func3, func4, func5, func6, func7, func8, func9));
    }

    @SafeVarargs
    public static <A> Function<A, A> compose(Function<A, A>... funcs) {
        return compose(Arrays.asList(funcs));
    }

    public static <A> Function<A, A> compose(List<Function<A, A>> funcs) {
        return args -> foldRight(args, funcs);
    }

    public static <X, A> Function<X, A> compose(List<Function<A, A>> funcs, Function<X, A> func) {
        return args -> foldRight(func.apply(args), funcs);
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

    @SafeVarargs
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
        RuntimeException sanityError = assertReducerSanity(rs);
        RuntimeException errorMessage = (reducers.isEmpty()) ? new IllegalStateException(unexpectedStateShapeWarningMessage)
                : null;
        return (state, action) -> {
            throwIfPresent(sanityError);
            throwIfPresent(errorMessage);
            return rs.stream() //
                    .reduce(state, //
                            (acc, reducer) -> checkReducerResult(reducer.apply(acc, action),
                                    String.format(undefinedStateErrorMessage, action, acc)), //
                            (acc, newState) -> newState);
        };
    }

    private static <S extends State, A extends Action> IllegalStateException assertReducerSanity(
            List<Reducer<S, A>> reducers) {
        for (Reducer<S, A> reducer : reducers) {
            S initialState = reducer.apply(null, getInitAction(reducer));
            if (initialState == null) {
                return new IllegalStateException(String.format(undefinedInitialStateMessage, reducer));
            }
        }
        return null;
    }

    private static <S extends State, A extends Action> A getInitAction(Reducer<S, A> reducer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(Reducer.class, reducer.getClass());
        Class<?> actionType = typeArgs[1];
        if (actionType.isEnum()) {
            for (A action : (A[]) actionType.getEnumConstants()) {
                try {
                    return (A) action.getClass().getMethod("valueOf", String.class).invoke(null, "INIT");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                return (A) actionType.newInstance();
            } catch (Exception e) {
                doThrow(e);
            }
        }
        return null;
    }

    private static <T> T checkNotNull(T reference, String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
        return reference;
    }

    private static <T> T checkReducerResult(T reference, String message) {
        if (reference == null) {
            throw new IllegalStateException(message);
        }
        return reference;
    }

    private static void throwIfPresent(Exception e) {
        if (e != null) {
            doThrow(e);
        }
    }

    private static void doThrow(Exception e) {
        Redux.<RuntimeException>doThrow0(e);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void doThrow0(Exception e) throws E {
        throw (E) e;
    }

}
