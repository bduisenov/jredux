package org.js.redux;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.js.redux.utils.TypeResolver;

/**
 * Object whose values correspond to different add functions.
 */
public class ReducersMapObject<A extends Action> {

    private final Map<String, Class<?>> types;
    private final Map<String, BiFunction<Object, A, Object>> reducers;

    private ReducersMapObject(Map<String, Class<?>> types, Map<String, BiFunction<Object, A, Object>> reducers) {
        this.types = types;
        this.reducers = reducers;
    }

    public Map<String, Class<?>> getTypes() {
        return types;
    }

    public Map<String, BiFunction<Object, A, Object>> getReducers() {
        return reducers;
    }

    public static class Builder<A extends Action> {

        private Map<String, Class<?>> types = new HashMap<>();
        private Map<String, BiFunction<Object, A, Object>> reducers = new LinkedHashMap<>();

        public <T> Builder<A> add(String key, BiFunction<T, A, T> reducer) {
            Class<?> stateType = TypeResolver.resolveRawArguments(BiFunction.class, reducer.getClass())[0];
            types.put(key, stateType);
            reducers.put(key, (BiFunction<Object, A, Object>)reducer);
            return this;
        }

        public <T> Builder<A> add(String key, T defaultValue, BiFunction<T, A, T> reducer) {
            if (defaultValue == null) {
                throw new NullPointerException("defaultValue must not be null");
            }
            Class<?> stateType = defaultValue.getClass();
            types.put(key, stateType);
            BiFunction<T, A, T> reducerWithDefaultValue = (state, action) -> {
                if (state == null) {
                    state = defaultValue;
                }
                return reducer.apply((T)state, action);
            };
            reducers.put(key, (BiFunction<Object, A, Object>)reducerWithDefaultValue);
            return this;
        }

        public ReducersMapObject<A> build() {
            return new ReducersMapObject<>(types, reducers);
        }

    }

}
