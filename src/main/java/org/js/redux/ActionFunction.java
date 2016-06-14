package org.js.redux;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by bduisenov on 14/06/16.
 */
public abstract class ActionFunction<T> extends Action implements BiFunction<Dispatch, Supplier<State>, T> {

    public ActionFunction() {
        super(null);
    }

}
