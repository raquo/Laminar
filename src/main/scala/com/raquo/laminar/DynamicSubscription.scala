package com.raquo.laminar

import com.raquo.xstream.{Listener, Subscription, XStream}

/** This class represents a subscription that can be turned on and off.
  * Note: creating such a dynamic subscription does not automatically activate it.
  *
  * This class is used by Laminar internally to suspend subscriptions when nodes
  * are unmounted, because in some circumstances it is possible to re-mount those
  * nodes, and we want to revive the subscriptions at that point.
  */
class DynamicSubscription[V](
  val stream: XStream[V],
  val listener: Listener[V]
) {

  private var maybeSubscription: Option[Subscription[V]] = None

  def isActive: Boolean = maybeSubscription.isDefined

  def activate(): Unit = {
    if (!isActive) {
      maybeSubscription = Some(stream.subscribe(listener))
    }
    // @TODO else, log warning?
    // @TODO Note: we do rely on this !isActive check in ReactiveNode.subscribe
  }

  def deactivate(): Unit = {
    maybeSubscription.foreach(_.unsubscribe())
    maybeSubscription = None
    // @TODO If already not active, throw warning?
  }
}
