package org.js.redux;

/* middleware */
public interface MiddlewareAPI {

    Dispatch dispatch();

    State getState();

}
