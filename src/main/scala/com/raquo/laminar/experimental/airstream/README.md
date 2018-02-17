# Airstream


Airstream is a small state propagation and streaming library. Primary differences from other solutions:

- **Mandatory [ownership](#ownership) of leaky resources** – it is impossible to create a subscription without specifying when it shall be destroyed. This helps prevent memory leaks and unexpected behaviour.

- **No [FRP glitches](#frp-glitches)** – neither observables themselves nor their observers will ever see inconsistent state within a transaction, at no runtime cost.

- **One integrated system for three core types of observables** – Event streams alone are not a good enough abstraction for anything other than events.
  - EventStream (lazy, no current value)
  - Signal (lazy, has current value)
  - State (eager, with current value)

- **Small size, simple implementation** – easy to understand, easy to create custom streams. Does not bloat your Scala.js bundle size.

Airstream has a very generic design, but is primarily intended to serve as a reactive layer for unidirectional dataflow architecture as applied to hierarchical UI components. As such, it is not burdened by features that cause more problems than they solve in frontend development, such as backpressure and typed effects.

I created Airstream because I found existing solutions were not suitable for building reactive UI components. My original need for Airstream is to replace the old reactive layer of [Laminar](https://github.com/raquo/Laminar), but I'll be happy to see it used by other reactive UI libraries as well. Laminar in general is well modularized, and you can definitely reuse other bits and pieces of it, for example [Scala DOM Types](https://github.com/raquo/scala-dom-types).


## Community

Please use [github issues](https://github.com/raquo/Laminar/issues) for bugs, feature requests, as well as all kinds of discussions, including questions on usage and roadmap. I think this will work better than spreading thin across gitter / stackoverflow / etc. You can _watch_ this project on github to get issue updates if you're interested in following discussions.

Note: Airstream is only temporarily located in Laminar's repo. This is just for my own rapid development convenience. While Laminar does depend on Airstream, Airstream has no knowledge of Laminar at all. Airstream is designed as an independent project, and I will move it out when the next version of Laminar is released.


## Table of Contents

TODO[Docs] compile this when the docs stabilize



## Documentation

The provided documentation is a high level overview that occasionally dives into gritty details for things that are hard to figure on your own. It is not a full replacement to discovering available methods by reading the code (which is quite simple, and has comments) or simply with an IDE's autocomplete functionality.

This documentation is not an introduction to functional reactive programming. Instead, it explains the specifics of one library. I therefore assume basic knowledge of streams and observables here. If you need a primer on standard reactive programming, consider [this guide](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754) by André Staltz or its [video adaptation](https://egghead.io/courses/introduction-to-reactive-programming). 

This documentation is intended to be read top to bottom, sections further down the line assume knowledge of concepts and behaviours introduced in earlier sections.

Warning: there is some rambling involved. I will need to revise this at some point.



### EventStream

EventStream is a reactive variable that represents a stream of discrete events.

EventStream has no concept of "current value". It is a stream of events, and there is no such thing as a "current event". Philosophically, an event has either happened or is yet to happen.

EventStream is a **lazy** observable. That means that it will not receive or process events unless it has at least one Observer (more on this below).

When you add an Observer to a stream, it typically starts to send events to the observer from now on. Different streams could potentially have custom code in them overriding this behaviour however.

The result of calling `observable.addObserver(observer)(owner)` or `observable.foreach(onNext)(owner)` is a Subscription. To remove the observer you can call `observable.removeObserver(sameObserver)` or `subscription.kill()`. You can ignore the `owner` implicit param for now. Read about it later in the [Ownership](#ownership) section. 


### Laziness

Before exploring other kinds of observables, let's outline how exactly laziness works in Airstream.

A LazyObservable (an EventStream or a Signal, but we'll focus on streams for now) can have observers – both "external" observers that you add manually using `addObserver` or `foreach` methods, and InternalObserver-s. More on those soon.


#### Starting Observables

When a stream acquires its first observer (does not matter if external or internal), it is said to be **started**. So when you call `addObserver` on a stream for the first time, you **start** the stream. Airstream will then call the `stream.onStart` method, which must ensure that this stream wakes up and starts working. Someone started observing (caring about the output of) this stream – and so the stream must ensure that the events start coming in.

Typically this is done by adding an InternalObserver to the parent stream – the stream on which this one depends. For example, let's consider this scenario:

```scala
val foo: EventStream[Foo] = ???
val bar: EventStream[Bar] = foo.map(fooToBar)
val baz: EventStream[Baz] = bar.map(barToBaz)
val qux: EventStream[Qux] = baz.map(bazToQux)
val rap: EventStream[Rap] = qux.map(quxToRap)

baz.addObserver(bazObserver)
```

Until `baz.addObserver(bazObserver)` was called, these streams would not be receiving or emitting any events because they have no observers, internal or external. After `addObserver` is called, `baz.onStart` is called, adding `baz` as an InternalObserver to `bar`. `baz` will now receive and process any events emitted by `bar`.

But this means that `bar` just got its first observer – even if an internal one, it still matters – someone started caring, even if indirectly. So its `onStart` method is called, and it adds `bar` as the first InternalObserver to `foo`. Now, `foo` is started as well, and its `onStart` method does _something_ to ensure that `foo` will now start sending out events. We don't actually know what it is that needs to be done for `foo`, because we didn't define `foo`'s implementation. For example, its `onStart` method could be adding a DOM listener to a DOM element.

Now we see how adding an observer resulted in a chain of activations of all upstream streams that were required, directly or indirectly, to get the events out of the stream we actually wanted to observe. The `onStart` method ensured – recursively – that the observed stream is now running.

Adding another observer to the now already running streams – `foo` or `bar` or `baz` – would not need to cause such a chain reaction because the stream to which it is being added already has observers (internal or external).

Lastly, notice that the `qux` and `rap` streams are untouched by all this. No one cares for their output yet, so those streams will not receive any events, and neither `bazToQux` nor `quxToRap` will ever run (well, not until we add observers that need them to run, directly or indirectly).

On a lower level, how exactly is it that `qux` will not run? Put simply, it needs to be getting events from `baz` to process them and produce its own events, but it's getting nothing from `baz` simply because at this point `baz` does not know that `qux` exists. `baz` sends out its events to all of its observers, but so far nothing added `qux` as an observer to `baz`.

For extra clarity, while the stream `rap` does depend on `qux`, `rap` itself has no observers, so it is **stopped**. Nothing started it yet, and so nothing triggered its `onStart` method which would have added `rap` as an InternalObserver to `qux`, starting `qux` recursively as described above. 


#### Stopping Observables

Just like Observers can be added to streams, they can also be removed, e.g. with `removeObserver`. When you remove the last observer (internal or external) from a stream, it is said to be **stopped**. The same domino effect as when starting streams applies, except the `onStop` method recursively undoes everything that was done by `onStart` – instead of adding an InternalObserver to parent stream, we remove it, and if that causes the grand-parent stream to be stopped, we call its `onStop` method, and the chain continues upstream. 

When the dust settles, streams that are now without observers (internal or external) will be stopped, and those that still have observers will otherwise be untouched, except they will stop referencing the now-stopped observables in their lists of internal observers.


#### Memory Management Implications

Every observable that depends on another – parent, or upstream observable, – always has a reference to that parent, regardless of whether it's started or stopped.

However, the parent/upstream observable has no references to its child/downstream observable(s) until the child observable is started. Only then does the parent obtain a reference to the child, adding it to the list of its internal observers. 

This has straightforward memory management implications: nothing in Airstream is keeping references to a *stopped* observable. So, if you don't have any of your own references to a stopped Observable, it will be garbage collected, as expected.  

However, a started observable has additional references to it from:
 1) The parent/upstream observable on which this observable depends (via the parent's list of internal observers)
 2) The Subscription objects created by `addObserver` or `foreach` calls on this observable, if this observable has external observers

Remember that if a given observable is started, its parent is also guaranteed to be started, and so on. This creates a potentially long chain of observables that typically terminate with external observers on the downstream end, and some kind of an event producer on the upstream end. All of these reference each other, directly or indirectly, and so will not be garbage collected unless there are no more references in your program to any observable or observer in this whole graph.

Now imagine that in the chain of activated observables mentioned above the most downstream observable is related to a UI component that has since then been destroyed. You would want that now-irrelevant observable to be stopped in order for it to be garbage collected, since it's not needed anymore, but it will continue to run for as long as it has its observer. And if you forgot to remove that observer when you destroyed the UI component it related to, you got yourself a memory leak.  

This is a common memory management pattern for most streaming libraries out there, so it should come as no surprise to anyone familiar with event streams.

Some reactive UI libraries such as Outwatch give you a way to bind the lifecycle of subscriptions to the lifecycle of corresponding UI components, and that automatically kills the subscription (removes the observer) when the UI component it relates to is destroyed. However, the underlying streaming libraries that such UI libraries use have no concept of such binding, and so in those libraries you can manually call `stream.addObserver` and create a subscription that will not cease to exist together with the UI component that it conceptually relates to.

What makes Airstream special is that it has a concept of ownership. When creating a leaky resource, e.g. when calling `addObserver`, you _have to_ also provide a reference to the Owner who will eventually kill the subscription. For example, that owner could be a UI component to which the subscription relates, and it could automatically kill all subscriptions that it owns when it is destroyed, allowing the now-irrelevant observables to be stopped and garbage collected. For more details, see the [Ownership](#ownership) section.



### Signal

Signal is a reactive variable that represents a time-varying value, or an accumulated value.

Similar to EventStream, Signal is **lazy**, so everything in the [Laziness](#laziness) section applies to Signals as well.

Unlike EventStream, a Signal always has a current value. For instance, you could create a Signal by calling `val signal = eventStream.toSignal(initialValue)`. In that example, `signal`'s current value would first be equal to `initialValue`, and then any time `eventStream` emits a value, `signal`'s current value would be updated to the emitted value, and then `signal` would emit this new current value.

However, all of that would only happen if `signal` had any observers (because of [Laziness](#laziness)). If `signal` had no observers, its current value would be stuck at the last current value it saved while it had observers, or at `initialValue` if it never had observers.

This laziness makes `Signal` generally unsuitable to represent state because its current value might get inconsistent in the absence of observers. It is also the reason why you can't directly access a Signal's current value (there is no `now()` method on it). (TODO[Docs] WELL HOW DO I READ ITS CURRENT VALUE THEN HUH)

When adding an Observer to a Signal, it will immediately receive its current value, as well as any future values. If you don't want the observer to receive the current value, add an observer to the stream `signal.changes` instead.

Note: Signal's initial value is evaluated lazily where it is not provided explicitly. For example:

```scala
val fooStream: EventStream[Foo] = ???
val fooSignal: Signal[Foo] = fooStream.toSignal(myFoo)
val barSignal: Signal[Bar] = fooSignal.map(fooToBar)
```

In this example, `barSignal`'s initial value would be equal to `fooToBar(myFoo)`, but that expression will not be evaluated until it is needed (i.e. until `barSignal` acquires an observer). And once evaluated, it will not be re-evaluated again.



### State

As mentioned above, Signal's current value depends on whether it has observers or not. Therefore, Airstream also offers State – a reactive variable that is **eager**, not lazy, and remembers its current value.

State does need observers to run. State calls its own `onStart` method which works just as described in the [Starting Observables](#starting-observables) section above).

Therefore, all the lazy observables that the State depends on get started automatically when such dependent state is initialized. For example:

```scala
val owner: Owner = ???
val fooSignal: Signal[Foo] = ???
val barSignal: Signal[Bar] = fooSignal.map(fooToBar)
val barState: State[Bar] = barSignal.toState(owner) // mirrors barSignal
val bazState: State[Baz] = barState.map(barToBaz)
```

In tha above snippet both `fooSignal` and `barSignal` are stopped until `barState` is created on line 4, as they have no observers up to that point. When `barState` is initialized, its `onStart` method will recursively ensure that all upstream observables are started. So it adds `barState` as an internal observer on `barSignal`, which starts `barSignal`, and then barSignal's own `onStart` method similarly starts `fooSignal`.

So, we can count on State running as soon as it gets created, regardless of whether state itself has any observers. This means we can use State to store, well, state and accumulated values, such as a list of received events. The state will keep calculating said list even if currently it has no observers.

This is useful in situations where a parent component wants to maintain some state, but the only observers of such state are children components, of which there might be zero or more. Without eagerness, the state would go stale when there are temporarily zero children components because it would temporarily have no observers.

Because State's current value is guaranteed to be consistent regardless of observers, it is available via the `state.now()` method. Using that method will reduce the need for combining multiple observables, if all you want is to sample some State's current value.

Unlike Signal, State only fires an event when its next value is different from its current value. Signal does not have this behaviour partly because we can't rely on its current value being consistent.

Also unlike Signal, State's initial value is evaluated eagerly, but also only once.

Similar to Signal, State's external observers receive its current value immediately. And State too exposes a `changes` stream that does not send current value on subscription.

You might have noticed in the code snippet above that you need an `owner` to create State. We have previously seen this in the `addObserver(observer)(owner)` method signature, and the reason is the same – creating a State is essentially adding an observer* – a leaky operation that must be eventually undone. Read about that in the [Ownership](#ownership) section.

* This does not mean that State serves the same purpose as external observers. Airstream does not care, but for proper hygiene don't put side effects into State or any other observable, those belong in observers.



### Relationship between EventStream, Signal, and State

A Signal is not an EventStream. Both are a LazyObservable (need observers to run).

You can `fold(initialValue)(fn)` an EventStream into a Signal, or make a Signal directly with `stream.toSignal(initialValue)`.

A State is not a Signal. Both are a MemoryObservable (remember their current value).

You can get an EventStream of `changes` from a MemoryObservable (a Signal or a State) – this stream will re-emit whatever the parent signal/state emits (subject to laziness of the stream), minus the initial value.

Observable is the lowest common denominator between EventStream, Signal and State.

Observable has no `map` method because mapping over State is potentially a leaky operation (see [Ownership](#ownership). If you need to map over an Observable, make a LazyObservable out of it by calling `toLazy` on it first, and then `map` over that.



## Ownership

Alright, this is it. By now you've read enough to have many questions about how ownership works. This assumes you've read all the docs above, but to recap the core problem that ownership solves:

* Adding an `Observer` to a lazily evaluated `Observable` such as `EventStream` is a leaky operation. That is, this bond will survive even if the observable and the observer are both effectively unreachable to user code. This is because the observable's parent observables will keep a reference to it for as long as it has observers.
* Therefore, you need to remember to remove observers that you added when the observers are no longer needed.
* But doing that manually is insane, you will eventually forget and cause memory leaks. You should not need take out your own garbage in a garbage collected language.

If any of the above does not make sense, the rest of this section might be confusing. Make sure you at least understand the entirety of the [Laziness](#laziness) section.

Without further ado:

**Owned** is a resource that must be killed in order to release memory or prevent some other leak. In Airstream, both Subscription (the result of `observable.addObserver` call) and State extend Owned.

Owned has an **Owner**. an Owner is an object that keeps track of its possessions (Owned-s) and knows when to kill them, and kills them when it's time. Airstream does not have any concrete owner classes, just the base trait. The reactive UI library or even yourself should implement those.

For example, in my reactive UI library [Laminar](https://github.com/raquo/Laminar) ReactiveElement (an object representing a JS DOM Element) implements Owner. When a ReactiveElement is discarded (unmounted from the DOM), it kills all of its possessions, or more specifically – kills all its Owned-s, i.e. all the State and Subscriptions that were bound to it. 

So, if you're writing a Laminar component and want it to have internal State, you would specify that component as the owner of said state. Then when the component is no longer needed, neither is its State, and the component conveniently kills the component's State, removing it as an observer from any upstream observables. And if any of those don't have any more observers, they would be stopped, and if they are not referenced elsewhere in your code, garbage collected.

When implementing Owned, you can perform whatever leaky operations you wanted (e.g. in case of State, adding an observer to parent observable) in its constructor, and override the `onKilled` method to perform any required cleanup (e.g. remove said observer).

Owned-s are bound to a specific Owner upon creation, and this link stays unchanged for the lifetime of the Owned.

Owned-s are normally killed by their Owner, but you can also override the `owned.kill` method to make it public. That will let you kill an Owned manually. The Owner will be notified about this via `owner.onKilledExternally(owned)` so that it can drop the reference to `owned` from its possessions.

An Owned can only be killed once. Killing the same Owned multiple times is undefined behaviour. Built-in Owner tracks its kills properly, make sure to preserve that behaviour if extending it. 

### Ownership & Memory Management

In broad terms, ownership solves memory leaks by tying the lifecycle of Owned-s to the lifecycle of an Owner. It helps you but makes the assumption that Owned-s are contained "within" their Owner, and that if you bring them out, you do it in a way that does not defeat the purpose of such containerization.


#### State Considerations

For example, let's say you have a parent component (Owner) that owns some State (Owned). You want to share this state to child components, of which there could be any number, and they would change over time. So let's say the parent component represents a list of child components in the UI.

Now imagine that child components are just functions that accept parent's state:

```scala
def childListItem(parentState: State[Foo]): ReactiveElement = {
  val childState = parentState.map(blah)
  renderElement(childState)
}
```

Can you spot the memory leak? Spoiler alert: for all intents and purposes we would expect the `childListItem` component to own `childState`. But did we do anything to make it so? No, `parentState.map` preserves the owner of the state, so the owner of `childState` is still the parent component.

That is a problem because when an individual child component is discarded and unmounted, its `childState` will not be killed, because its owner, the parent component, is still alive and kicking. So every new child will create a new state object which will persist (not just bloat the memory – it will actually run!) until the parent component is discarded, and that might never even happen.

**The violation that caused this memory leak to happen is that we allowed a factory of one component's Owned-s (`parentState.map`) to escape into another component.**

This must be prevented with a simple rule of thumb: do not let State escape its owner. If you need to pass this state to a different component (owner), pass a Signal instead: `parentState.toSignal()`. Signal is a lazy observable and if the receiving component needs to create State out of it, it can do so with `signal.toState(newOwner)`:

```scala
def childListItem(parentSignal: Signal[Foo]): ReactiveElement = {
  val childOwner: Owner = ???
  val childState = parentSignal.map(blah).toState(childOwner)
  renderElement(childState)
}
``` 


#### Subscription Considerations

Subscription does not expose a factory to create more Owned resources, but it does keep references to both the Observable and the Observer that it bound. That means that if you're keeping a reference to a Subscription, you're also keeping those references. This does not affect execution, but might affect garbage collection if you're keeping this reference when it's no longer needed.

In practice this should not be a problem. You only need a reference to a Subscription in order to `kill` it manually, so you should have no reason to share it far and wide. 


### Sources of Events

We understand how events propagate through streams, signals and state, but the events in Airstream have to originate somewhere, right?


#### `EventStream.fromSeq`

```scala
object EventStream {
  def fromSeq[A](events: Seq[A]): EventStream[A] = ...
  ...
}
```

This method creates an event stream that synchronously emits events from the provided sequence one by one to any newly added observer.

Each event is emitted in a separate transaction, meaning that the propagation of the previous event will fully complete before the propagation of the new event starts.


#### `EventStream.periodic`

TODO[API] – implement this.


#### `EventBus`

`new EventBus[MyEvent]` is currently _the_ way to feed events into Airstream. EventBus exposes two properties:

**`stream`** is the stream of events emitted by the EventBus.

**`writer`** is a WriteBus object that lets you trigger EventBus events in a few ways.

WriteBus extends Observer, so you can call `onNext(newEventValue)` on it, or pass it as an observer to another stream's `addObserver` method. This will cause the event bus to emit `newEventValue` in a new transaction.

You can also call `addSource(otherStream)(owner)` on it, and the event bus will re-emit every event emitted by that stream. This is different from adding `writer` as an observer to `otherStream` because this will not cause otherStream to be started unless/until the EventBus's own stream is started (see [Laziness](#laziness)).

You've probably noticed that addSource takes `owner` as an implicit param – this is for memory management purposes. You would typically pass a WriteBus to a child component if you want the child to send any events to the parent. Thus, we want addSource to be automatically undone when said child is discarded (see [Ownership](#ownership)).

An EventBus can have multiple simultaneously sources. In that case it will emit events from all of those sources. EventBus emits every event in a new transaction. Note that EventBus lets you create loops of Observables. It is up to you to make sure that a propagation of an event through such loops eventually terminates (via a proper `.filter(passes)` gate for example, or an implicit non-equality filter in State).

You can manually remove a previously added source stream using `removeSource` or by calling `kill()` on the EventBusSource object returned by the addSource call.

EventBus is particularly useful to get a single stream of events from a dynamic list of child components. You basically pass down the `writer` to every child component, and inside the child component you can add a source stream to it, or add it as an observer to some stream. Then when any given child component is discarded, its connection to the event bus will also be severed.

Typically you don't pass EventBus itself down to child components as it provides both read and write access. Instead, you pass down either the writer or the stream, depending on what is needed. This security is the reason those are separate, by the way.

WriteBus comes with a way to create new writers. Consider this:

```scala
val eventBus = new EventBus[Foo]
val barWriter: WriteBus[Bar] = eventBus.writer.filterWriter(isGoodFoo).mapWriter(barToFoo)
```

Now you can send `Bar` events to `barWriter`, and they will appear in `eventBus` processed with `barToFoo` then and filtered by `isGoodFoo`. This is useful when you want to get events from a child component, but the child component does not or should not know what `Foo` is. Generally if you don't need such separation of concerns, you can just `map`/`filter` the stream that's feeding the EventBus instead.


#### Custom Observables

EventBus is a very generic solution that should suit most needs, even if perhaps not very elegantly sometimes.

You can create your own observables that emit events in their own unique way by wrapping or extending EventBus (easier) or extending Observable (more work and knowledge required)).

Unfortunately I don't have enough time to describe how to create custom observables in detail right now. You will need to read the rest of the documentation and the source code – you will see how other observables such as MapStream or FilterStream are implemented. Airstream's source code should be easy to comprehend. It is clean, small (a bit more than 1K LoC), and does not use complicated implicits or too much functional stuff.



### FRP Glitches

#### Other Libraries

A glitch in Functional Reactive Programming is a situation where inconsistent state is allowed to exist and exposed to either an observable or an observer. For example, consider the typical diamond case:

```scala
val numbers: EventStream[Int] = ???
val isPositive: EventStream[Boolean] = numbers.map(_ > 0)
val doubledNumbers: EventStream[Int] = numbers.map(_ * 2)
val combinedStream: EventStream[(Int, Boolean)] = doubledNumbers.combineWith(isPositive)
combinedStream.addObserver(combinedStreamObserver)(owner)
```

Now, without thinking too hard, what do you think `combinedStream` will emit when `numbers` emits `1`, assuming `-1` was previously emitted? You might expect that `isPositive` will emit `true`, doubledNumbers will emit `2`, and then combinedStream will emit a tuple `(2, true)`. That would make sense, and this is how Airstream works at no cost to you, and yet this is not how most streaming and state propagation libraries behave.

Most streaming libraries will introduce a **glitch** in this scenario, as they are implemented with unconditional depth-first propagation. So in other libraries when the event from `numbers` (`1`) propagates, it goes to `isPositive` (`true`), then to `combinedStream` (`(-1, true)`). And that's a glitch. `(-1, true)` is not a valid state, as -1 is not a positive number. Immediately afterwards, `doubledNumbers` will emit `1`, and finally combinedStream would emit `(1, true)`, the correct event.

Such behaviour is problematic in a few ways – first, you are now propagating two events on equal standing. Any observables (and in most other libraries, even observers!) downstream of `combinedStream` will see two events come in, the first one carrying invalid/incorrect state, and they will probably perform incorrect calculations or side effects because of that.

In general, glitches happen when you have an observable that _synchronously depends_ on multiple observables that _synchronously depend_ on a common ancestor or one of themselves. I'm using the term `synchronously depends` to describe a situation where emitting an event to a parent observable might result in the child observable also emitting it – synchronously. So `map` and `filter` would fall into this category, but `delay` wouldn't. 


#### Topological Rank

In the diamond-combine case described above Airstream avoids a glitch because CombineObservable-s (those created using combineWith method) do not propagate downstream immediately. Instead, they are put into a pendingObservables queue in the current transaction (we'll get to those soon). When the rest of the propagation within a transaction finishes, the propagation of the first pending observable is resumed. When that is finished, we propagate the first remaining pending observable, and so on.

So in our example, what happens in Airstream: after `isPositive` emits `true`, `combinedStream` is notified that one of its parents emitted a new event. Instead of emitting its own event, it adds itself to the list of pending observables. Then, as the `isPositive` branch finished propagating (for now), `doubledNumbers` emits `2`, and then again notifies `combinedStream` about this. `combinedStream` is already pending, so it just grabs and remembers the new value from this parent. At this point the propagation of `numbers` is complete (assuming no other branches exist), and Airstream checks `pendingObservables`on the current transaction. It finds only one – `combinedStream`, and re-starts the propagation from there. The only thing left to do in our example is to send the new event – `(2, true)` to `combinedStreamObserver`.

Now, only this simple example could work with such logic. The important bit that makes this work for complex observable graphs is topological rank. Topological rank in Airstream is defined as follows: if observable A _synchronously depends_ (see definition above) on observable B, its topological rank will be greater than that of B. In practical terms, `doubledNumbers.topoRank = numbers.topoRank + 1` and `combinedStream.topoRank == max(isPositive.topoRank, doubledNumbers.topoRank) + 1`.

In case of `combineWith`, Airstream uses topological rank for one thing – do determine which of the pending observables to resolve first. So when I said that Airstream continues the propagation of the "first" pending observable, I meant the one with the lowest `topoRank` among pending observables. This ensures that if you have more than one combined observable pending, that the one that doesn't depend on the other one will be propagated first.

So this is how Airstream avoids the glitch in the diamond-combine case.


#### Transactions

Before we dive into other kinds of glitches (ha! you thought that was it!?), we need to know what a Transaction is.

Philosophically, a Transaction in Airstream encapsulates a part of the propagation that 1) happens synchronously, and 2) contains no loops of observables. Within the confines of a single Transaction Airstream guarantees no glitches.

Async streams such as `stream.delay(500)` emit their events in a new transaction because Airstream executes transactions sequentially – and there is no sense in keeping other transactions blocked until some Promise or Future decides to resolve itself.

Events that come from outside of Airstream – see [Event Sources](#event-sources) – each come in in a new Transaction, and those source observables have a `topoRank` of 1. I guess it makes sense why `EventStream.periodic` would behave that way, but why wouldn't `EventBus` reuse the transaction of whatever event came in from one of its source streams?

And the answer is the limitation of our topological ranking approach: it does not work for loops of observables. A topoRank is a property of an observable, not of the event coming in. And an observable's topoRank is determined at its creation. EventBus on its creation has no sources, so its stream needs to emit its events in a new Transaction because there is no way to guarantee correct topological ranking to avoid glitches.

That said, in practice this is not a big deal because the events that an EventBus receives from different sources should be usually independent of each other because they are coming from different child components.

Apart from EventBus there is another way to create a loop – the `eventStream.flatten` method. And that one too, produces an event stream that emits all events in a new transaction, for all the same reason. TODO[Docs] Document flattening.

Loops necessarily break transactions as a tradeoff. Some other libraries do some kinds of dynamic topological sorting which is less predictable and whose performance worsens as your observables graph gets more complicated, but with Airstream there are no such costs. The only – and tiny – cost is when Airstream inserts a CombineObservable into the list of pending observables – that list is sorted by a static `topoRank` field, so it takes O(n) where n is the number of currently pending observables, which is usually zero or not much more than that.

Lastly, keep in mind that "propagation" does not include external observers. If you call `observable.addObserver`, the observer will create a new transaction for every event that it receives. This is because observers are generally for side effects. Part of those effects might be emitting events, but you don't want that to be affected by other propagations going on, which would happen if we reused a transactions for observers. Philosophically, Observers should not know what they're observing (and they can observe multiple things at a time).


#### Merge Glitch-By-Design

Consider this:

```scala
val numbers: EventStream[Int] = ???
val tens: EventStream[Boolean] = numbers.map(_ * 10)
val hundreds: EventStream[Int] = tens.map(_ * 10)
val multiples: EventStream[Int] = EventStream.merge(tens, hundreds)
multiples.addObserver(multiplesObserver)(owner)
```

Same deal – what do you expect `multiples` to emit when `numbers` emits 1? I admit, it's a loaded question because of how I named `multiples`. I expect it to emit 10, and then 100. 10 comes first not because magic, but because the stream `hundreds` _synchronously depends_ on `tens`. Or, more precisely, because its `topoRank` is higher. This behaviour is especially desirable when your events are effectively operations – you don't want the merged stream to swallow operations or to put them in the wrong order.

TODO[API] Consider ordering synchronous events by the order their streams are given to merge instead of topoRank.

In Airstream, MergeEventStream will not emit more than one event in the same Transaction, because a Transaction by its very definition is about propagating a single event (that it happens to sometimes be split into multiple branches e.g. `tens` and `hundreds` is irrelevant, it's still the same change propagating), whereas a merged stream is capable of creating new events out of thin air as shown here. So in this example `multiples` will emit 10 in the same transaction that `numbers` emitted 1 in, and it will then create a new transaction which will emit 100 when it gets its turn.

MergeEventStream uses the same pendingObservables mechanism as CombineObservable because both extend SyncObservable.


### Operators

Airstream supports standard observables operators like `map` / `filter` / etc. Some of the operators ones are available only on certain types of observables. For example, you currently can only `fold` an EventStream. This scarcity is sort of deliberate – we start out with the most basic / obvious operators and will expand into fancier ones as the need arises. However, some basic operators are also missing just because I didn't get to it yet (as opposed to by design), e.g. there is no operator to combine more than two observables. 

There is currently no centralized documentation on operators – they are well annotated in the source code, in various traits that extend `Observable`.


### Error Handling


TODO[API] Error handling is not yet implemented, but will be soon, it's a priority feature.



## Limitations

* Currently Airstream only runs on Scala.js because its primary intended use case is unidirectional dataflow architecture on the frontend. However, its design is very generic, and it is definitely possible to make Airstream work on JVM, but that is complicated by 1) JVM's multithreaded environment, 2) Airstream using efficient JS-specific data structures such as js.Array that do not exist on the JVM, and 3) me having limited time, and no personal need for Airstream on the JVM. Those are solvable.
* Airstream has no concept of observables "completing". If you would like to make a case for this feature, please file an issue on github.


## My Related Projects

- [Laminar](https://github.com/raquo/Laminar) – Efficient reactive UI library for Scala.js that uses Airstream
- [XStream.scala](https://github.com/raquo/XStream.scala) – Streaming library previously used by Laminar

Other building blocks of Laminar:

- [Scala DOM Types](https://github.com/raquo/scala-dom-types) – Type definitions that we use for all the HTML tags, attributes, properties, and styles
- [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) – Low-level Scala & Scala.js library for building and manipulating DOM trees
- [Scala DOM TestUtils](https://github.com/raquo/scala-dom-testutils) – Test that your Javascript DOM nodes match your expectations



## Author

Nikita Gazarov – [raquo.com](http://raquo.com)



## License

Airstream is provided under the [MIT license](https://github.com/raquo/Laminar/blob/master/LICENSE.md).
