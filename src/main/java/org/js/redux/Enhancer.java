package org.js.redux;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by bduisenov on 02/06/16.
 */
public interface Enhancer<S extends State, A extends Action> extends
        Function<BiFunction<Reducer<S, A>, S, Store<S, A>>, BiFunction<Reducer<S, A>, S, Store<S, A>>> {

}
