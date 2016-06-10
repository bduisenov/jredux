package org.js.redux;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.reflect.TypeToken;

/**
 * Created by bduisenov on 07/06/16.
 */
public final class State {

    private enum KEY {
        SINGLE_KEY;
    }

    private final Map<Enum<?>, Object> state;

    private State(Map<Enum<?>, Object> state) {
        this.state = state;
    }

    public <T> Optional<T> get() {
        T result = null;
        Object value = state.get(KEY.SINGLE_KEY);
        if (value != null) {
            result = (T)TypeToken.of(value.getClass()).getRawType().cast(value);
        }
        return Optional.ofNullable(result);
    }

    public <T> Optional<T> get(TypeToken<T> typeOfT) {
        T result = null;
        Object value = state.get(KEY.SINGLE_KEY);
        if (value != null) {
            result = (T) typeOfT.getRawType().cast(value);
        }
        return Optional.ofNullable(result);
    }

    public <T> Optional<T> get(Class<T> type) {
        T result = null;
        Object value = state.get(KEY.SINGLE_KEY);
        if (value != null && value.getClass().isAssignableFrom(type)) {
            result = type.cast(value);
        } else {
            // handle type missmatch
        }
        return Optional.ofNullable(result);
    }

    public <T> Optional<T> get(Enum<?> key) {
        T result = null;
        Object value = state.get(key);
        if (value != null) {
            result = (T) new TypeToken<T>() {}.getRawType().cast(value);
        } else {
            // handle type missmatch
        }
        return Optional.ofNullable(result);
    }

    public <T> Optional<T> get(Enum<?> key, Class<T> type) {
        T result = null;
        Object value = state.get(key);
        if (value != null && value.getClass().isAssignableFrom(type)) {
            result = type.cast(value);
        } else {
            // handle type missmatch
        }
        return Optional.ofNullable(result);
    }

    public static State empty() {
        return new State(Collections.emptyMap());
    }

    public static State of(Map<Enum<?>, Object> state) {
        return new State(state);
    }

    public static State of(Object v1) {
        if (v1== null) {
            throw new NullPointerException();
        }
        return new State(Collections.singletonMap(KEY.SINGLE_KEY, v1));
    }

    public static State of(Enum<?> k1, Object v1) {
        if (k1 == null || v1 == null) {
            throw new NullPointerException();
        }
        return new State(Collections.singletonMap(k1, v1));
    }

    public static State of(Enum<?> k1, Object v1, Enum<?> k2, Object v2) {
        if (k1 == null || v1 == null || k2 == null || v2 == null) {
            throw new NullPointerException();
        }
        Map<Enum<?>, Object> state = new LinkedHashMap<>(2);
        state.put(k1, v1);
        state.put(k2, v2);
        return new State(state);
    }

    public static State of(Enum<?> k1, Object v1, Enum<?> k2, Object v2, Enum<?> k3, Object v3) {
        if (k1 == null || v1 == null || k2 == null || v2 == null || k3 == null || v3 == null) {
            throw new NullPointerException();
        }
        Map<Enum<?>, Object> state = new LinkedHashMap<>(3);
        state.put(k1, v1);
        state.put(k2, v2);
        state.put(k3, v3);
        return new State(state);
    }

    public interface Builder {
        void add(Enum<?> key, Object val);
    }

    public static State of(Consumer<Builder> vals) {
        HashMap<Enum<?>, Object> xs = new HashMap<>();
        vals.accept(xs::put);
        return new State(xs);
    }

    public boolean isEmpty() {
        return state.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        State state1 = (State) o;

        return state.equals(state1.state);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public String toString() {
        return "State{" + state + '}';
    }
}
