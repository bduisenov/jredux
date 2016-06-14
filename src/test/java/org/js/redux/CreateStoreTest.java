package org.js.redux;

import static org.js.redux.CreateStoreTest.Keys.bar;
import static org.js.redux.CreateStoreTest.Keys.foo;
import static org.js.redux.Redux.combineReducers;
import static org.js.redux.StoreCreator.createStore;
import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.js.redux.helpers.ActionCreators.dispatchInMiddle;
import static org.js.redux.helpers.ActionCreators.throwError;
import static org.js.redux.helpers.ActionCreators.unknownAction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    @Test
    public void supportsMultipleSubscriptions() {
        Store store = createStore(Reducers::todos);
        Listener listenerA = mock(Listener.class);
        Listener listenerB = mock(Listener.class);

        Subscription unsubscribeA = store.subscribe(listenerA);
        store.dispatch(unknownAction());
        verify(listenerA).onDispatch();
        verify(listenerB, never()).onDispatch();

        store.dispatch(unknownAction());
        verify(listenerA, times(2)).onDispatch();
        verify(listenerB, never()).onDispatch();

        Subscription unsubscribeB = store.subscribe(listenerB);
        verify(listenerA, times(2)).onDispatch();
        verify(listenerB, never()).onDispatch();

        store.dispatch(unknownAction());
        verify(listenerA, times(3)).onDispatch();
        verify(listenerB).onDispatch();

        unsubscribeA.unsubscribe();
        verify(listenerA, times(3)).onDispatch();
        verify(listenerB).onDispatch();

        store.dispatch(unknownAction());
        verify(listenerA, times(3)).onDispatch();
        verify(listenerB, times(2)).onDispatch();

        unsubscribeB.unsubscribe();
        verify(listenerA, times(3)).onDispatch();
        verify(listenerB, times(2)).onDispatch();

        store.dispatch(unknownAction());
        verify(listenerA, times(3)).onDispatch();
        verify(listenerB, times(2)).onDispatch();

        unsubscribeA = store.subscribe(listenerA);
        verify(listenerA, times(3)).onDispatch();
        verify(listenerB, times(2)).onDispatch();

        store.dispatch(unknownAction());
        verify(listenerA, times(4)).onDispatch();
        verify(listenerB, times(2)).onDispatch();
    }

    @Test
    public void onlyRemovesListenerOnceWhenUnsubscribeIsCalled() {
        Store store = createStore(Reducers::todos);
        Listener listenerA = mock(Listener.class);
        Listener listenerB = mock(Listener.class);

        Subscription unsubscribeA = store.subscribe(listenerA);
        store.subscribe(listenerB);

        unsubscribeA.unsubscribe();
        unsubscribeA.unsubscribe();

        store.dispatch(unknownAction());
        verify(listenerA, never()).onDispatch();
        verify(listenerB).onDispatch();
    }

    @Test
    public void onlyRemovesRelevantListenerWhenUnsubscribeIsCalled() {
        Store store = createStore(Reducers::todos);
        Listener listener = mock(Listener.class);
        store.subscribe(listener);

        Subscription unsubscribeSecond = store.subscribe(listener);

        unsubscribeSecond.unsubscribe();
        unsubscribeSecond.unsubscribe();

        store.dispatch(unknownAction());
        verify(listener).onDispatch();
    }

    @Test
    public void supportsRemovingASubscriptionWithinASubscription() {
        Store store = createStore(Reducers::todos);
        Listener listenerA = mock(Listener.class);
        Listener listenerB = mock(Listener.class);
        Listener listenerC = mock(Listener.class);

        store.subscribe(listenerA);
        AtomicReference<Subscription> unSubBRef = new AtomicReference<>();
        Subscription unSubB = store.subscribe(() -> {
            listenerB.onDispatch();
            unSubBRef.get().unsubscribe();
        });
        unSubBRef.set(unSubB);
        store.subscribe(listenerC);

        store.dispatch(unknownAction());
        store.dispatch(unknownAction());

        verify(listenerA, times(2)).onDispatch();
        verify(listenerB).onDispatch();
        verify(listenerC, times(2)).onDispatch();
    }

    @Test
    public void delaysUnsubscribeUntilTheEndOfCurrentDispatch() {
        Store store = createStore(Reducers::todos);

        List<Subscription> unsubscribeHandles = new ArrayList<>();
        Runnable doUnsubscribeAll = () -> unsubscribeHandles.forEach(Subscription::unsubscribe);

        Listener listener1 = mock(Listener.class);
        Listener listener2 = mock(Listener.class);
        Listener listener3 = mock(Listener.class);

        unsubscribeHandles.add(store.subscribe(listener1::onDispatch));
        unsubscribeHandles.add(store.subscribe(() -> {
            listener2.onDispatch();
            doUnsubscribeAll.run();
        }));
        unsubscribeHandles.add(store.subscribe(listener3::onDispatch));

        store.dispatch(unknownAction());
        verify(listener1).onDispatch();
        verify(listener2).onDispatch();
        verify(listener3).onDispatch();

        store.dispatch(unknownAction());
        verify(listener1).onDispatch();
        verify(listener2).onDispatch();
        verify(listener3).onDispatch();
    }

    @Test
    public void delaysSubscribeUntilTheEndOfCurrentDispatch() {
        Store store = createStore(Reducers::todos);

        Listener listener1 = mock(Listener.class);
        Listener listener2 = mock(Listener.class);
        Listener listener3 = mock(Listener.class);
        // as a workaround with java's effectively final constraint
        AtomicBoolean listener3Added = new AtomicBoolean(false);
        Runnable maybeAddThirdListener = () -> {
            if (!listener3Added.get()) {
                listener3Added.set(true);
                store.subscribe(listener3::onDispatch);
            }
        };
        store.subscribe(listener1::onDispatch);
        store.subscribe(() -> {
            listener2.onDispatch();
            maybeAddThirdListener.run();
        });
        store.dispatch(unknownAction());
        verify(listener1).onDispatch();
        verify(listener2).onDispatch();
        verify(listener3, never()).onDispatch();

        store.dispatch(unknownAction());
        verify(listener1, times(2)).onDispatch();
        verify(listener2, times(2)).onDispatch();
        verify(listener3).onDispatch();
    }

    @Test
    public void usesTheLastSnapshotOfSubscribersDuringNestedDispatch() {
        Store store = createStore(Reducers::todos);

        Listener listener1 = mock(Listener.class);
        Listener listener2 = mock(Listener.class);
        Listener listener3 = mock(Listener.class);
        Listener listener4 = mock(Listener.class);

        AtomicReference<Subscription> unsubscribe4Ref = new AtomicReference<>();
        AtomicReference<Subscription> unsubscribe1Ref = new AtomicReference<>();
        Subscription unsubscribe1 = store.subscribe(() -> {
            listener1.onDispatch();
            verify(listener1).onDispatch();
            verify(listener2, never()).onDispatch();
            verify(listener3, never()).onDispatch();
            verify(listener4, never()).onDispatch();

            unsubscribe1Ref.get().unsubscribe();
            Subscription unsubscribe4 = store.subscribe(listener4);
            unsubscribe4Ref.set(unsubscribe4);
            store.dispatch(unknownAction());
            verify(listener1).onDispatch();
            verify(listener2).onDispatch();
            verify(listener3).onDispatch();
            verify(listener4).onDispatch();
        });
        unsubscribe1Ref.set(unsubscribe1);
        store.subscribe(listener2);
        store.subscribe(listener3);

        store.dispatch(unknownAction());
        verify(listener1).onDispatch();
        verify(listener2, times(2)).onDispatch();
        verify(listener3, times(2)).onDispatch();
        verify(listener4).onDispatch();

        unsubscribe4Ref.get().unsubscribe();
        store.dispatch(unknownAction());
        verify(listener1).onDispatch();
        verify(listener2, times(3)).onDispatch();
        verify(listener3, times(3)).onDispatch();
        verify(listener4).onDispatch();
    }

    @Test
    public void providesAnUpToDateStateWhenASubscriberIsNotified() {
        Store store = createStore(Reducers::todos);
        store.subscribe(() -> {
            assertEquals(State.of(Collections.singletonList(new Todo(1, "Hello"))), store.getState());
        });
        store.dispatch(addTodo("Hello"));
    }

    //TODO only accepts plain object actions

    @Test
    public void handlesNestedDispatchesGracefully() {
        Store store = createStore(combineReducers(ReducersMapObject.builder() //
                .add(foo).withInitialValue(0).reducer((state, action) -> action.type == foo ? 1 : state) //
                .add(bar).withInitialValue(0).reducer((state, action) -> action.type == bar ? 2 : state) //
                .build()));
        store.subscribe(() -> {
            State state = store.getState();
            if (state.get(bar, Integer.class).orElse(0) == 0) {
                store.dispatch(Action.of(bar));
            }
        });
        store.dispatch(Action.of(foo));
        assertEquals(State.of(foo, 1, bar, 2), store.getState());
    }

    @Test
    public void doesNotAllowDispatchFromWithinAReducer() {
        Store store = createStore(Reducers::dispatchInTheMiddleOfReducer);
        try {
            store.dispatch(dispatchInMiddle(() -> store.dispatch(unknownAction())));
            fail();
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().matches(".*may not dispatch.*"));
        }
    }

    @Test
    public void recoversFromAnErrorWithinAReducer() {
        Store store = createStore(Reducers::errorThrowingReducer);
        try {
            store.dispatch(throwError());
            fail();
        } catch (UnsupportedOperationException e) {
            // ignore
        }

        store.dispatch(unknownAction());
    }

    @Test
    public void throwsIfActionTypeIsMissing() {
        Store store = createStore(Reducers::todos);
        try {
            store.dispatch(Action.empty());
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().matches(".*Actions may not have an undefined \"type\" property.*"));
        }
    }

    @Test
    public void throwsIfActionTypeIsUndefined() {
        Store store = createStore(Reducers::todos);
        try {
            store.dispatch(Action.of(null));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().matches(".*Actions may not have an undefined \"type\" property.*"));
        }
    }

    //TODO does not throw if action type is falsy

    @Test
    public void acceptsEnhancerAsTheThirdArgument() {
        AtomicBoolean called = new AtomicBoolean(false);
        StoreEnhancer spyEnhancer = vanillaStore -> (reducer, preloadedState) -> {
            return new DelegatingStore(vanillaStore.apply(reducer, preloadedState)) {

                @Override
                public Action dispatch(Action action) {
                    called.set(true);
                    return store.dispatch(action);
                }
            };
        };
        Store store = createStore(Reducers::todos, State.empty(), spyEnhancer);
        Action action = addTodo("Hello");
        store.dispatch(action);

        assertTrue(called.get());
        assertEquals(State.of(Collections.singletonList(new Todo(1, "Hello"))), store.getState());
    }

    @Test
    public void acceptsEnhancerAsTheSecondArgumentIfInitialStateIsMissing() {
        AtomicBoolean called = new AtomicBoolean(false);
        StoreEnhancer spyEnhancer = vanillaStore -> (reducer, preloadedState) -> {
            return new DelegatingStore(vanillaStore.apply(reducer, preloadedState)) {

                @Override
                public Action dispatch(Action action) {
                    called.set(true);
                    return store.dispatch(action);
                }
            };
        };
        Store store = createStore(Reducers::todos, spyEnhancer);
        Action action = addTodo("Hello");
        store.dispatch(action);

        assertTrue(called.get());
        assertEquals(State.of(Collections.singletonList(new Todo(1, "Hello"))), store.getState());
    }

    //TODO throws if enhancer is neither undefined nor a function

    @Test
    public void throwsIfNextReducerIsNotAFunction() {
        Store store = createStore(Reducers::todos);

        try {
            store.replaceReducer(null);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().matches(".*Expected the nextReducer to be a function.*"));
        }

        store.replaceReducer((state, action) -> null);
    }

    @Test
    public void throwsIfListenerIsNotAFunction() {
        Store store = createStore(Reducers::todos);

        try {
            store.subscribe(null);
        } catch (NullPointerException e) {
            // success
        }
    }

    // Symbol.observable interop point

    enum Keys {
        foo, bar
    }

}
