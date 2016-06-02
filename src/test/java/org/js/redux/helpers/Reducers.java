package org.js.redux.helpers;

import java.util.Arrays;
import java.util.List;

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
        return result;
    }

    public static <S extends Todos.State> Reducer<Todos, TodoAction> todos() {
        return (todos, action) -> {
            todos = firstNonNull(todos, new Todos());
            switch (action.type) {
                case ADD_TODO: {
                    Todos.State[] states = todos.getStates();
                    List<Todos.State> newStates = Arrays.asList(states);
                    newStates.add(new Todos.State(id(states), action.text));
                    return new Todos(newStates.toArray(new Todos.State[] {}));
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
