package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.core.{LazyObservable, MemoryObservable}

trait Signal[+A] extends MemoryObservable[A] with LazyObservable[A, Signal] {

  override def map[B](project: A => B): Signal[B] = {
    new MapSignal(parent = this, project)
  }

  override def compose[B](operator: Signal[A] => Signal[B]): Signal[B] = {
    operator(this)
  }

  override def combineWith[AA >: A, B](otherSignal: Signal[B]): CombineSignal2[AA, B] = {
    new CombineSignal2(
      parent1 = this,
      parent2 = otherSignal
    )
  }
}

// @TODO all.map/etc should also return a MemoryStream... But how? CanBuildFrom..?
object Signal {

//  def combine[A, B](
//    signal1: Signal[A],
//    signal2: Signal[B]
//  )(
//    implicit combineSignalOwner: Owner
//  ): CombineSignal2[A, B] = {
//    new CombineSignal2(parent1 = signal1, parent2 = signal2, combineSignalOwner)
//  }

}
