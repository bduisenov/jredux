package org.js.redux;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An *action* is a plain object that represents an intention to change the state. Actions are the
 * only way to getReducers data into the store. Any data, whether from UI events, network callbacks, or
 * other sources such as WebSockets needs to eventually be dispatched as actions.
 *
 * Actions must have a `type` field that indicates the type of action being performed. Types can be
 * defined as constants and imported from another module. It’s better to use strings for `type` than
 * Symbols because strings are serializable.
 *
 * Other than `type`, the structure of an action object is really up to you. If you’re interested,
 * check out Flux Standard Action for recommendations on how actions should be constructed.
 */
public final class Action {

    private static final String SINGLE_VALUE = "value";

    public final Enum<?> type;

    private final Map<Map.Entry<String, Class<?>>, Object> values;

    private Action(Enum<?> type) {
        this.type = type;
        this.values = Collections.emptyMap();
    }

    private Action(Enum<?> type, Map<Map.Entry<String, Class<?>>, Object> values) {
        this.type = type;
        this.values = values;
    }

    public <T> T getValue(Class<T> type) {
        Object value = values.get(new AbstractMap.SimpleImmutableEntry<>(SINGLE_VALUE, type));
        if (value != null) {
            return type.cast(value);
        }
        return null;
    }

    public <T> T getValue(String key, Class<T> type) {
        Object value = values.get(new AbstractMap.SimpleImmutableEntry<>(key, type));
        if (value != null) {
            return type.cast(value);
        }
        return null;
    }

    public static Action of() {
        return new Action(null);
    }

    public static Action of(Enum<?> type) {
        return new Action(type);
    }

    public static Action of(Enum<?> type, Object v1) {
        return new Action(type, new HashMap<Map.Entry<String, Class<?>>, Object>() {{
            put(new SimpleImmutableEntry<>(SINGLE_VALUE, v1.getClass()), v1);
        }});
    }

    public static Action of(Enum<?> type, String k1, Object v1) {
        return new Action(type, new HashMap<Map.Entry<String, Class<?>>, Object>() {{
            put(new SimpleImmutableEntry<>(k1, v1.getClass()), v1);
        }});
    }

    public static Action of(Enum<?> type, String k1, Object v1, String k2, Object v2) {
        return new Action(type, new HashMap<Map.Entry<String, Class<?>>, Object>() {{
            put(new SimpleImmutableEntry<>(k1, v1.getClass()), v1);
            put(new SimpleImmutableEntry<>(k2, v2.getClass()), v2);
        }});
    }

    public static Action of(Enum<?> type, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return new Action(type, new HashMap<Map.Entry<String, Class<?>>, Object>() {{
            put(new SimpleImmutableEntry<>(k1, v1.getClass()), v1);
            put(new SimpleImmutableEntry<>(k2, v2.getClass()), v2);
            put(new SimpleImmutableEntry<>(k3, v3.getClass()), v3);
        }});
    }

}
