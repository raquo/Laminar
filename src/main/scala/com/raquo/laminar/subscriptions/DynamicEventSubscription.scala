package com.raquo.laminar.subscriptions

import com.raquo.xstream.Subscription

class DynamicEventSubscription[T](
  val subscription: Subscription[T, Nothing]
) extends AnyVal {
  // @TODO[Integrity] Find a better solution that is zero-runtime-cost
  def dynamicListener: DynamicEventListener[T] = subscription.listener.asInstanceOf[DynamicEventListener[T]]
}
