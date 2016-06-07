package org.js.redux;

/**
 * Function to remove listener added by `Store.subscribe()`.
 */
@FunctionalInterface
public interface Subscription {

    void unsubscribe();

}
