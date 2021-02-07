package com.raquo.laminar.api

import com.raquo.airstream

trait Airstream {

  // -- Core

  type EventStream[+A] = airstream.core.EventStream[A]

  type Signal[+A] = airstream.core.Signal[A]

  type Observable[+A] = airstream.core.Observable[A]

  type Observer[-A] = airstream.core.Observer[A]

  val EventStream: airstream.core.EventStream.type = airstream.core.EventStream

  val Signal: airstream.core.Signal.type = airstream.core.Signal

  val Observer: airstream.core.Observer.type = airstream.core.Observer

  val AirstreamError: airstream.core.AirstreamError.type = airstream.core.AirstreamError

  // -- Event Bus

  type EventBus[A] = airstream.eventbus.EventBus[A]

  type WriteBus[A] = airstream.eventbus.WriteBus[A]

  val EventBus: airstream.eventbus.EventBus.type = airstream.eventbus.EventBus

  val WriteBus: airstream.eventbus.WriteBus.type = airstream.eventbus.WriteBus

  // -- State

  type Val[A] = airstream.state.Val[A]

  type Var[A] = airstream.state.Var[A]

  type OwnedSignal[+A] = airstream.state.OwnedSignal[A]

  type StrictSignal[+A] = airstream.state.StrictSignal[A]

  val Val: airstream.state.Val.type = airstream.state.Val

  val Var: airstream.state.Var.type = airstream.state.Var

  // -- Ownership

  type Owner = airstream.ownership.Owner

  type Subscription = airstream.ownership.Subscription

  type DynamicOwner = airstream.ownership.DynamicOwner

  type DynamicSubscription = airstream.ownership.DynamicSubscription

  val DynamicSubscription: airstream.ownership.DynamicSubscription.type = airstream.ownership.DynamicSubscription

  // -- Flatten

  type FlattenStrategy[-Outer[+_] <: Observable[_], -Inner[_], Output[+_] <: Observable[_]] = airstream.flatten.FlattenStrategy[Outer, Inner, Output]

  lazy val SwitchStreamStrategy: airstream.flatten.FlattenStrategy.SwitchStreamStrategy.type = airstream.flatten.FlattenStrategy.SwitchStreamStrategy

  lazy val SwitchSignalStrategy: airstream.flatten.FlattenStrategy.SwitchSignalStrategy.type = airstream.flatten.FlattenStrategy.SwitchSignalStrategy

  lazy val SwitchFutureStrategy: airstream.flatten.FlattenStrategy.SwitchFutureStrategy.type = airstream.flatten.FlattenStrategy.SwitchFutureStrategy

  lazy val ConcurrentFutureStrategy: airstream.flatten.FlattenStrategy.ConcurrentFutureStrategy.type = airstream.flatten.FlattenStrategy.ConcurrentFutureStrategy

  lazy val OverwriteFutureStrategy: airstream.flatten.FlattenStrategy.OverwriteFutureStrategy.type = airstream.flatten.FlattenStrategy.OverwriteFutureStrategy

  // -- Utils

  type Ref[+A <: AnyRef] = airstream.util.Ref[A]

  lazy val Ref: airstream.util.Ref.type = airstream.util.Ref
}
