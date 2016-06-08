package org.js.redux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.sun.istack.internal.Nullable;

/**
 * Created by bduisenov on 05/06/16.
 */
public class Redux {

    private static final String undefinedStateErrorMessage = "Given action \"%s\", reducer \"%s\" returned undefined. "
            + "To ignore an action, you must explicitly return the previous state.";

    private static final String unexpectedStateShapeWarningMessage = "Store does not have a valid reducer. Make sure the argument passed "
            + "to combineReducers is an object whose values are reducers.";

    private static final String undefinedInitialStateMessage = "Reducer \"%s\" returned undefined during initialization. "
            + "If the state passed to the reducer is undefined, you must "
            + "explicitly return the initial state. The initial state may not be undefined.";

    enum ActionTypes {
        INIT;
    }
    
    private Redux() {
        //
    }

    /**
     * Turns an object whose values are different add functions, into a single add function.
     * It will call every child add, and gather their results into a single state object, whose
     * keys correspond to the keys of the passed add functions.
     * 
     * @param reducers
     *            An object whose values correspond to different add functions that need to be
     *            combined into one. One handy way to obtain it is to use ES6 `import * as reducers`
     *            syntax. The reducers may never return undefined for any action. Instead, they
     *            should return their initial state if the state passed to them was undefined, and
     *            the current state for any unrecognized action.
     * @param <S>
     *            Combined state object type.
     *
     * @return A add function that invokes every add inside the passed object, and builds a
     *         state object with the same shape.
     */
    public static <R extends ReducersMapObject> Reducer combineReducers(R reducers) {
        RuntimeException sanityError = assertReducerSanity(reducers.getReducers());

        return (state, action) -> {
            if (state == null) {
                state = State.empty();
            }
            if (sanityError != null) {
                throw sanityError;
            }

            String warningMessage = getUnexpectedStateShapeWarningMessage(state, reducers.getReducers(), action);
            if (warningMessage != null) {
                warning(warningMessage);
            }

            boolean hasChanged = false;
            Map<Enum<?>, Object> nextState = new LinkedHashMap<>();
            for (Map.Entry<Enum<?>, BiFunction<Object, Action, Object>> entry : reducers.getReducers().entrySet()) {
                Enum<?> key = entry.getKey();
                BiFunction<Object, Action, Object> reducer = entry.getValue();

                Class<?> stateType = reducers.getTypes().get(key);

                Object previousStateForKey = state.get(key, stateType);
                Object nextStateForKey = reducer.apply(previousStateForKey, action);
                if (nextStateForKey == null) {
                    String errorMessage = getUndefinedStateErrorMessage(key, action);
                    throw new RuntimeException(errorMessage);
                }
                nextState.put(key, nextStateForKey);
                hasChanged = hasChanged || nextStateForKey != previousStateForKey;
            }
            return hasChanged ? State.of(nextState) : state;
        };
    }

    private static void warning(String warningMessage) {
        //FIXME
    }

    @Nullable
    private static String getUnexpectedStateShapeWarningMessage(State state,
            Map<Enum<?>, BiFunction<Object, Action, Object>> reducers, Action action) {
        if (reducers.isEmpty()) {
            return unexpectedStateShapeWarningMessage;
        }
        return null;
    }

    private static String getUndefinedStateErrorMessage(Enum<?> key, Action action) {
        String actionName = (action != null && action.type != null) ? action.type.toString() : "an action";
        return String.format(undefinedStateErrorMessage, actionName, key);
    }

    @Nullable
    private static RuntimeException assertReducerSanity(Map<Enum<?>, BiFunction<Object, Action, Object>> finalReducers) {
        RuntimeException result = null;
        try {
            List<IllegalStateException> exceptions = new ArrayList<>();
            finalReducers.forEach((key, reducer) -> {
                Object initialValue = reducer.apply(null, Action.of(ActionTypes.INIT));
                if (initialValue == null) {
                    IllegalStateException e = new IllegalStateException(String.format(undefinedInitialStateMessage, key));
                    exceptions.add(e);
                }
            });
            if (!exceptions.isEmpty()) {
                IllegalStateException e = new IllegalStateException();
                exceptions.forEach(e::addSuppressed);
                result = e;
            }
        } catch (RuntimeException e) {
            result = e;
        }
        return result;
    }

    /**
     * chain[function(next) => action =>]
     * dispatch = compose(...chain)(store.dispatch) === function(action) => {}
     *
     * @param middlewares
     * @return
     */
    public static GenericStoreEnhancer applyMiddleware(Middleware... middlewares) {
        return null;
    }

    /* compose */

    /**
     * Composes single-argument functions from right to left. The rightmost function can take
     * multiple arguments as it provides the signature for the resulting composite function.
     *
     * @param funcs
     *            The functions to compose.
     * @returns function obtained by composing the argument functions from right to left. For
     *          example, `compose(f, g, h)` is identical to doing `(...args) => f(g(h(...args)))`.
     */
    public static <R> Function<List<R>, R> composeList() {
        return rs -> rs.get(0);
    }

    public static <R> Function<R, R> compose() {
        return r -> r;
    }

    public static <A, R> Function<A, R> compose(Function<A, R> f1) {
        return f1;
    }

    public static <A, B, R> Function<A, R> compose(Function<B, R> f1, Function<A, B> f2) {
        return (args) -> compose(f1).apply(f2.apply(args));
    }

    public static <A, B, C, R> Function<A, R> compose(Function<C, R> f1, Function<B, C> f2, Function<A, B> f3) {
        return (args) -> compose(f1, f2).apply(f3.apply(args));
    }

    public static <A, B, C, D, R> Function<A, R> compose(Function<D, R> f1, Function<C, D> f2, Function<B, C> f3,
            Function<A, B> f4) {
        return (args) -> compose(f1, f2, f3).apply(f4.apply(args));
    }

    public static <A, B, C, D, E, R> Function<A, R> compose(Function<E, R> f1, Function<D, E> f2, Function<C, D> f3,
            Function<B, C> f4, Function<A, B> f5) {
        return (args) -> compose(f1, f2, f3, f4).apply(f5.apply(args));
    }

    public static <A, B, C, D, E, F, R> Function<A, R> compose(Function<F, R> f1, Function<E, F> f2, Function<D, E> f3,
            Function<C, D> f4, Function<B, C> f5, Function<A, B> f6) {
        return (args) -> compose(f1, f2, f3, f4, f5).apply(f6.apply(args));
    }

    public static <A, B, C, D, E, F, G, R> Function<A, R> compose(Function<G, R> f1, Function<F, G> f2, Function<E, F> f3,
            Function<D, E> f4, Function<C, D> f5, Function<B, C> f6, Function<A, B> f7) {
        return (args) -> compose(f1, f2, f3, f4, f5, f6).apply(f7.apply(args));
    }

    public static <A, B, C, D, E, F, G, H, R> Function<A, R> compose(Function<H, R> f1, Function<G, H> f2, Function<F, G> f3,
            Function<E, F> f4, Function<D, E> f5, Function<C, D> f6, Function<B, C> f7, Function<A, B> f8) {
        return (args) -> compose(f1, f2, f3, f4, f5, f6, f7).apply(f8.apply(args));
    }

    public static <A, B, C, D, E, F, G, H, I, R> Function<A, R> compose(Function<I, R> f1, Function<H, I> f2,
            Function<G, H> f3, Function<F, G> f4, Function<E, F> f5, Function<D, E> f6, Function<C, D> f7, Function<B, C> f8,
            Function<A, B> f9) {
        return (args) -> compose(f1, f2, f3, f4, f5, f6, f7, f8).apply(f9.apply(args));
    }

    public static <A, B, C, D, E, F, G, H, I, J, R> Function<A, R> compose(Function<J, R> f1, Function<I, J> f2,
            Function<H, I> f3, Function<G, H> f4, Function<F, G> f5, Function<E, F> f6, Function<D, E> f7, Function<C, D> f8,
            Function<B, C> f9, Function<A, B> f10) {
        return (args) -> compose(f1, f2, f3, f4, f5, f6, f7, f8, f9).apply(f10.apply(args));
    }

    @SafeVarargs
    public static <R> Function<R, R> compose(Function<R, R>... funcs) {
        return (args) -> foldRight(args, Arrays.asList(funcs));
    }

    private static <R> R foldRight(R acc, List<Function<R, R>> xs) {
        if (xs.isEmpty()) {
            return acc;
        } else if (xs.size() == 1) {
            return xs.get(0).apply(acc);
        } else {
            Function<R, R> last = xs.get(xs.size() - 1);
            return foldRight(last.apply(acc), xs.subList(0, xs.size() - 1));
        }
    }

}
