package org.js.redux;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bduisenov on 07/06/16.
 */
public final class State {

    private final Map<Enum<?>, Object> state;

    private State(Map<Enum<?>, Object> state) {
        this.state = state;
    }

    public <T> T get(Enum<?> key, Class<T> type) {
        Object value = state.get(key);
        if (value != null && value.getClass().isAssignableFrom(type)) {
            return type.cast(value);
        } else {
            // handle type missmatch
        }
        return null;
    }

    public static State empty() {
        return new State(Collections.emptyMap());
    }

    public static State of(Map<Enum<?>, Object> state) {
        return new State(state);
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
