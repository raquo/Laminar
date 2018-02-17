package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.LazyObservable
import com.raquo.laminar.experimental.airstream.signal.{FoldSignal, Signal, SignalFromEventStream}

trait EventStream[+A] extends LazyObservable[A] {

  override def map[B](project: A => B): EventStream[B] = {
    new MapEventStream(this, project)
  }

  def mapTo[B](value: B): EventStream[B] = {
    new MapEventStream[A, B](this, _ => value)
  }

  def filter(passes: A => Boolean): EventStream[A] = {
    new FilterEventStream(this, passes)
  }

  def delay(intervalMillis: Int = 0): EventStream[A] = {
    new DelayEventStream(parent = this, intervalMillis)
  }

  def throttle(intervalMillis: Int): EventStream[A] = {
    ThrottleEventStream(parent = this, intervalMillis)
  }

  def debounce(delayFromLastEventMillis: Int): EventStream[A] = {
    new DebounceEventStream(parent = this, delayFromLastEventMillis)
  }

  def fold[B](initialValue: B)(fn: (B, A) => B): Signal[B] = {
    new FoldSignal(parent = this, initialValue, fn)
  }

  def toSignal[B >: A](initialValue: B): Signal[B] = {
    new SignalFromEventStream(parent = this, initialValue)
  }

  def compose[B](operator: EventStream[A] => EventStream[B]): EventStream[B] = {
    operator(this)
  }

  def combineWith[AA >: A, B](otherEventStream: EventStream[B]): CombineEventStream2[AA, B, (AA, B)] = {
    new CombineEventStream2(
      parent1 = this,
      parent2 = otherEventStream,
      combinator = (_, _)
    )
  }
}

object EventStream {

  def fromSeq[A](events: Seq[A]): EventStream[A] = {
    new SeqEventStream[A](events)
  }

  @inline def combine[A, B](
    stream1: EventStream[A],
    stream2: EventStream[B]
  ): EventStream[(A, B)] = {
    stream1.combineWith(stream2)
  }

  def merge[A](streams: EventStream[A]*): EventStream[A] = {
    new MergeEventStream[A](streams)
  }

  implicit def toTuple2Stream[A, B](stream: EventStream[(A, B)]): Tuple2EventStream[A, B] = {
    new Tuple2EventStream(stream)
  }
}
