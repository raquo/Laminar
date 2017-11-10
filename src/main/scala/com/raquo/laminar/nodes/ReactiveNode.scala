package com.raquo.laminar.nodes

import com.raquo.laminar.emitter.EventBus.WriteBus
import com.raquo.xstream.{Listener, Subscription, XStream}

import scala.collection.mutable

trait ReactiveNode {

  // @TODO[API] Find a better data structure for this
  var maybeSubscriptions: Option[mutable.Buffer[Subscription[_]]] = None

  def subscribe[V](
    $value: XStream[V],
    onNext: V => Unit
  ): Unit = {
    val subscription = $value.subscribe[V](Listener(onNext))
    maybeSubscriptions match {
      case Some(subscriptions) =>
        subscriptions += subscription
      case None =>
        maybeSubscriptions = Some(mutable.Buffer(subscription))
    }
  }

  @inline def subscribeBus[V](
    $source: XStream[V],
    targetBus: WriteBus[V]
  ): Unit = {
    subscribe($value = $source, onNext = targetBus.sendNext)
  }

  def unsubscribeFromAll(): Unit = {
    maybeSubscriptions.foreach {
      subscriptions => subscriptions.foreach(_.unsubscribe())
    }
    maybeSubscriptions = None
  }
}
