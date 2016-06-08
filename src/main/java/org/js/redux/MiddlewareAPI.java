package org.js.redux;

/* middleware */
public interface MiddlewareAPI<S> {

    Dispatch dispatch();

    S getState();

}
