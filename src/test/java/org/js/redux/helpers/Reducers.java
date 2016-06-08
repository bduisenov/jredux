package org.js.redux.helpers;

import static org.js.redux.helpers.ActionTypes.ADD_TODO;
import static org.js.redux.helpers.ActionTypes.DISPATCH_IN_MIDDLE;
import static org.js.redux.helpers.ActionTypes.THROW_ERROR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.js.redux.Action;
import org.js.redux.State;

/**
 * Created by bduisenov on 08/06/16.
 */
public class Reducers {

    private static int id(List<Todo> state) {
        return state.stream() //
                .map(Todo::getId) //
                .max(Integer::compare) //
                .orElse(0) + 1;
    }

    public static State todos(State state, Action action) {
        if (action.type == ADD_TODO) {
            List<Todo> todos = state.<List<Todo>>get().orElse(Collections.emptyList());
            List<Todo> newTodos = new ArrayList<>(todos.size() + 1);
            newTodos.addAll(todos);
            newTodos.add(new Todo(id(todos), action.getValue(String.class).orElse(null)));
            return State.of(newTodos);
        }
        return state;
    }

    public static State todosReverse(State state, Action action) {
        if (action.type == ADD_TODO) {
            List<Todo> todos = state.<List<Todo>>get().orElse(Collections.emptyList());
            List<Todo> newTodos = new ArrayList<>(todos.size() + 1);
            newTodos.add(new Todo(id(todos), action.getValue(String.class).orElse(null)));
            newTodos.addAll(todos);
            return State.of(newTodos);
        }
        return state;
    }

    public static List dispatchInTheMiddleOfReducer(List state, Action action) {
        if (action.type == DISPATCH_IN_MIDDLE) {
            action.getValue("boundDispatchFn", Supplier.class).ifPresent(Supplier::get);
        }
        return state;
    }

    public static List errorThrowingReducer(List state, Action action) {
        if (action.type == THROW_ERROR) {
            throw new RuntimeException();
        }
        return state;
    }

}
