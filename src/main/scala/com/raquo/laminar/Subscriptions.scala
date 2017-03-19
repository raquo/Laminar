package com.raquo.laminar

import com.raquo.laminar.exceptions.AttrSubscriptionException
import com.raquo.snabbdom.setters.Attr
import com.raquo.xstream.Subscription

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

@ScalaJSDefined
class Subscriptions(val node: RNode) extends js.Object {

  var attrs: js.UndefOr[js.Dictionary[Subscription[_, Nothing]]] = js.undefined
  var on: js.UndefOr[js.Dictionary[Subscription[_, Nothing]]] = js.undefined
  var props: js.UndefOr[js.Dictionary[Subscription[_, Nothing]]] = js.undefined
  var styles: js.UndefOr[js.Dictionary[Subscription[_, Nothing]]] = js.undefined

  def addAttrSubscription[V](
    attr: Attr[V, RNode, RNodeData],
    subscription: Subscription[V, Nothing]
  ): Unit = {
    if (attrs.isEmpty) {

      attrs = js.Dictionary[Subscription[_, Nothing]](attr.name -> subscription)
    } else {
      if (attrs.get.contains(attr.name)) {
        throw AttrSubscriptionException(
          node,
          attr,
          existingSubscription = attrs.get.apply(attr.name).asInstanceOf[Subscription[V, Nothing]],
          attemptedSubscription = subscription
        )
      } else {
        attrs.get.update(attr.name, subscription)
      }
    }
  }
}
