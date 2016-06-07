package org.js.redux;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Object whose values correspond to different add functions.
 */
public final class ReducersMapObject {

    private final Map<Enum<?>, Class<?>> types;

    private final Map<Enum<?>, BiFunction<Object, Action, Object>> reducers;

    private ReducersMapObject(Map<Enum<?>, Class<?>> types, Map<Enum<?>, BiFunction<Object, Action, Object>> reducers) {
        this.types = types;
        this.reducers = reducers;
    }

    public Map<Enum<?>, Class<?>> getTypes() {
        return types;
    }

    public Map<Enum<?>, BiFunction<Object, Action, Object>> getReducers() {
        return reducers;
    }

    public static <A extends Action> KeyStep<A> builder() {
        return new Steps<>();
    }

    public interface BuildStep<A extends Action> {

        ReducersMapObject build();
    }

    public interface ReducerStep<A extends Action> {

        <T> KeyOrBuildStep<A> reducer(BiFunction<T, A, T> reducer);
    }

    public interface TypedReducerStep<T, A extends Action> {

        KeyOrBuildStep<A> reducer(BiFunction<T, A, T> reducer);
    }

    public interface InitialValueStep<A extends Action> {

        <T> TypedReducerStep<T, A> withInitialValue(T initialValue);
    }

    public interface StateTypeStep<A extends Action> {

        <T> TypedReducerStep<T, A> withStateType(Class<T> type);
    }

    public interface KeyStep<A extends Action> {

        InitialValueOrStateTypeStep<A> add(Enum<?> key);
    }

    public interface KeyOrBuildStep<A extends Action> extends KeyStep<A>, BuildStep<A> {

    }

    public interface InitialValueOrStateTypeStep<A extends Action> extends InitialValueStep<A>, StateTypeStep<A> {

    }

    private static class Steps<A extends Action> implements //
            KeyStep<A>, //
            KeyOrBuildStep<A>, //
            InitialValueOrStateTypeStep<A>, //
            ReducerStep<A>, //
            BuildStep<A> {

        private final Map<Enum<?>, Class<?>> types = new HashMap<>();

        private final Map<Enum<?>, BiFunction<Object, Action, Object>> reducers = new LinkedHashMap<>();

        private Enum<?> key;

        private Class type;

        private Object initialValue;

        @Override
        public InitialValueOrStateTypeStep<A> add(Enum<?> key) {
            if (key == null) {
                throw new NullPointerException("reducer's key must be set");
            }
            this.key = key;
            return this;
        }

        @Override
        public <T> TypedReducerStep<T, A> withInitialValue(T initialValue) {
            this.initialValue = initialValue;
            return this::reducer;
        }

        @Override
        public <T> TypedReducerStep<T, A> withStateType(Class<T> type) {
            this.type = type;
            return this::reducer;
        }

        @Override
        public <T> KeyOrBuildStep<A> reducer(BiFunction<T, A, T> reducer) {
            if (reducer == null) {
                throw new NullPointerException("reducer must not be null");
            }

            if (initialValue != null) {
                type = initialValue.getClass();
            }
            types.put(key, type);

            if (initialValue == null) {
                reducers.put(key, (BiFunction<Object, Action, Object>) reducer);
            } else {
                Function<T, BiFunction<T, A, T>> reducerWithInitialValue = iv -> (state, action) -> {
                    state = state == null ? iv : state;
                    return reducer.apply(state, action);
                };
                reducers.put(key, (BiFunction<Object, Action, Object>) reducerWithInitialValue.apply((T)initialValue));
            }

            this.key = null;
            this.type = null;
            this.initialValue = null;

            return this;
        }

        @Override
        public ReducersMapObject build() {
            return new ReducersMapObject(types, reducers);
        }

    }

}
