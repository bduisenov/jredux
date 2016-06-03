package org.js.redux;

import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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

    static class test implements BiFunction<Consumer<TodoAction>, Supplier<Todos>, Function<Consumer<TodoAction>, Consumer<TodoAction>>>  {

        @Override
        public Function<Consumer<TodoAction>, Consumer<TodoAction>> apply(Consumer<TodoAction> dispatcher, Supplier<Todos> getState) {
            return next -> next::accept;
        }
    }

    @Test
    public void testWrapsDispatchMethodWithMiddlewareOnce() throws Exception {
        BiFunction<Consumer<TodoAction>, Supplier<Todos>, Function<Consumer<TodoAction>, Consumer<TodoAction>>> spy = spy(new test());
        Store<Todos, TodoAction> store = Redux.<Todos, TodoAction, TodoAction>applyMiddleware(spy, Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(new Params<>(Reducers.todos()));

        store.dispatch(addTodo("Use Redux"));
        store.dispatch(addTodo("Flux FTW!"));

        verify(spy).apply(any(), any());

        Todos expected = new Todos(new Todos.State(1, "Use Redux"), new Todos.State(2, "Flux FTW!"));
        assertEquals(expected, store.getState());

    }


}
