package org.js.redux;

/**
 * Created by bduisenov on 01/06/16.
 */
public class TestState implements State {

    public final int value;

    public TestState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
