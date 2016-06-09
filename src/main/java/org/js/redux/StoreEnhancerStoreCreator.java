package org.js.redux;

/**
 * Created by bduisenov on 05/06/16.
 */
@FunctionalInterface
public interface StoreEnhancerStoreCreator {

    Store apply(Reducer reducer, State preloadedState);

}
