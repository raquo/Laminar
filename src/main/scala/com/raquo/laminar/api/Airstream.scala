package com.raquo.laminar.api

import com.raquo.airstream

trait Airstream {

  type EventBus[A] = airstream.eventbus.EventBus[A]

  type EventBusSource[A] = airstream.eventbus.EventBusSource[A]

  type EventStream[+A] = airstream.eventstream.EventStream[A]

  type FlattenStrategy[-Outer[+_] <: Observable[_], -Inner[_], Output[+_] <: LazyObservable[_]] = airstream.features.FlattenStrategy[Outer, Inner, Output]

  type LazyObservable[+A] = airstream.core.LazyObservable[A]

  type MemoryObservable[+A] = airstream.core.MemoryObservable[A]

  type Observable[+A] = airstream.core.Observable[A]

  type Observer[-A] = airstream.core.Observer[A]

  type Owned = airstream.ownership.Owned

  type Owner = airstream.ownership.Owner

  type Ref[+A <: AnyRef] = airstream.util.Ref[A]

  type Signal[+A] = airstream.signal.Signal[A]

  type State[+A] = airstream.state.State[A]

  type StateVar[A] = airstream.state.StateVar[A]

  type Subscription = airstream.core.Subscription

  type Val[A] = airstream.signal.Val[A]

  type Var[A] = airstream.signal.Var[A]

  type WriteBus[A] = airstream.eventbus.WriteBus[A]

  val AirstreamError: airstream.core.AirstreamError.type = airstream.core.AirstreamError

  val ConcurrentFutureStrategy: airstream.features.FlattenStrategy.ConcurrentFutureStrategy.type = airstream.features.FlattenStrategy.ConcurrentFutureStrategy

  val EventStream: airstream.eventstream.EventStream.type = airstream.eventstream.EventStream

  val Observer: airstream.core.Observer.type = airstream.core.Observer

  val OverwriteFutureStrategy: airstream.features.FlattenStrategy.OverwriteFutureStrategy.type = airstream.features.FlattenStrategy.OverwriteFutureStrategy

  lazy val Ref: airstream.util.Ref.type = airstream.util.Ref

  val StateVar: airstream.state.StateVar.type = airstream.state.StateVar

  val SwitchFutureStrategy: airstream.features.FlattenStrategy.SwitchFutureStrategy.type = airstream.features.FlattenStrategy.SwitchFutureStrategy

  val SwitchStreamStrategy: airstream.features.FlattenStrategy.SwitchStreamStrategy.type = airstream.features.FlattenStrategy.SwitchStreamStrategy

  val Val: airstream.signal.Val.type = airstream.signal.Val

  val Var: airstream.signal.Var.type = airstream.signal.Var
}
