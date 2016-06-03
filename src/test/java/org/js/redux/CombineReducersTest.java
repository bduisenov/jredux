package org.js.redux;

import static org.js.redux.Redux.combineReducers;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by bduisenov on 03/06/16.
 */
public class CombineReducersTest {

    static class action implements Action {
        String type;
        char val;
        static action increment() {
            return new action() {{
                    type = "INCREMENT";
                }};
        }
        static action push(char c) {
            return new action() {{
                    type = "PUSH";
                    val = c;
                }};
        }
    }

    static class state implements State {

        final int counter;

        final Character stack;

        state() {
            this.counter = 0;
            this.stack = null;
        }

        state(int value) {
            this.counter = value;
            this.stack = null;
        }

        state(int value, char c) {
            this.counter = value;
            this.stack = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            state state = (state) o;

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

    @Test
    public void testReturnsACompositeReducerThatMapsTheStateKeysToGivenReducers() throws Exception {
        Reducer<state, action> reducer = combineReducers( //
                (state, action) -> {
                    int value = firstNonNull(state, new state(0)).counter;
                    return action.type.equals("INCREMENT") ? new state(value + 1) : state;
                }, //
                (state, action) -> {
                    int value = firstNonNull(state, new state(0)).counter;
                    return (action.type.equals("PUSH")) ? new state(value, action.val) : state;
                });
        state s1 = reducer.apply(new state(), action.increment());
        assertEquals(new state(1), s1);
        state s2 = reducer.apply(s1, action.push('a'));
        assertEquals(new state(1, 'a'), s2);
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
