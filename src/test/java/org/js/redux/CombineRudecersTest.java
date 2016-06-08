package org.js.redux;

import static org.js.redux.CombineRudecersTest.ReducerKeys.child1;
import static org.js.redux.CombineRudecersTest.ReducerKeys.child2;
import static org.js.redux.CombineRudecersTest.ReducerKeys.child3;
import static org.js.redux.CombineRudecersTest.ReducerKeys.counter;
import static org.js.redux.CombineRudecersTest.ReducerKeys.stack;
import static org.js.redux.CombineRudecersTest.ReducerKeys.throwingReducer;
import static org.js.redux.CombineRudecersTest.Type.FOO;
import static org.js.redux.CombineRudecersTest.Type.increment;
import static org.js.redux.CombineRudecersTest.Type.push;
import static org.js.redux.CombineRudecersTest.Type.whatever;
import static org.js.redux.Redux.combineReducers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(counter).withInitialValue(0) //
                .reducer((state, action) -> //
                (action.type == increment) ? state + 1 : state) //
                .add(stack).withInitialValue(Collections.<String>emptyList()) //
                .reducer((state, action) -> {
                    if (action.type == push) {
                        List<String> newState = new ArrayList<>(state.size() + 1);
                        newState.addAll(state);
                        newState.add(action.getValue(String.class).orElse(null));
                        return newState;
                    }
                    return state;
                }).build());

        State s1 = reducer.apply(State.empty(), Action.of(increment));
        assertEquals(State.of(counter, 1, stack, Collections.emptyList()), s1);
        State s2 = reducer.apply(s1, Action.of(push, "a"));
        assertEquals(State.of(counter, 1, stack, Collections.singletonList("a")), s2);
    }

    @Test
    public void throwsAnErrorIfAReducerReturnsUndefinedHandlingAnAction() {
        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(counter).withInitialValue(0) //
                .reducer((state, action) -> {
                    if (action != null && action.type != null) {
                        switch (action.type.toString()) {
                            case "increment":
                                return state + 1;
                            case "decrement":
                                return state - 1;
                            case "whatever":
                                return null;
                            default:
                                return state;
                        }
                    }
                    return null;
                }).build());

        try {
            reducer.apply(State.of(counter, 0), Action.of(whatever));
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().matches(".*whatever.*counter.*"));
        }

        try {
            reducer.apply(State.of(counter, 0), null);
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().matches(".*counter.*an action.*"));
        }

        try {
            reducer.apply(State.of(counter, 0), Action.of());
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().matches(".*counter.*an action.*"));
        }
    }

    @Test
    public void throwsAnErrorOnFirstCallIfAReducerReturnsUndefinedInitializing() {
        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(counter).withStateType(Integer.class) //
                .reducer((state, action) -> {
                    switch (action.type.toString()) {
                        case "increment":
                            return state + 1;
                        case "decrement":
                            return state - 1;
                        default:
                            return state;
                    }
                }).build());

        try {
            reducer.apply(State.empty(), null);
        } catch (IllegalStateException e) {
            assertTrue(e.getSuppressed()[0].getMessage().matches(".*counter.*initialization.*"));
        }

    }

    @Test
    public void catchesErrorThrownInReducerWhenInitializingAndRethrow() {
        String errorMessage = "Error thrown in reducer";
        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(throwingReducer).withStateType(Object.class) //
                .reducer((state, action) -> {
                    throw new UnsupportedOperationException(errorMessage);
                }).build());

        try {
            reducer.apply(State.empty(), null);
        } catch (UnsupportedOperationException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

    //TODO allows a symbol to be used as an action type

    @Test
    public void maintainsReferentialEqualityIfTheReducersItIsCombiningDo() {
        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(child1).withInitialValue(new Object()) //
                .reducer((state, action) -> state) //
                .add(child2).withInitialValue(new Object()) //
                .reducer((state, action) -> state) //
                .add(child3).withInitialValue(new Object()) //
                .reducer((state, action) -> state) //
                .build());

        State initialState = reducer.apply(null, Action.of());
        assertSame(initialState, reducer.apply(initialState, Action.of(FOO)));
    }

    @Test
    public void doesNotHaveReferentialEqualityIfOneOfTheReducersChangesSomething() {
        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(child1).withInitialValue(new Object()) //
                .reducer((state, action) -> state) //
                .add(child2).withInitialValue(0) //
                .reducer((state, action) -> {
                    if (action.type != null && action.type.toString().equals("increment")) {
                        return state + 1;
                    }
                    return state;
                }) //
                .add(child3).withInitialValue(new Object()) //
                .reducer((state, action) -> state) //
                .build());

        State initialState = reducer.apply(null, Action.of());
        assertNotSame(initialState, reducer.apply(initialState, Action.of(increment)));
    }

    //TODO throws an error on first call if a reducer attempts to handle a private action

    //TODO warns if no reducers are passed to combineReducers

    //TODO warns if input state does not match reducer shape

    enum ReducerKeys {
        counter, stack, throwingReducer, child1, child2, child3
    }

    enum Type {
        increment, decrement, push, whatever, FOO;
    }
}
