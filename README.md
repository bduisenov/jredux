# JRedux
JRedux is implementation of [Redux](https://github.com/reactjs/redux/) 3.5.x in java.

[![Build Status](https://travis-ci.org/bduisenov/jredux.svg?branch=master)](https://travis-ci.org/bduisenov/jredux)

### The Gist
```java
    enum ActionType {
        INCREMENT, DECREMENT
    }

    State counter(State state, Action<String> action) {
        state = (state == null) ? State.of(0) : state;
        if (action.type == INCREMENT) {
            return state.<Integer>get().map(v -> State.of(v + 1)).get();
        } else if (action.type == DECREMENT) {
            return state.<Integer>get().map(v -> State.of(v - 1)).get();
        }
        return state;
    }

    {
        Store store = createStore(this::counter);
        store.subscribe(() -> System.out.println(store.getState()));

        store.dispatch(Action.of(INCREMENT));
        store.dispatch(Action.of(INCREMENT));
        store.dispatch(Action.of(DECREMENT));
    }
```

###### more examples

```java

    {
        Store store = applyMiddleware(this::thunk) //
                .apply(StoreCreator::createStore) //
                .apply(Reducers::todos, State.of(Collections.singletonList(new Todo(1, "Say"))));
        store.subscribe(() -> System.out.println(store.getState()));

        store.dispatch(addTodo("Hello"));
        // State{{SINGLE_KEY=[Todo{id=1, text='Say'}, Todo{id=2, text='Hello'}]}}
        
        store.dispatch(addTodoIfEmpty("World"));

        store.dispatch(addTodoAsync("Maybe")).thenRunAsync(() -> {
            System.out.println(Thread.currentThread().getName());
        });
        // State{{SINGLE_KEY=[Todo{id=1, text='Say'}, Todo{id=2, text='Hello'}, Todo{id=3, text='Maybe'}]}}
        // ForkJoinPool.commonPool-worker-1
    }

```

### Current status (test coverage)
  + applyMiddleware (**covered**)
    + wraps dispatch method with middleware once (**covered**)
    + passes recursive dispatches through the middleware chain (**covered**)
    + works with thunk middleware (**covered**)
    + keeps unwrapped dispatch available while middleware is initializing (**covered**)

  + ~~bindActionCreators~~ (*TODO*)
    + ~~wraps the action creators with the dispatch function~~ (*TODO*)
    + ~~skips non-function values in the passed object~~ (*TODO*)
    + ~~supports wrapping a single function only~~ (*TODO*)
    + ~~throws for an undefined actionCreator~~ (*TODO*)
    + ~~throws for a null actionCreator~~ (*TODO*)
    + ~~throws for a primitive actionCreator~~ (*TODO*)

  + Utils
    + combineReducers
      + returns a composite reducer that maps the state keys to given reducers (**covered**)
      + ~~ignores all props which are not a function~~ (*skipped*)
      + throws an error if a reducer returns undefined handling an action (**covered**)
      + throws an error on first call if a reducer returns undefined initializing (**covered**)
      + catches error thrown in reducer when initializing and re-throw (**covered**)
      + ~~allows a symbol to be used as an action type~~ (*skipped*)
      + maintains referential equality if the reducers it is combining do (**covered**)
      + does not have referential equality if one of the reducers changes something (**covered**)
      + ~~throws an error on first call if a reducer attempts to handle a private action~~ (*TODO*)
      + ~~warns if no reducers are passed to combineReducers~~ (*skipped*)
      + warns if input state does not match reducer shape (**covered**)
    + compose (**covered**)
      + composes from right to left (**covered**)
      + composes functions from right to left (**covered**)
      + can be seeded with multiple arguments (**covered**)
      + returns the first given argument if given no functions (**covered**)
      + returns the first function if given only one (**covered**)
  + createStore
    + ~~exposes the public API~~ (*skipped*)
    + ~~throws if reducer is not a function~~ (*skipped*)
    + passes the initial action and the initial state (**covered**)
    + applies the reducer to the previous state (**covered**)
    + ~~applies the reducer to the initial state~~ (*TODO*)
    + preserves the state when replacing a reducer (**covered**)
    + supports multiple subscriptions (**covered**)
    + only removes listener once when unsubscribe is called (**covered**)
    + only removes relevant listener when unsubscribe is called (**covered**)
    + supports removing a subscription within a subscription (**covered**)
    + delays unsubscribe until the end of current dispatch (**covered**)
    + delays subscribe until the end of current dispatch (**covered**)
    + uses the last snapshot of subscribers during nested dispatch (**covered**)
    + provides an up-to-date state when a subscriber is notified (**covered**)
    + ~~only accepts plain object actions~~ (*skipped*)
    + handles nested dispatches gracefully (**covered**)
    + does not allow dispatch() from within a reducer (**covered**)
    + recovers from an error within a reducer (**covered**)
    + throws if action type is missing (**covered**)
    + throws if action type is undefined (**covered**)
    + ~~does not throw if action type is falsy~~ (*skipped*)
    + accepts enhancer as the third argument (**covered**)
    + accepts enhancer as the second argument if initial state is missing (**covered**)
    + ~~throws if enhancer is neither undefined nor a function~~ (*skipped*)
    + throws if nextReducer is not a function (**covered**)
    + throws if listener is not a function (**covered**)
    + ~~Symbol.observable interop point~~ (*skipped*)
      + ~~should exist~~ (*skipped*)
      + ~~should pass an integration test with no unsubscribe~~ (*skipped*)
      + ~~should pass an integration test with an unsubscribe~~ (*skipped*)
      + ~~should pass an integration test with a common library (RxJS)~~ (*skipped*)
      + ~~returned value~~ (*skipped*)
        + ~~should be subscribable~~ (*skipped*)
        + ~~should throw a TypeError if an observer object is not supplied to subscribe~~ (*skipped*)
        + ~~should return a subscription object when subscribed~~ (*skipped*)


License
----

MIT
