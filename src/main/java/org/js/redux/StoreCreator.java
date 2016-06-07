package org.js.redux;

/**
 * Created by bduisenov on 05/06/16.
 */
public interface StoreCreator {

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     * 
     * @param <S>
     *            S State object type.
     */
    //<S> Store<S> createStore(Reducer<S> add);

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    //<S> Store<S> createStore(Reducer<S> add, StoreEnhancer<S> enhancer);

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    //<S> Store<S> createStore(Reducer<S> add, S preloadedState);

    /**
     * A store creator is a function that creates a Redux store. Like with dispatching function, we
     * must distinguish the base store creator, `createStore(add, preloadedState)` exported from
     * the Redux package, from store creators that are returned from the store enhancers.
     *
     * @param <S>
     *            S State object type.
     */
    //<S> Store<S> createStore(Reducer<S> add, S preloadedState, StoreEnhancer<S> enhancer);

}
