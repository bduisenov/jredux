package org.js.redux;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by bduisenov on 05/06/16.
 */
public class StoreCreator {

    private static String undefinedActionTypeMessage = "Actions may not have an undefined \"type\" property. Have you misspelled a constant?";

    private static String illegalUsageOfReducerMessage = "Reducers may not dispatch actions.";

    private StoreCreator() {
        //
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from the
     * Redux package, from store creators that are returned from the store enhancers.
     * 
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer reducer) {
        return createStore(reducer, (State) null);
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from the
     * Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer reducer, StoreEnhancer enhancer) {
        return null;
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from the
     * Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer reducer, State preloadedState) {
        Store store = new Store() {

            private Reducer currentReducer = reducer;

            private State currentState = preloadedState;

            private List<Listener> currentListeners = new CopyOnWriteArrayList<>();
            private List<Listener> nextListeners = currentListeners;
            private boolean isDispatching = false;

            @Override
            public Action dispatch(Action action) {
                if (action.type == null) {
                    throw new NullPointerException(undefinedActionTypeMessage);
                }
                if (isDispatching) {
                    throw new UnsupportedOperationException(illegalUsageOfReducerMessage);
                }
                try {
                    isDispatching = true;
                    currentState = currentReducer.apply(currentState, action);
                } finally {
                    isDispatching = false;
                }
                for (Listener listener : (currentListeners = nextListeners)) {
                    listener.onDispatch();
                }
                return action;
            }

            @Override
            public State getState() {
                return currentState;
            }

            @Override
            public Subscription subscribe(Listener listener) {
                if (listener == null) {
                    throw new NullPointerException("Expected listener must not be null.");
                }
                nextListeners.add(listener);
                Subscription subscription = new Subscription() {

                    boolean isSubscribed = true;

                    @Override
                    public void unsubscribe() {
                        if (!isSubscribed) {
                            return;
                        }
                        isSubscribed = false;
                        nextListeners.remove(listener);
                    }
                };
                if (listener instanceof ListenerWithItsSubscription) {
                    ((ListenerWithItsSubscription) listener).accept(subscription);
                }
                return subscription;
            }

            @Override
            public void replaceReducer(Reducer nextReducer) {
                currentReducer = nextReducer;
                dispatch(Action.of(Redux.ActionTypes.INIT));
            }

            @Override
            public StoreCreator createStore() {
                return null;
            }
        };
        store.dispatch(Action.of(Redux.ActionTypes.INIT));
        return store;
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from the
     * Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer add, State preloadedState, StoreEnhancer enhancer) {
        return null;
    }

}
