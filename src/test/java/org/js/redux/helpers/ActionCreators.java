package org.js.redux.helpers;

/**
 * Created by bduisenov on 02/06/16.
 */
public class ActionCreators {

    public static TodoAction addTodo(String text) {
        return new TodoAction(ActionTypes.ADD_TODO, text);
    }

}
