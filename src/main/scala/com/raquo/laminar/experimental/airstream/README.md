# Airstream


# TODO: THIS IS CRIMINALLY OUT OF DATE.

## This is only relevant to the very first implementation of Airstream which was rewritten almost from scratch.


## Ownership

Airstream is designed around the idea that end users should never need to manually kill Signals or Subscriptions to prevent memory leaks and unexpected behaviour. To achieve that, both Signal and Subscription extend Owned trait, so it is impossible to instantiate them without specifying an Owner.

Owner is typically specified implicitly when creating new Signals using signal.map, Signal.combine, or when creating a Subscription using observable.foreach.

Airstream's API is designed for a specific user interface development paradigm where you define a dynamic tree of self-contained UI Components which are also Airstream Owners. Therefore, Signals and Subscriptions created "inside" a Component will be owned by that Component (the Component should provide an implicit val of itself "inside" itself), so when the Component is discarded it also immediately and automatically discards the Signals and Subscriptions that it owns, so that the user doesn't have to do that manually.

You can surely use Airstream for other purposes as long as our idea of ownership is compatible with what you're doing.



## Signals

Signal is a reactive variable encapsulating a value that changes in time discretely. It's sort of like a stream with a guaranteed initial, but there is more to it:

* Signals are executed **exactly once** when one or more of their parents (Signals that they depend on) update, and only after all their parents have finished updating.
* Observers that listen to Signals are executed **exactly once** per Signal execution, **after** the signal propagation caused by the initial update has finished
* Unlike a stream, a Signal is Owned like a subscription (see below)


### Class hierarchy

* **trait Signal** - base trait with common Signal logic (e.g. children, standard propagation)
  * **Var** - signal that can be updated manually. Does not depend on other signals
  * **Val** - a constant signal, only ever emits its initial value. Does not depend on other signals
  * **trait ComputedSignal** - signal which derives its value from parent signal(s)
  * **MapSignal** - signal which derives its value from parent signal by applying a `project` function
  * **trait CombineSignal** - contains a special propagation rule for signals with multiple parents
    * **CombineSignal2** - concrete CombineSignal for combining exactly two signals into a signal of a tuple of their values



## Limitations

* Currently Airstream only runs on Scala.js because this is the intended use case. The main challenge in bringing it to the JVM would be the multi-threaded environment. We might also use JS-specific optimizations in the future, which could be painful to extract into a JS-only layer. Making Airstream work on the JVM is not a priority for me, so to be fair I didn't give any of that much thought.  
