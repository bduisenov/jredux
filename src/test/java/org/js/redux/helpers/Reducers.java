package org.js.redux.helpers;

import org.js.redux.Reducer;

/**
 * Created by bduisenov on 02/06/16.
 */
public class Reducers {

    private static int id(Todos.State[] states) {
        int result = 0;
        for (Todos.State state : states) {
            result = state.id > result ? state.id : result;
        }
        return result + 1;
    }

    public static <S extends Todos.State> Reducer<Todos, TodoAction> todos() {
        return (todos, action) -> {
            todos = firstNonNull(todos, new Todos());
            if (action.type == null) return todos;
            switch (action.type) {
                case ADD_TODO: {
                    Todos.State[] states = todos.getStates();
                    Todos.State[] newStates = new Todos.State[states.length + 1];
                    System.arraycopy(states, 0, newStates, 0, states.length);
                    newStates[newStates.length - 1] = (new Todos.State(id(states), action.text));
                    return new Todos(newStates);
                }
                default: return todos;
            }
        };
    }

    private static <T> T firstNonNull(T first, T second) {
        return first != null ? first : checkNotNull(second);
    }

    private static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

}
