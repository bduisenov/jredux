package org.js.redux;

/**
 * Created by bduisenov on 05/06/16.
 */
public interface ActionCreator<A> {

    A get(Object... args);

}
