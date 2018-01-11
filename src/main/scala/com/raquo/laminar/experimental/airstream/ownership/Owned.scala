package com.raquo.laminar.experimental.airstream.ownership

/** Represents a leaky resource such as a
  * [[com.raquo.laminar.experimental.airstream.signal.Signal]] or a
  * [[com.raquo.laminar.experimental.airstream.core.Subscription]]
  *
  * Due to circular references, such resources will leak memory if
  * not explicitly disabled. Well, unless the whole Signal/Stream graph that
  * a given [[Owned]] depends on becomes unreachable, which might never happen.
  *
  * So, such leaky resources can only be instantiated if they notify their [[Owner]]
  * about their existence. The owner is something that knows when these resources
  * are no longer needed. For example, an owner could be a UI component that knows
  * when it was unmounted / discarded. The component would then `kill`s the Signals
  * and Subscriptions that were defined to belong to it upon creation.
  *
  * You can implement your own Owner-s and Owned-s. The "leaks" in question don't
  * need to be memory leaks. It could be any sort of lifecycle management as long
  * as it can be mapped to this API.
  */
trait Owned {

  registerWithOwner()

  // @TODO[Elegance] This API is weird and not very useful (not really enforcing all ownership constraints). Reconsider.
  /** This method should call owner.own(this) for a particular owner. */
  protected[this] def registerWithOwner(): Unit

  // @TODO[API,Docs] Update access level and comment...
  /** This will be called by an [[Owner]] when this resource should be discarded. */
  private[airstream] def kill(): Unit
}
