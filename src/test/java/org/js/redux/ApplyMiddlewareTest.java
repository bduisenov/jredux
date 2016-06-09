package org.js.redux;

import static org.js.redux.Redux.applyMiddleware;
import static org.js.redux.helpers.ActionCreators.addTodo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.js.redux.helpers.MiddlewareHelper;
import org.js.redux.helpers.Reducers;
import org.js.redux.helpers.Todo;
import org.junit.Test;

/**
 * Created by bduisenov on 06/06/16.
 */
public class ApplyMiddlewareTest {

    @Test
    public void wrapsDispatchMethodWithMiddlewareOnce() {
        Middleware test = middlewareAPI -> next -> action -> {
            assertNotNull(middlewareAPI.dispatch());
            assertNotNull(middlewareAPI.getState());
            return next.apply(action);
        };
        Store store = applyMiddleware(test, MiddlewareHelper::thunk) //
                .apply(StoreCreator::createStore) //
                .apply(Reducers::todos);

        store.dispatch(addTodo("Use Redux"));
        store.dispatch(addTodo("Flux FTW!"));

        assertEquals(State.of(Arrays.asList(new Todo(1, "Use Redux"), new Todo(2, "Flux FTW!"))), store.getState());
    }

}
