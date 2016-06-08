package org.js.redux;

/**
 * Created by bduisenov on 05/06/16.
 */
public class StoreCreator {

    private StoreCreator() {
        //
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     * 
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer add) {
        return null;
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer add, StoreEnhancer enhancer) {
        return null;
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer add, State preloadedState) {
        return null;
    }

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    public static Store createStore(Reducer add, State preloadedState, StoreEnhancer enhancer) {
        return null;
    }

}
