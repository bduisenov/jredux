package org.js.redux;

/**
 * Created by bduisenov on 05/06/16.
 */
@FunctionalInterface
public interface StoreEnhancer {

    StoreEnhancerStoreCreator apply(StoreEnhancerStoreCreator next);

}
