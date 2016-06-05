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
        Store<Todos, TodoAction> store = Redux.applyMiddleware(spy, Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(Reducers.todos(), null);

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
        Store<Todos, TodoAction> store = Redux.applyMiddleware(spy, Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(Reducers.todos(), null);

        store.dispatch(addTodoAsync("Use Redux"));
        assertEquals(2, reachedCnt);
    }

    @Test
    public void testWorksWithThunkMiddleware() throws Exception {
        Store<Todos, TodoAction> store = Redux.<Todos, TodoAction>applyMiddleware(Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(Reducers.todos(), null);
        store.dispatch(addTodoIfEmpty("Hello"));
        assertEquals(new Todos(new Todos.State(1, "Hello")), store.getState());

        store.dispatch(addTodoIfEmpty("Hello"));
        assertEquals(new Todos(new Todos.State(1, "Hello")), store.getState());

        store.dispatch(addTodo("World"));
        assertEquals(new Todos(new Todos.State(1, "Hello"), new Todos.State(2, "World")), store.getState());

        store.dispatch(addTodoAsync("Maybe"));
        assertEquals(new Todos(new Todos.State(1, "Hello"), new Todos.State(2, "World"), new Todos.State(3, "Maybe")), store.getState());
    }

    @Test
    public void testKeepsUnwrappedDispatchAvailableWhileMiddlewareIsInitializing() throws Exception {
        // This is documenting the existing behavior in Redux 3.x.
        // We plan to forbid this in Redux 4.x.

        BiFunction<Consumer<TodoAction>, Supplier<Todos>, Function<Consumer<TodoAction>, Consumer<TodoAction>>> earlyDispatch = // 
                (dispatch, getState) -> {
            dispatch.accept(addTodo("Hello"));
            return todoActionConsumer -> action -> System.out.println(action);
        };

        Function<BiFunction<Reducer<Todos, TodoAction>, Todos, Store<Todos, TodoAction>>, BiFunction<Reducer<Todos, TodoAction>, Todos, Store<Todos, TodoAction>>> func = Redux.applyMiddleware(earlyDispatch);
        Store<Todos, TodoAction> store = Redux.createStore(Reducers.todos(), func);
        assertEquals(new Todos(new Todos.State(1, "Hello")), store.getState());

    }

}
