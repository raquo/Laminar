package com.raquo.laminar.subscriptions

import com.raquo.laminar.RNode
import com.raquo.xstream.Subscription

class DynamicEventSubscription[T](
  val subscription: Subscription[T, Nothing]
) extends AnyVal {

  // @TODO[Integrity] Find a safer solution that is also zero-runtime-cost
  def dynamicListener: DynamicEventListener[T] = {
    subscription.listener.asInstanceOf[DynamicEventListener[T]]
  }

  /** Remove subscription from old node and add it to new node without re-subscribing
    *
    * In case of memory streams, this method is different from re-subscription
    * in that the listener does not get invoked with the latest value saved in
    * the memory stream, which normally does happen when you subscribe to a
    * memory stream.
    *
    * So the use case of this method is limited to nodes that were already patched or
    * even created by the listener, as is the case with ChildReceiver subscription.
    *
    * Note: we need to remove subscription from old node here because otherwise this
    *       subscription will be unsubscribed from when the old node is removed.
    */
  def transfer(fromNode: RNode, toNode: RNode): Unit = {
    // @TODO Do we need the fromNode param in this method? We could just look at old activeNode?
    val prevIndex = fromNode.data.subscriptions.indexOf(subscription)
    if (prevIndex != -1) {
      fromNode.data.subscriptions.splice(prevIndex, 1)
    }

    // @TODO[Performance] See if we can/should reuse this method for other receivers.
    if (!toNode.hasSubscription(this)) {
      dynamicListener.setActiveNode(toNode)
      toNode.data.subscriptions.push(subscription.asInstanceOf[Subscription[Any, Nothing]])
    }
  }
}
