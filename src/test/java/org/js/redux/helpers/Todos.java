package org.js.redux.helpers;

import java.util.ArrayList;
import java.util.List;

import org.js.redux.State;

/**
 * Created by bduisenov on 02/06/16.
 */
public class Todos implements State {

    public static class State {
        public final int id;
        public String text;

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
    }

    public final List<Todos.State> states = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Todos todos = (Todos) o;

        return states.equals(todos.states);

    }

    @Override
    public int hashCode() {
        return states.hashCode();
    }
}
