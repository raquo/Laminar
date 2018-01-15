package com.raquo.laminar.experimental.airstream.eventbus

import com.raquo.laminar.experimental.airstream.core.{Observer, SyncObservable}
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.experimental.airstream.ownership.Owner

class WriteBus[A] extends Observer[A] { self =>

  private[eventbus] val stream: WriteBusStream[A] = new WriteBusStream(this)

  /** Note: to remove this source, call .removeSource() on the resulting WriteBusSource */
  def addSource(sourceStream: EventStream[A])(implicit owner: Owner): WriteBusSource[A] = {
    new WriteBusSource(stream, sourceStream, owner)
  }

  // @TODO better names for mapWriter / filterWriter, consider lens/zoom/etc.

  def mapWriter[B](project: B => A)(implicit owner: Owner): WriteBus[B] = {
    val mapBus = new WriteBus[B]
    addSource(mapBus.stream.map(project))(owner)
    mapBus
  }

  def filterWriter(passes: A => Boolean)(implicit owner: Owner): WriteBus[A] = {
    val filterBus = new WriteBus[A]
    addSource(filterBus.stream.filter(passes))(owner)
    filterBus
  }

  override def onNext(nextValue: A): Unit = {
    if (stream.isStarted) {
      stream.fireBus(nextValue)
      SyncObservable.resolvePendingSyncObservables()
    }
  }

  override def map[B](project: B => A): Observer[B] = {
    Observer(nextValue => self.onNext(project(nextValue)))
  }

  override def filter[B <: A](passes: B => Boolean): Observer[B] = {
    Observer(nextValue => if (passes(nextValue)) self.onNext(nextValue))
  }
}
