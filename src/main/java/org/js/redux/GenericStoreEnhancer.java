package org.js.redux;

import java.util.function.Function;

/**
 * Created by bduisenov on 05/06/16.
 */
@FunctionalInterface
public interface GenericStoreEnhancer {

    Function<Reducer, Store> apply(StoreEnhancerStoreCreator next);

}
