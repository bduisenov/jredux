package org.js.redux;

/* middleware */
public interface MiddlewareAPI<S> {

    Dispatch<S> dispatch();

    S getState();

}
