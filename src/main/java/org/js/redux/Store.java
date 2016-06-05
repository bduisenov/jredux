package org.js.redux;

import java.util.function.Function;

/**
 *
 * The store has the following responsibilities:
 * Holds application state;
 * Allows access to state via getState();
 * Allows state to be updated via dispatch(action);
 * Registers listeners via subscribe(listener);
 * Handles unregistering of listeners via the function returned by subscribe(listener).
 *
 * Created by bduisenov on 01/06/16.
 */
public interface Store<S extends State, A extends Action> {

    /**
     * Reads the state tree managed by the store.
     *
     * @returns The current state tree of your application.
     */
    S getState();

    /**
     * Dispatches an action. It is the only way to trigger a state change.
     *
     * The `reducer` function, used to create the store, will be called with the current state tree
     * and the given `action`. Its return value will be considered the **next** state of the tree,
     * and the change listeners will be notified.
     *
     * The base implementation only supports plain object actions. If you want to dispatch a
     * Promise, an Observable, a thunk, or something else, you need to wrap your store creating
     * function into the corresponding middleware. For example, see the documentation for the
     * `redux-thunk` package. Even the middleware will eventually dispatch plain object actions
     * using this method.
     *
     * @param action
     *            A plain object representing “what changed”. It is a good idea to keep actions
     *            serializable so you can record and replay user sessions, or use the time
     *            travelling `redux-devtools`. An action must have a `type` property which may not
     *            be `undefined`. It is a good idea to use string constants for action types.
     * 
     * @return For convenience, the same action object you dispatched. Note that, if you use a
     *         custom middleware, it may wrap `dispatch()` to return something else (for example, a
     *         Promise you can await).
     */
    A dispatch(A action);

    <R> R dispatch(Function<Function<A, A>, R> action);

    /**
     * Adds a change listener. It will be called any time an action is dispatched, and some part of
     * the state tree may potentially have changed. You may then call `getState()` to read the
     * current state tree inside the callback.
     *
     * You may call `dispatch()` from a change listener, with the following caveats:
     *
     * 1. The subscriptions are snapshotted just before every `dispatch()` call. If you subscribe or
     * unsubscribe while the listeners are being invoked, this will not have any effect on the
     * `dispatch()` that is currently in progress. However, the next `dispatch()` call, whether
     * nested or not, will use a more recent snapshot of the subscription list.
     *
     * 2. The listener should not expect to see all state changes, as the state might have been
     * updated multiple times during a nested `dispatch()` before the listener is called. It is,
     * however, guaranteed that all subscribers registered before the `dispatch()` started will be
     * called with the latest state by the time it exits.
     *
     * @param listener
     *            A callback to be invoked on every dispatch.
     * @return A function to remove this change listener.
     */
    Subscription subscribe(Listener listener);

}
