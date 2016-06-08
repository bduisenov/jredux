package org.js.redux;

import static org.js.redux.StoreCreator.createStore;
import static org.junit.Assert.assertEquals;

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

}
