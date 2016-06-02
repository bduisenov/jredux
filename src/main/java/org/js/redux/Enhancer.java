package org.js.redux;

import java.util.function.BiFunction;

/**
 * Created by bduisenov on 02/06/16.
 */
public interface Enhancer<S extends State, A extends Action> extends BiFunction<S, A, S> {

}
