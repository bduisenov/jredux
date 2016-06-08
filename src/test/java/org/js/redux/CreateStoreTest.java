package org.js.redux;

import static org.js.redux.StoreCreator.createStore;
import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.js.redux.helpers.ActionCreators;
import org.js.redux.helpers.Reducers;
import org.js.redux.helpers.Todo;
import org.junit.Test;

/**
 * Created by bduisenov on 08/06/16.
 */
public class CreateStoreTest {

    //TODO exposes the public API

    //TODO throws if reducer is not a function

    @Test
    public void passesTheInitialActionAndTheInitialState() {
        Store store = createStore(Reducers::todos, State.of(new Todo(1, "Hello")));
        assertEquals(new Todo(1, "Hello"), store.getState().get().get());
    }

    @Test
    public void appliesTheReducerToThePreviousState() {
        Store store = createStore(Reducers::todos);
        assertEquals(State.empty(), store.getState());

        store.dispatch(ActionCreators.unknownAction());
        assertEquals(State.empty(), store.getState());

        store.dispatch(addTodo("Hello"));
        assertEquals(State.of(Collections.singletonList(new Todo(1, "Hello"))), store.getState());

        store.dispatch(addTodo("World"));
        assertEquals(State.of(Arrays.asList(new Todo(1, "Hello"), new Todo(2, "World"))), store.getState());
    }

    @Test
    public void preservesTheStateWhenReplacingAReducer() {
        Store store = createStore(Reducers::todos);
        store.dispatch(addTodo("Hello"));
        store.dispatch(addTodo("World"));
        assertEquals(State.of(Arrays.asList(new Todo(1, "Hello"), new Todo(2, "World"))), store.getState());

        store.replaceReducer(Reducers::todosReverse);
        assertEquals(State.of(Arrays.asList(new Todo(1, "Hello"), new Todo(2, "World"))), store.getState());

        store.dispatch(addTodo("Perhaps"));
        assertEquals(State.of(Arrays.asList( //
                new Todo(3, "Perhaps"), //
                new Todo(1, "Hello"), //
                new Todo(2, "World"))), store.getState());

        store.replaceReducer(Reducers::todos);
        assertEquals(State.of(Arrays.asList( //
                new Todo(3, "Perhaps"), //
                new Todo(1, "Hello"), //
                new Todo(2, "World"))), store.getState());

        store.dispatch(addTodo("Surely"));
        assertEquals(State.of(Arrays.asList( //
                new Todo(3, "Perhaps"), //
                new Todo(1, "Hello"), //
                new Todo(2, "World"), //
                new Todo(4, "Surely"))), store.getState());
    }

}
