package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{Observable, Observer, SyncObservable}

import scala.scalajs.js

class EventBus[A] extends EventStream[A] {

  private[this] var isStarted = false

  private[this] val sourceStreams: js.Array[EventStream[A]] = js.Array()

  lazy val toObserver: Observer[A] = Observer { nextValue =>
    if (isStarted) {
      fire(nextValue)
      SyncObservable.resolvePendingSyncObservables()
    }
  }

  def addSource(sourceStream: EventStream[A]): Unit = {
    sourceStreams.push(sourceStream)
  }

  def removeSource(sourceStream: EventStream[A]): Unit = {
    val index = sourceStreams.indexOf(sourceStream)
    if (index != -1) {
      sourceStreams.splice(index, deleteCount = 1)
    }
  }

  def hasSource(sourceStream: EventStream[A]): Boolean = {
    sourceStreams.indexOf(sourceStream) != -1
  }

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    if (sourceStreams.indexOf(otherObservable) != -1) {
      true
    } else {
      seenObservables.push(this)
      sourceStreams.exists(_.syncDependsOn(otherObservable, seenObservables))
    }
  }

  // @TODO This is almost the same as CombineStream2
  override protected[this] def onStart(): Unit = {
    isStarted = true
    sourceStreams.foreach(_.addInternalObserver(toObserver))
  }

  // @TODO This is almost the same as CombineStream2
  override protected[this] def onStop(): Unit = {
    isStarted = false
    sourceStreams.foreach(_.removeInternalObserver(toObserver))
  }
}
