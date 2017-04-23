package com.raquo.laminar.nodes

import com.raquo.xstream.{Listener, Subscription, XStream}

import scala.collection.mutable

trait ReactiveNode {

  // @TODO[API] Find a better data structure for this
  var maybeSubscriptions: Option[mutable.Buffer[Subscription[_, Nothing]]] = None

  def subscribe[V](
    $value: XStream[V],
    onNext: V => Unit
  ): Unit = {
    val subscription = $value.subscribe[V, Nothing](Listener(onNext))
    maybeSubscriptions match {
      case Some(subscriptions) =>
        subscriptions += subscription
      case None =>
        maybeSubscriptions = Some(mutable.Buffer(subscription))
    }
  }
}
