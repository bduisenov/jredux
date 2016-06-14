package org.js.redux;

import static org.js.redux.CombineRudecersTest.ReducerKeys.bar;
import static org.js.redux.CombineRudecersTest.ReducerKeys.child1;
import static org.js.redux.CombineRudecersTest.ReducerKeys.child2;
import static org.js.redux.CombineRudecersTest.ReducerKeys.child3;
import static org.js.redux.CombineRudecersTest.ReducerKeys.counter;
import static org.js.redux.CombineRudecersTest.ReducerKeys.qux;
import static org.js.redux.CombineRudecersTest.ReducerKeys.stack;
import static org.js.redux.CombineRudecersTest.ReducerKeys.throwingReducer;
import static org.js.redux.CombineRudecersTest.Type.FOO;
import static org.js.redux.CombineRudecersTest.Type.decrement;
import static org.js.redux.CombineRudecersTest.Type.increment;
import static org.js.redux.CombineRudecersTest.Type.push;
import static org.js.redux.CombineRudecersTest.Type.whatever;
import static org.js.redux.Redux.combineReducers;
import static org.js.redux.StoreCreator.createStore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

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
                .reducer((List<String> state, Action<String> action) -> {
                    if (action.type == push) {
                        List<String> newState = new ArrayList<>(state.size() + 1);
                        newState.addAll(state);
                        newState.add(action.payload.orElse(null));
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
                        if (action.type == increment) {
                            return state + 1;
                        } else if (action.type == decrement) {
                            return state - 1;
                        } else if (action.type == whatever) {
                            return null;
                        }
                        return state;
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
            reducer.apply(State.of(counter, 0), Action.empty());
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().matches(".*counter.*an action.*"));
        }
    }

    @Test
    public void throwsAnErrorOnFirstCallIfAReducerReturnsUndefinedInitializing() {
        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(counter) //
                .reducer((Integer state, Action<Object> action) -> {
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
                .add(throwingReducer) //
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

        State initialState = reducer.apply(null, Action.empty());
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

        State initialState = reducer.apply(null, Action.empty());
        assertNotSame(initialState, reducer.apply(initialState, Action.of(increment)));
    }

    //TODO throws an error on first call if a reducer attempts to handle a private action

    //TODO warns if no reducers are passed to combineReducers

    @Test
    public void warnsIfInputStateDoesNotMatchReducerShape() {
        ConsoleHandler console = spy(new ConsoleHandler());
        Redux.logger.addHandler(console);
        Redux.logger.setUseParentHandlers(false);

        Reducer reducer = combineReducers(ReducersMapObject.builder() //
                .add(ReducerKeys.foo).withInitialValue(State.of(ReducerKeys.bar, 1)) //
                .reducer((state, action) -> state) //
                .add(ReducerKeys.baz).withInitialValue(State.of(qux, 3)) //
                .reducer((state, action) -> state) //
                .build());

        ArgumentCaptor<LogRecord> logRecordAC = ArgumentCaptor.forClass(LogRecord.class);
        doNothing().when(console).publish(logRecordAC.capture());

        reducer.apply(null, null);
        verifyZeroInteractions(console);

        reducer.apply(State.of(ReducerKeys.foo, State.of(ReducerKeys.bar, 2)), null);
        verifyZeroInteractions(console);

        reducer.apply(State.of(ReducerKeys.foo, State.of(ReducerKeys.bar, 2), ReducerKeys.baz, State.of(qux, 4)), null);
        verifyZeroInteractions(console);

        createStore(reducer, State.of(bar, 2));
        String message = logRecordAC.getValue().getMessage();
        assertTrue(message.matches("Unexpected key \"bar\".*createStore.*instead: \"foo\", \"baz\".*"));

        createStore(reducer, State.of(bar, 2, qux, 4));
        message = logRecordAC.getValue().getMessage();
        assertTrue(message.matches("Unexpected keys \"bar\", \"qux\".*createStore.*instead: \"foo\", \"baz\".*"));

        // /createStore has unexpected type of "Number".*keys: "foo", "baz"/

        reducer.apply(State.of(bar, 2), null);
        message = logRecordAC.getValue().getMessage();
        assertTrue(message.matches("Unexpected key \"bar\".*reducer.*instead: \"foo\", \"baz\".*"));

        reducer.apply(State.of(bar, 2, qux, 4), null);
        message = logRecordAC.getValue().getMessage();
        assertTrue(message.matches("Unexpected keys \"bar\", \"qux\".*reducer.*instead: \"foo\", \"baz\".*"));

        // /reducer has unexpected type of "Number".*keys: "foo", "baz"/
    }

    enum ReducerKeys {
        counter, stack, throwingReducer, child1, child2, child3, foo, baz, bar, qux
    }

    enum Type {
        increment, decrement, push, whatever, FOO;
    }
}
