package com.raquo.laminar

import com.raquo.xstream.XStream

class SubscriptionRequest private (
  val $value: XStream[Any],
  val onNext: (Any, RNode) => Unit
)

object SubscriptionRequest {
  def apply[V]($value: XStream[V], onNext: (V, RNode) => Unit): SubscriptionRequest = {
    // @TODO[Integrity] See if we can avoid this cast
    new SubscriptionRequest($value, onNext.asInstanceOf[(Any, RNode) => Unit])
  }
}
