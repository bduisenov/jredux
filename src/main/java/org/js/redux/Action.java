package org.js.redux;

import java.util.Optional;

/**
 * An *action* is a plain object that represents an intention to change the state. Actions are the
 * only way to getReducers data into the store. Any data, whether from UI events, network callbacks,
 * or other sources such as WebSockets needs to eventually be dispatched as actions.
 *
 * Actions must have a `type` field that indicates the type of action being performed. Types can be
 * defined as constants and imported from another module. It’s better to use strings for `type` than
 * Symbols because strings are serializable.
 *
 * This implementation is more or less relying on FSA
 * {@link https://github.com/acdlite/flux-standard-action} standard.
 * 
 */
public final class Action<T> {

    /**
     * The type of an action identifies to the consumer the nature of the action that has occurred.
     * Two actions with the same type MUST be strictly equivalent (using ===). By convention, type
     * is usually string constant or a Symbol.
     */
    public final Enum<?> type;

    /**
     * The optional payload property MAY be any type of value. It represents the payload of the
     * action. Any information about the action that is not the type or status of the action should
     * be part of the payload field.
     * 
     * By convention, if error is true, the payload SHOULD be an error object. This is akin to
     * rejecting a promise with an error object.
     */
    public final Optional<T> payload;

    /**
     * The optional error property MAY be set to true if the action represents an error.
     * 
     * An action whose error is true is analogous to a rejected Promise. By convention, the payload
     * SHOULD be an error object.
     */
    public final boolean error;

    private Action(Enum<?> type) {
        this.type = type;
        this.payload = Optional.empty();
        this.error = false;
    }

    private Action(Enum<?> type, T val) {
        this.type = type;
        this.payload = Optional.ofNullable(val);
        this.error = false;
    }

    private Action(Enum<?> type, T val, boolean error) {
        this.type = type;
        this.payload = Optional.of(val);
        this.error = error;
    }

    public static Action empty() {
        return new Action<>(null);
    }

    public static Action of(Enum<?> type) {
        return new Action<>(type);
    }

    public static <T> Action of(Enum<?> type, T val) {
        return new Action<>(type, val);
    }

    public static <T> Action error(Enum<?> type, T val) {
        return new Action<>(type, val, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Action<?> action = (Action<?>) o;

        if (error != action.error)
            return false;
        if (!type.equals(action.type))
            return false;
        return payload.equals(action.payload);

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + payload.hashCode();
        result = 31 * result + (error ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Action{" + "payload=" + payload + ", type=" + type + '}';
    }
}
