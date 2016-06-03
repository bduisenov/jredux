package org.js.redux;

import static org.js.redux.Redux.combineReducers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Test;

/**
 * Created by bduisenov on 03/06/16.
 */
public class CombineReducersTest {

    @Test
    public void testReturnsACompositeReducerThatMapsTheStateKeysToGivenReducers() throws Exception {
        Reducer<TestState, TestAction> reducer = combineReducers( //
                (state, action) -> {
                    state = firstNonNull(state, new TestState(0));
                    return action.type == Type.INCREMENT ? new TestState(state.counter + 1) : state;
                }, //
                (state, action) -> {
                    state = firstNonNull(state, new TestState(0));
                    return (action.type == Type.PUSH) ? new TestState(state.counter, action.val) : state;
                });
        TestState s1 = reducer.apply(new TestState(), TestAction.increment());
        assertEquals(new TestState(1), s1);
        TestState s2 = reducer.apply(s1, TestAction.push('a'));
        assertEquals(new TestState(1, 'a'), s2);
    }

    @Test
    public void testThrowsAnErrorIfAReducerReturnsUndefinedHandlingAnAction() throws Exception {
        Reducer<TestState, TestAction> reducer = combineReducers( //
                (state, action) -> {
                    state = firstNonNull(state, new TestState(0));
                    if (action.type == Type.INCREMENT) return new TestState(state.counter + 1);
                    if (action.type == Type.DECREMENT) return new TestState(state.counter - 1);
                    if (action.type == Type.WHATEVER || action.type == null) return null;
                    return state;
                });
        try {
            reducer.apply(new TestState(0), TestAction.whatever());
            fail();
        } catch (Exception e) {
            //
        }

        try {
            reducer.apply(new TestState(0), null);
            fail();
        } catch (Exception e) {
            //
        }

        try {
            reducer.apply(new TestState(0), TestAction.empty());
            fail();
        } catch (Exception e) {
            //
        }

    }

    @Test
    public void testThrowsAnErrorOnFirstCallIfAReducerReturnsUndefinedInitializing() throws Exception {
        Reducer<TestState, TestAction> reducer = combineReducers( //
                (state, action) -> {
                    if (action.type == Type.INCREMENT) return new TestState(state.counter + 1);
                    if (action.type == Type.DECREMENT) return new TestState(state.counter - 1);
                    return state;
                });
        try {
            reducer.apply(null, null);
            fail();
        } catch (Exception e) {
            //
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCatchesErrorThrownInReducerWhenInitializingAndRethrow() throws Exception {
        Reducer<TestState, TestAction> reducer = combineReducers( //
                (state, action) -> {
                    throw new UnsupportedOperationException("Error thrown in reducer");
                });
    }

    @Test(expected = IllegalStateException.class)
    public void testWarnsIfNoReducersArePassedToCombineReducers() throws Exception {
        Reducer<TestState, TestAction> reducer = combineReducers(Collections.emptyList());
        reducer.apply(null, null);
    }

    enum Type {
        INCREMENT, DECREMENT, WHATEVER, PUSH
    }

    static class TestAction implements Action {
        Type type;
        char val;
        static TestAction empty() {
            return new TestAction();
        }
        static TestAction increment() {
            return new TestAction() {{
                type = Type.INCREMENT;
            }};
        }
        static TestAction push(char c) {
            return new TestAction() {{
                type = Type.PUSH;
                val = c;
            }};
        }
        static TestAction whatever() {
            return new TestAction() {{
                type = Type.WHATEVER;
            }};
        }
    }

    static class TestState implements State {

        final int counter;

        final Character stack;

        TestState() {
            this.counter = 0;
            this.stack = null;
        }

        TestState(int value) {
            this.counter = value;
            this.stack = null;
        }

        TestState(int value, char c) {
            this.counter = value;
            this.stack = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            TestState state = (TestState) o;

            if (counter != state.counter)
                return false;
            return stack != null ? stack.equals(state.stack) : state.stack == null;

        }

        @Override
        public int hashCode() {
            int result = counter;
            result = 31 * result + (stack != null ? stack.hashCode() : 0);
            return result;
        }
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
