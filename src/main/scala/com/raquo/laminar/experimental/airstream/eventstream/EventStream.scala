package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.LazyObservable

trait EventStream[+A] extends LazyObservable[A, EventStream] {

  // @TODO[API] Can we make SyncEventStream covariant in A and include that type in .softSync/.sync output?
  // @TODO[Performance] SyncEventStream should override .softSync and .sync to return `this`, I think

  def softSync(): EventStream[A] = {
    new SyncEventStream(this, isSoft = true)
  }

  def sync(): EventStream[A] = {
    new SyncEventStream(this, isSoft = false)
  }

  override def map[B](project: A => B): EventStream[B] = {
    new MapEventStream(this, project)
  }

  def filter(passes: A => Boolean): EventStream[A] = {
    new FilterEventStream(this, passes)
  }

  override def compose[B](operator: EventStream[A] => EventStream[B]): EventStream[B] = {
    operator(this)
  }

  def flatten[B](implicit ev: A <:< EventStream[B]): EventStream[B] = ???

  override def combineWith[AA >: A, B](otherEventStream: EventStream[B]): CombineEventStream2[AA, B] = {
    new CombineEventStream2(
      parent1 = this,
      parent2 = otherEventStream
    )
  }

//  def toSignal(initialValue: A): Signal[A]
}

object EventStream {

  def combine[A, B](
    stream1: EventStream[A],
    stream2: EventStream[B]
  ): EventStream[(A, B)] = {
    new CombineEventStream2(stream1, stream2)
  }
}
