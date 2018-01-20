package com.raquo.laminar.experimental.airstream.eventbus

import com.raquo.laminar.experimental.airstream.core.{InternalObserver, Observable, Transaction}
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import org.scalajs.dom

import scala.scalajs.js

class WriteBusStream[A](writeBus: WriteBus[A]) extends EventStream[A] with InternalObserver[A] {

  private[eventbus] var isStarted = false

  private[eventbus] val sources: js.Array[WriteBusSource[A]] = js.Array()

  @inline private[eventbus] def addSource(source: WriteBusSource[A]): Unit = {
    sources.push(source)
    if (isStarted) {
      source.sourceStream.addInternalObserver(this)
    }
  }

  private[eventbus] def removeSource(source: WriteBusSource[A]): Unit = {
    val index = sources.indexOf(source)
    if (index != -1) {
      sources.splice(index, deleteCount = 1)
      if (isStarted) {
        source.sourceStream.removeInternalObserver(this)
      }
    }
  }

  override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
    dom.console.log(s">>>>WBS.onNext($nextValue): isStarted=$isStarted")
    dom.console.log(sources)
    // Note: We're not checking isStarted here because if this stream wasn't started, it wouldn't have been
    // fired as an internal observer. WriteBus calls this method manually, so it checks .isStarted on its own.
    // @TODO ^^^^ We should document this contract in InternalObserver
    fire(nextValue, transaction)
  }

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    // @TODO Check seenObservables, ESPECIALLY here!!!!
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
    sources.foreach(_.sourceStream.addInternalObserver(this))
  }

  override protected[this] def onStop(): Unit = {
    isStarted = false
    sources.foreach(_.sourceStream.removeInternalObserver(this))
  }
}
