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
        Reducer<TestState, TestAction> combined = Redux.combineReducers( //
                (state, action) -> {
                    state = firstNonNull(state, new TestState(0));
                    switch (action) {
                        case INCREMENET: {
                            return new TestState(state.value + 1);
                        }
                        case DECREMENT: {
                            return new TestState(state.value - 1);
                        }
                        default:
                            return state;
                    }
                }, // 
                (state, action) -> {
                    state = firstNonNull(state, new TestState(0));
                    switch (action) {
                        case INCREMENET: {
                            return new TestState(state.value + 1);
                        }
                        case DECREMENT: {
                            return new TestState(state.value - 1);
                        }
                        default:
                            return state;
                    }
                });
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

    private static <T> T firstNonNull(T first, T second) {
        return first != null ? first : checkNotNull(second);
    }

    private static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

}
