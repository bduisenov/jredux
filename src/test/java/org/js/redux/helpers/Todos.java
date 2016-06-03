package org.js.redux.helpers;

import java.util.Arrays;

import org.js.redux.State;

/**
 * Created by bduisenov on 02/06/16.
 */
public class Todos implements State {

    public static class State {
        public final int id;
        public final String text;

        public State(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            State state = (State) o;

            if (id != state.id)
                return false;
            return text.equals(state.text);

        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + text.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "State{" +
                    "id=" + id +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    private final State[] states;

    public Todos() {
        states = new State[]{};
    }

    public Todos(State... states) {
        this.states = states;
    }

    public State[] getStates() {
        return states;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Todos todos = (Todos) o;

        return Arrays.deepEquals(states, todos.states);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(states);
    }

    @Override
    public String toString() {
        return "Todos{" +
                "states=" + Arrays.toString(states) +
                '}';
    }
}
