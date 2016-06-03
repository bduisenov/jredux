package org.js.redux;

import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.js.redux.helpers.ActionCreators.addTodoAsync;
import static org.js.redux.helpers.ActionCreators.addTodoIfEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    boolean reachable = false;
    int reachedCnt = 0;

    @Test
    public void testWrapsDispatchMethodWithMiddlewareOnce() throws Exception {
        BiFunction<Consumer<TodoAction>, Supplier<Todos>, Function<Consumer<TodoAction>, Consumer<TodoAction>>> spy =
                (dispatch, getState) -> next -> action -> { next.accept(action); reachable = true; };
        Store<Todos, TodoAction> store = Redux.<Todos, TodoAction, TodoAction>applyMiddleware(spy, Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(new Params<>(Reducers.todos()));

        store.dispatch(addTodo("Use Redux"));
        store.dispatch(addTodo("Flux FTW!"));

        assertTrue(reachable);

        Todos expected = new Todos(new Todos.State(1, "Use Redux"), new Todos.State(2, "Flux FTW!"));
        assertEquals(expected, store.getState());
    }

    @Test
    public void testPassesRecursiveDispatchesThroughTheMiddlewareChain () throws Exception {
        BiFunction<Consumer<TodoAction>, Supplier<Todos>, Function<Consumer<TodoAction>, Consumer<TodoAction>>> spy =
                (dispatch, getState) -> next -> action -> { next.accept(action); ++reachedCnt; };
        Store<Todos, TodoAction> store = Redux.<Todos, TodoAction, TodoAction>applyMiddleware(spy, Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(new Params<>(Reducers.todos()));

        store.dispatch(addTodoAsync("Use Redux"));
        assertEquals(2, reachedCnt);
    }

    @Test
    public void testWorksWithThunkMiddleware() throws Exception {
        Store<Todos, TodoAction> store = Redux.<Todos, TodoAction, TodoAction>applyMiddleware(Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(new Params<>(Reducers.todos()));
        store.dispatch(addTodoIfEmpty("Hello"));
        assertEquals(new Todos(new Todos.State(1, "Hello")), store.getState());

        store.dispatch(addTodoIfEmpty("Hello"));
        assertEquals(new Todos(new Todos.State(1, "Hello")), store.getState());

        store.dispatch(addTodo("World"));
        assertEquals(new Todos(new Todos.State(1, "Hello"), new Todos.State(2, "World")), store.getState());

        store.dispatch(addTodoAsync("Maybe"));
        assertEquals(new Todos(new Todos.State(1, "Hello"), new Todos.State(2, "World"), new Todos.State(3, "Maybe")), store.getState());
    }

}
