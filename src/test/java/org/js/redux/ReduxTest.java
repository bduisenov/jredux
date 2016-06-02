package org.js.redux;

import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
 * Created by bduisenov on 01/06/16.
 */
public class ReduxTest {

    @Test
    public void testStore() throws Exception {
        Store<TestState, TestAction> store = Redux.createStore(TestReducer.func, null);

        store.dispatch(TestAction.INCREMENET);
        assertEquals(1, store.getState().value);

        store.dispatch(TestAction.INCREMENET);
        assertEquals(2, store.getState().value);

        store.dispatch(TestAction.DECREMENT);
        assertEquals(1, store.getState().value);

        store.dispatch(TestAction.DECREMENT);
        assertEquals(0, store.getState().value);
    }

    @Test
    public void testCombine() throws Exception {
        Reducer<TestState, TestAction> combined = Redux
                .combineReducers(TestReducer.func, AnotherTestReducer.func);
        Store<TestState, TestAction> store = Redux.createStore(combined, null);

        store.dispatch(TestAction.INCREMENET);
        assertEquals(2, store.getState().value);

        store.dispatch(TestAction.INCREMENET);
        assertEquals(4, store.getState().value);

        store.dispatch(TestAction.DECREMENT);
        assertEquals(2, store.getState().value);

        store.dispatch(TestAction.DECREMENT);
        assertEquals(0, store.getState().value);
    }

    @Test
    public void testListeners() throws Exception {
        Store<TestState, TestAction> store = Redux.createStore(TestReducer.func, null);
        Listener mockedListener = mock(Listener.class);
        store.subscribe(mockedListener);
        store.dispatch(TestAction.INCREMENET);
        store.dispatch(TestAction.INCREMENET);
        verify(mockedListener, times(2)).onStateChanged();
    }

    public <S extends State, A extends Action> Function<Function<Object, Void>, Function<BiFunction<Consumer<A>, Supplier<S>, Void>, Void>> test() {
        return next -> action -> {
            next.apply(action);
            return null;
        };
    }

    // wraps dispatch method with middleware once
    @Test
    public void applyMiddleware() {
        Store<Todos, TodoAction> store = Redux.<Todos, TodoAction>applyMiddleware(Middleware::thunk) //
                .apply(Redux::createStore) //
                .apply(new Params<>(Reducers.todos()));

        store.dispatch(addTodo("Use Redux"));
        store.dispatch(addTodo("Flux FTW!"));

        Todos expected = new Todos() {{
            /*states.add(new Todos.State(1, "Use Redux"));
            states.add(new Todos.State(2, "Flux FTW!"));*/
        }};
        assertEquals(expected, store.getState());

    }

}
