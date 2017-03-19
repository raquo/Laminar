package com.raquo.laminar.exceptions

import com.raquo.laminar.{RNode, RNodeData}
import com.raquo.snabbdom.setters.Attr
import com.raquo.xstream.Subscription

case class AttrSubscriptionException[V](
  rnode: RNode,
  attr: Attr[V, RNode, RNodeData],
  existingSubscription: Subscription[V, Nothing],
  attemptedSubscription: Subscription[V, Nothing]
)
  extends LaminarException(
    message = s"Subscription for attr `${attr.name}` already exists. An attribute can only be subscribed to one stream.",
    cause = None
  )
