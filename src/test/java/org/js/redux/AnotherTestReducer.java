package org.js.redux;

/**
 * Created by bduisenov on 01/06/16.
 */
public enum AnotherTestReducer implements Reducer<TestState, TestAction> {

    func;

    @Override
    public TestState apply(TestState state, TestAction action) {
        int value = firstNonNull(state, new TestState(0)).getValue();
        switch (action) {
            case INCREMENET: {
                return new TestState(value + 1);
            }
            case DECREMENT: {
                return new TestState(value - 1);
            }
            default: return state;
        }
    }

    private static <T> T firstNonNull(T first, T second) {
        return first != null ? first : checkNotNull(second);
    }

    private static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

}
