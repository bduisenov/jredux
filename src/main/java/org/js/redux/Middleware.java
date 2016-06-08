package org.js.redux;

import java.util.function.Function;

/**
 * A middleware is a higher-order function that composes a dispatch function to return a new
 * dispatch function. It often turns async actions into actions.
 *
 * Middleware is composable using function composition. It is useful for logging actions, performing
 * side effects like routing, or turning an asynchronous API call into a series of synchronous
 * actions.
 */
public interface Middleware {

    <S> Function<Dispatch, Dispatch> apply(MiddlewareAPI<S> api);

}
