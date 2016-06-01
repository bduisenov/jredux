package org.js.redux;

import java.util.function.BiFunction;

/**
 * Actions describe the fact that something happened, but don’t specify how the application’s state
 * changes in response. This is the job of a reducer.
 * 
 * Created by bduisenov on 01/06/16.
 */
public interface Reducer<S extends State, A extends Action> extends BiFunction<S, A, S> {

}
