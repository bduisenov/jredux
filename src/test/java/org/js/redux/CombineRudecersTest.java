package org.js.redux;

import static org.js.redux.Redux.combineReducers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Created by bduisenov on 06/06/16.
 */
public class CombineRudecersTest {

    @Test
    public void returnsACompositeReducerThatMapsTheStateKeysToGivenReducers() {
        Reducer<CombinedAction> reducer = combineReducers(new ReducersMapObject.Builder<CombinedAction>() //
                .add("counter", (Integer state, CombinedAction action) -> {//
                        state = (state == null) ? 0 : state;
                        return (action.getType().equals("increment")) ? state + 1 : state;
                }) //
                .add("stack", Collections.emptyList(), (List<String> state, CombinedAction action) -> {
                    if (action.getType().equals("push")) {
                        List<String> newState = new ArrayList<>(state.size() + 1);
                        newState.addAll(state);
                        newState.add(action.getValue());
                        return newState;
                    }
                    return state;
                }).build());
        State s1 = reducer.apply(State.empty(), new CombinedAction("increment", null));
        assertEquals(State.of("counter", 1, "stack", Collections.emptyList()), s1);
        State s2 = reducer.apply(s1, new CombinedAction("push", "a"));
        assertEquals(State.of("counter", 1, "stack", Collections.singletonList("a")), s2);
    }

    @Test
    public void throwsAnErrorIfAReducerReturnsUndefinedHandlingAnAction() {
        Reducer<CombinedAction> reducer = combineReducers(
                new ReducersMapObject.Builder<CombinedAction>() //
                        .add("counter", 0, (Integer state, CombinedAction action) -> {
                            if (action != null && action.getType() != null) {
                                switch (action.getType().toString()) {
                                    case "increment":
                                        return state + 1;
                                    case "decrement":
                                        return state - 1;
                                    case "whatever":
                                        return null;
                                }
                            }
                            return null;
                        }).build());
        try {
            reducer.apply(State.of("counter", 0), new CombinedAction("whatever", null));
            fail();
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reducer.apply(State.of("counter", 0), null);
            fail();
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reducer.apply(State.of("counter", 0), new CombinedAction(null, null));
            fail();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class CombinedAction implements Action {

        private String type;

        private String value;

        public CombinedAction(String type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public Object getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }
}
