package org.js.redux;

import static org.js.redux.Redux.combineReducers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
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
        Reducer<CombinedAction> reducer = combineReducers(ReducersMapObject.<CombinedAction>builder() //
                .add("counter").withInitialValue(0) //
                .reducer((Integer state, CombinedAction action) ->  //
                        (action.getType().equals("increment")) ? state + 1 : state) //
                .add("stack").withInitialValue(Collections.<String>emptyList()) //
                .reducer((List<String> state, CombinedAction action) -> {
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
                ReducersMapObject.<CombinedAction>builder() //
                        .add("counter").withInitialValue(0) //
                        .reducer((Integer state, CombinedAction action) -> {
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

    //TODO throws an error on first call if a reducer returns undefined initializing

    @Test(expected = UnsupportedOperationException.class)
    public void catchesErrorThrownInReducerWhenInitializingAndRethrow() {
        Reducer<CombinedAction> reducer = combineReducers(
                ReducersMapObject.<CombinedAction>builder() //
                        .add("throwingReducer").withStateType(Object.class) //
                        .reducer((Object state, CombinedAction action) -> {
                            throw new UnsupportedOperationException("Error thrown in reducer");
                        }).build());
        reducer.apply(State.empty(), null);
    }

    //TODO allows a symbol to be used as an action type

    @Test
    public void maintainsReferentialEqualityIfTheReducersItIsCombiningDo() {
        Reducer<CombinedAction> reducer = combineReducers(
                ReducersMapObject.<CombinedAction>builder() //
                        .add("child1").withInitialValue(new Object()) //
                        .reducer((Object state, CombinedAction action) -> state)
                        .add("child2").withInitialValue(new Object()) //
                        .reducer((Object state, CombinedAction action) -> state)
                        .add("child3").withInitialValue(new Object()) //
                        .reducer((Object state, CombinedAction action) -> state)
                        .build());
        State initialState = reducer.apply(null, new CombinedAction(null, null));
        assertSame(initialState, reducer.apply(initialState, new CombinedAction("FOO", null)));
    }
    
    @Test
    public void doesNotHaveReferentialEqualityIfOneOfTheReducersChangesSomething() {
        Reducer<CombinedAction> reducer = combineReducers(
                ReducersMapObject.<CombinedAction>builder() //
                        .add("child1").withInitialValue(new Object()) //
                        .reducer((Object state, CombinedAction action) -> state)
                        .add("child2").withInitialValue(0) //
                        .reducer((Integer state, CombinedAction action) -> {
                            if (action.getType() != null && action.getType().toString().equals("increment")) {
                                return state + 1;
                            }
                            return state;
                        })
                        .add("child3").withInitialValue(new Object()) //
                        .reducer((Object state, CombinedAction action) -> state)
                        .build());
        State initialState = reducer.apply(null, new CombinedAction(null, null));
        assertNotSame(initialState, reducer.apply(initialState, new CombinedAction("increment", null)));
    }

    //TODO throws an error on first call if a reducer attempts to handle a private action

    //TODO warns if no reducers are passed to combineReducers

    //TODO warns if input state does not match reducer shape

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
