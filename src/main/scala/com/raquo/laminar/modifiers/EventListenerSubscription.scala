package com.raquo.laminar.modifiers

import com.raquo.airstream.ownership._

// @TODO[Org] This isn't a Modifier, so should this be in this package?
private[laminar] class EventListenerSubscription(
  val listener: EventListener.Any,
  val subscription: DynamicSubscription
)
