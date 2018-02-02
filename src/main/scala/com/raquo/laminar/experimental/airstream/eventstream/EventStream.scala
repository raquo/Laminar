package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.LazyObservable
import com.raquo.laminar.experimental.airstream.signal.{Signal, SignalFromEventStream}

trait EventStream[+A] extends LazyObservable[A, EventStream] {

  def mapTo[B](value: B): EventStream[B] = {
    new MapEventStream[A, B](this, _ => value)
  }

  def filter(passes: A => Boolean): EventStream[A] = {
    new FilterEventStream(this, passes)
  }

  def flatten[B](implicit ev: A <:< EventStream[B]): EventStream[B] = ???

  def toSignal[B >: A](initialValue: B): Signal[B] = {
    new SignalFromEventStream(parent = this, initialValue)
  }

  override def map[B](project: A => B): EventStream[B] = {
    new MapEventStream(this, project)
  }

  override def compose[B](operator: EventStream[A] => EventStream[B]): EventStream[B] = {
    operator(this)
  }

  override def combineWith[AA >: A, B](otherEventStream: EventStream[B]): CombineEventStream2[AA, B, (AA, B)] = {
    new CombineEventStream2(
      parent1 = this,
      parent2 = otherEventStream,
      combinator = (_, _)
    )
  }
}

object EventStream {

  def combine[A, B](
    stream1: EventStream[A],
    stream2: EventStream[B]
  ): EventStream[(A, B)] = {
    new CombineEventStream2[A, B, (A, B)](stream1, stream2, (_, _))
  }

  def merge[A](streams: EventStream[A]*): EventStream[A] = {
    new MergeEventStream[A](streams)
  }

  implicit def toTuple2Stream[A, B](stream: EventStream[(A, B)]): Tuple2EventStream[A, B] = {
    new Tuple2EventStream(stream)
  }
}
