package org.js.redux;

import java.util.function.Consumer;

/**
 * would be used as until another approach would be found
 * Created by bduisenov on 08/06/16.
 */
public abstract class ListenerWithItsSubscription implements Listener, Consumer<Subscription> {

    protected Subscription subscription;

    @Override
    public void accept(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public abstract void onDispatch();

}
