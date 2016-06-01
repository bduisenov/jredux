package org.js.redux;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

/**
 * Created by bduisenov on 01/06/16.
 */
public class ReduxTest {

    @Test
    public void testStore() throws Exception {
        Store<TestState, TestAction> store = Redux.createStore(null, TestReducer.func);

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
        Store<TestState, TestAction> store = Redux.createStore(null, combined);

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
        Store<TestState, TestAction> store = Redux.createStore(null, TestReducer.func);
        Listener mockedListener = mock(Listener.class);
        store.subscribe(mockedListener);
        store.dispatch(TestAction.INCREMENET);
        store.dispatch(TestAction.INCREMENET);
        verify(mockedListener, times(2)).onStateChanged();
    }

}
