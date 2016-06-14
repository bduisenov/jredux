package org.js.redux;

import static org.js.redux.Redux.applyMiddleware;
import static org.js.redux.StoreCreator.createStore;
import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.js.redux.helpers.ActionCreators.addTodoAsync;
import static org.js.redux.helpers.ActionCreators.addTodoIfEmpty;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.js.redux.helpers.MiddlewareHelper;
import org.js.redux.helpers.Reducers;
import org.js.redux.helpers.Todo;
import org.junit.Test;

/**
 * Created by bduisenov on 06/06/16.
 */
public class ApplyMiddlewareTest {

    @Test
    public void wrapsDispatchMethodWithMiddlewareOnce() {
        Middleware test = middlewareAPI -> (Function<Dispatch, Dispatch>) next -> (Dispatch) next;
        Store store = applyMiddleware(test, MiddlewareHelper::thunk) //
                .apply(StoreCreator::createStore) //
                .apply(Reducers::todos, null);

        store.dispatch(addTodo("Use Redux"));
        store.dispatch(addTodo("Flux FTW!"));

        assertEquals(State.of(Arrays.asList(new Todo(1, "Use Redux"), new Todo(2, "Flux FTW!"))), store.getState());
    }

    @Test
    public void passesRecursiveDispatchesThroughTheMiddlewareChain() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        Middleware test = middlewareAPI -> (Function<Dispatch, Dispatch>) next -> new Dispatch() {

            @Override
            public <T> T apply(Action action) {
                calls.incrementAndGet();
                return next.apply(action);
            }
        };
        Store store = applyMiddleware(test, MiddlewareHelper::thunk) //
                .apply(StoreCreator::createStore) //
                .apply(Reducers::todos, null);
        store.dispatch(addTodoAsync("Use Redux")).get();

        assertEquals(2, calls.get());
    }

    @Test
    public void worksWithThunkMiddleware() throws Exception {
        Store store = applyMiddleware(MiddlewareHelper::thunk) //
                .apply(StoreCreator::createStore) //
                .apply(Reducers::todos, null);
        store.dispatch(addTodoIfEmpty("Hello"));
        assertEquals(State.of(Collections.singletonList(new Todo(1, "Hello"))), store.getState());

        store.dispatch(addTodoIfEmpty("Hello"));
        assertEquals(State.of(Collections.singletonList(new Todo(1, "Hello"))), store.getState());

        store.dispatch(addTodo("World"));
        assertEquals(State.of(Arrays.asList(new Todo(1, "Hello"), new Todo(2, "World"))), store.getState());

        store.dispatch(addTodoAsync("Maybe")).get();
        assertEquals(State.of(Arrays.asList(new Todo(1, "Hello"), new Todo(2, "World"), new Todo(3, "Maybe"))), store.getState());
    }

    @Test
    public void keepsUnwrappedDispatchAvailableWhileMiddlewareIsinitializing() {
        Middleware earlyDispatch = middlewareAPI -> (Function<Dispatch, Dispatch>) dispatch -> {
            dispatch.apply(addTodo("Hello"));
            return new Dispatch() {

                @Override
                public Action apply(Action action) {
                    return action;
                }
            };
        };
        Store store = createStore(Reducers::todos, applyMiddleware(earlyDispatch));
        assertEquals(State.of(Collections.singletonList(new Todo(1, "Hello"))), store.getState());
    }

}
