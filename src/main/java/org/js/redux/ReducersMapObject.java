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

    private final Map<Enum<?>, BiFunction> reducers;

    private ReducersMapObject(Map<Enum<?>, Class<?>> types, Map<Enum<?>, BiFunction> reducers) {
        this.types = types;
        this.reducers = reducers;
    }

    public Map<Enum<?>, Class<?>> getTypes() {
        return types;
    }

    public Map<Enum<?>, BiFunction> getReducers() {
        return reducers;
    }

    public static KeyStep builder() {
        return new Steps();
    }

    public interface BuildStep {

        ReducersMapObject build();
    }

    public interface ReducerStep {

        <T, A> KeyOrBuildStep reducer(BiFunction<T, Action<A>, T> reducer);
    }

    public interface TypedReducerStep<T> {

        <A> KeyOrBuildStep reducer(BiFunction<T, Action<A>, T> reducer);
    }

    public interface InitialValueStep {

        <T> TypedReducerStep<T> withInitialValue(T initialValue);
    }

    public interface StateTypeStep {

        <T> TypedReducerStep<T> withStateType(Class<T> type);
    }

    public interface KeyStep {

        InitialValueOrStateTypeStep add(Enum<?> key);
    }

    public interface KeyOrBuildStep extends KeyStep, BuildStep {

    }

    public interface InitialValueOrStateTypeStep extends InitialValueStep, StateTypeStep {

    }

    private static class Steps implements //
            KeyStep, //
            KeyOrBuildStep, //
            InitialValueOrStateTypeStep, //
            ReducerStep, //
            BuildStep {

        private final Map<Enum<?>, Class<?>> types = new HashMap<>();

        private final Map<Enum<?>, BiFunction> reducers = new LinkedHashMap<>();

        private Enum<?> key;

        private Class type;

        private Object initialValue;

        @Override
        public InitialValueOrStateTypeStep add(Enum<?> key) {
            if (key == null) {
                throw new NullPointerException("reducer's key must be set");
            }
            this.key = key;
            return this;
        }

        @Override
        public <T> TypedReducerStep<T> withInitialValue(T initialValue) {
            this.initialValue = initialValue;
            return this::reducer;
        }

        @Override
        public <T> TypedReducerStep<T> withStateType(Class<T> type) {
            this.type = type;
            return this::reducer;
        }

        @Override
        public <T, A> KeyOrBuildStep reducer(BiFunction<T, Action<A>, T> reducer) {
            if (reducer == null) {
                throw new NullPointerException("reducer must not be null");
            }

            if (initialValue != null) {
                type = initialValue.getClass();
            }
            types.put(key, type);

            if (initialValue == null) {
                reducers.put(key, (BiFunction) reducer);
            } else {
                Function<T, BiFunction<T, Action<A>, T>> reducerWithInitialValue = iv -> (state, action) -> {
                    state = state == null ? iv : state;
                    return reducer.apply(state, action);
                };
                reducers.put(key, (BiFunction) reducerWithInitialValue.apply((T)initialValue));
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
