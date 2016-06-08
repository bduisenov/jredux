package org.js.redux;

import static org.js.redux.StoreCreator.createStore;
import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.js.redux.helpers.ActionCreators.unknownAction;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        Subscription unSubB = store.subscribe(new ListenerWithItsSubscription() {

            @Override
            public void onDispatch() {
                listenerB.onDispatch();
                subscription.unsubscribe();
            }
        });
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

    boolean listener3Added = false;
    @Test
    public void delaysSubscribeUntilTheEndOfCurrentDispatch() {
        Store store = createStore(Reducers::todos);

        Listener listener1 = mock(Listener.class);
        Listener listener2 = mock(Listener.class);
        Listener listener3 = mock(Listener.class);

        Runnable maybeAddThirdListener = () -> {
            if (!listener3Added) {
                listener3Added = true;
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

}
