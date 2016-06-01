package org.js.redux;

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

    S getState();

    void dispatch(A action);

    Subscription subscribe(Listener listener);

}
