package org.js.redux.helpers;

import org.js.redux.Action;

/**
 * Created by bduisenov on 02/06/16.
 */
public class TodoAction implements Action {

    public ActionTypes type;

    public String text;

    public TodoAction() {
    }

    public TodoAction(ActionTypes type, String text) {
        this.type = type;
        this.text = text;
    }
}
