package com.raquo.laminar.api

import com.raquo.airstream

trait Airstream {

  type EventBus[A] = airstream.eventbus.EventBus[A]

  type EventBusSource[A] = airstream.eventbus.EventBusSource[A]

  type EventStream[+A] = airstream.eventstream.EventStream[A]

  type LazyObservable[+A] = airstream.core.LazyObservable[A]

  type MemoryObservable[+A] = airstream.core.MemoryObservable[A]

  type Observable[+A] = airstream.core.Observable[A]

  type Observer[-A] = airstream.core.Observer[A]

  type Signal[+A] = airstream.signal.Signal[A]

  type State[+A] = airstream.state.State[A]

  type StateVar[A] = airstream.state.StateVar[A]

  type Subscription = airstream.core.Subscription

  type Val[A] = airstream.signal.Val[A]

  type Var[A] = airstream.signal.Var[A]

  type WriteBus[A] = airstream.eventbus.WriteBus[A]


  val EventStream: airstream.eventstream.EventStream.type = airstream.eventstream.EventStream

  val Observer: airstream.core.Observer.type = airstream.core.Observer

  val StateVar: airstream.state.StateVar.type = airstream.state.StateVar

  val Val: airstream.signal.Val.type = airstream.signal.Val

  val Var: airstream.signal.Var.type = airstream.signal.Var
}
