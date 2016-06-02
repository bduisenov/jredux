package org.js.redux;

import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.junit.Assert.assertEquals;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.js.redux.helpers.Middleware;
import org.js.redux.helpers.Reducers;
import org.js.redux.helpers.TodoAction;
import org.js.redux.helpers.Todos;
import org.junit.Test;

/**
 * Created by bduisenov on 02/06/16.
 */
public class ApplyMiddlewareTest {

    public <S extends State, A extends Action> Function<Function<Object, Void>, Function<BiFunction<Consumer<A>, Supplier<S>, Void>, Void>> test() {
        return next -> action -> {
            next.apply(action);
            return null;
        };
    }

    // wraps dispatch method with middleware once
    @Test
    public void testWrapsDispatchMethodWithMiddlewareOnce() throws Exception {
        Store<Todos, TodoAction> store = Redux.<Todos, TodoAction>applyMiddleware(Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(new Params<>(Reducers.todos()));

        store.dispatch(addTodo("Use Redux"));
        store.dispatch(addTodo("Flux FTW!"));

        Todos expected = new Todos(new Todos.State(1, "Use Redux"), new Todos.State(2, "Flux FTW!"));
        assertEquals(expected, store.getState());

    }


}
