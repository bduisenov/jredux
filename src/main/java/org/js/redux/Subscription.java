package org.js.redux;

/**
 * Created by bduisenov on 01/06/16.
 */
@FunctionalInterface
public interface Subscription {

    void unsubscribe();

}
