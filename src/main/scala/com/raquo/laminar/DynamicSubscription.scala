package com.raquo.laminar

import com.raquo.xstream.{Listener, XStream}

/** TODO With the new API this is kind of more of a lifecycle context than a subscription. Adjust name accordingly?
  * TODO[API] We should reconsider this API when we deal with subscription lifecycle management. These () => Unit are ugly AF
  *
  * This class represents a subscription that can be turned on and off.
  * Note: creating such a dynamic subscription does not automatically activate it.
  *
  * This class is used by Laminar internally to suspend subscriptions when nodes
  * are unmounted, because in some circumstances it is possible to re-mount those
  * nodes, and we want to revive the subscriptions at that point.
  */
class DynamicSubscription[V](
  val subscribe: () => Unit,
  val unsubscribe: () => Unit
) {

  def this (
    stream: XStream[V],
    listener: Listener[V]
  ) {
    this(
      subscribe = () => stream.addListener(listener),
      unsubscribe = () => stream.removeListener(listener)
    )
  }

  private[laminar] var isActive = false

  def activate(): Unit = {
    if (!isActive) {
      isActive = true
      subscribe()
    }
  }

  def deactivate(): Unit = {
    isActive = false
    unsubscribe()
    // @TODO If already not active, throw warning?
  }
}
