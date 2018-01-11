package com.raquo.laminar.experimental.airstream.eventbus

import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.experimental.airstream.eventstream.EventStream

import scala.scalajs.js

class WriteBusStream[A](writeBus: WriteBus[A]) extends EventStream[A] {

  private[eventbus] var isStarted = false

  private[eventbus] val sources: js.Array[WriteBusSource[A]] = js.Array()

  @inline private[eventbus] def addSource(source: WriteBusSource[A]): Unit = {
    sources.push(source)
    if (isStarted) {
      source.sourceStream.addInternalObserver(writeBus)
    }
  }

  private[eventbus] def removeSource(source: WriteBusSource[A]): Unit = {
    val index = sources.indexOf(source)
    if (index != -1) {
      sources.splice(index, deleteCount = 1)
      if (isStarted) {
        source.sourceStream.removeInternalObserver(writeBus)
      }
    }
  }

  private[eventbus] def fireBus(nextValue: A): Unit = {
    super.fire(nextValue)
  }

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    // Ask every WriteBusSource, essentially
    sources.exists { source =>
      val sourceStream = source.sourceStream
      seenObservables.push(sourceStream)
      if (sourceStream == otherObservable) {
        true
      } else {
        sourceStream.syncDependsOn(otherObservable, seenObservables)
      }
    }
  }

  override protected[this] def onStart(): Unit = {
    isStarted = true
    sources.foreach(_.sourceStream.addInternalObserver(writeBus))
  }

  override protected[this] def onStop(): Unit = {
    isStarted = false
    sources.foreach(_.sourceStream.removeInternalObserver(writeBus))
  }
}
