package org.js.redux.helpers;

import org.js.redux.Action;

/**
 * Created by bduisenov on 02/06/16.
 */
public class TodoAction implements Action {

    public final ActionTypes type;

    public final String text;

    public TodoAction(ActionTypes type, String text) {
        this.type = type;
        this.text = text;
    }
}
