package org.js.redux;

/**
 * A store enhancer is a higher-order function that composes a store creator to return a new,
 * enhanced store creator. This is similar to middleware in that it allows you to alter the store
 * interface in a composable way.
 *
 * Store enhancers are much the same concept as higher-order components in React, which are also
 * occasionally called “component enhancers”.
 *
 * Because a store is not an instance, but rather a plain-object collection of functions, copies can
 * be easily created and modified without mutating the original store. There is an example in
 * `compose` documentation demonstrating that.
 *
 * Most likely you’ll never write a store enhancer, but you may use the one provided by the
 * developer tools. It is what makes time travel possible without the app being aware it is
 * happening. Amusingly, the Redux middleware implementation is itself a store enhancer.
 *
 * Created by bduisenov on 05/06/16.
 */
@FunctionalInterface
public interface StoreEnhancer {

    StoreEnhancerStoreCreator apply(StoreEnhancerStoreCreator next);

}
