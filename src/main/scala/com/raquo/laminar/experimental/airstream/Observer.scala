package com.raquo.laminar.experimental.airstream

// @TODO I don't think this needs context. Maybe we need to introduce Subscription that would link an Observer to a Signal?

case class Observer[-A](
  onNext: A => Unit,
  context: Context
)
