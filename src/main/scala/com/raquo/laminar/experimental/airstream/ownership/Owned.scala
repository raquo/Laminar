package com.raquo.laminar.experimental.airstream.ownership

/** Represents a leaky resource such as a
  * [[com.raquo.laminar.experimental.airstream.state.State]] or a
  * [[com.raquo.laminar.experimental.airstream.core.Subscription]] or a
  * [[com.raquo.laminar.experimental.airstream.eventbus.EventBusSource]]
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

  protected[this] val owner: Owner

  // @TODO[Elegance] Try getting around this with Scala's early initializers https://stackoverflow.com/q/4712468/2601788
  /** You MUST call this method in the constructor of your [[Owned]] instance.
    * Note: We can't call init() here because [[owner]] is not initialized yet.
    */
  protected[this] def init(): Unit = {
    owner.own(this)
  }

  /** You can expose this method if your [[Owned]] resource can be killed manually */
  protected[this] def kill(): Unit = {
    onKilled()
    owner.onKilledExternally(this)
  }

  // @TODO[API] This method exists only due to permissions. Is there another way?
  @inline private[ownership] def onKilledByOwner(): Unit = onKilled()

  /** This method will be called when this [[Owned]] resource is killed, either manually or by the owner.
    * This is where you implement cleanup for your resource.
    */
  protected[this] def onKilled(): Unit
}
