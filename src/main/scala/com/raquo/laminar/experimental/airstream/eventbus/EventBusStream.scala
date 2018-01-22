package com.raquo.laminar.experimental.airstream.eventbus

import com.raquo.laminar.experimental.airstream.core.{InternalObserver, Transaction}
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import org.scalajs.dom

import scala.scalajs.js

class EventBusStream[A](writeBus: WriteBus[A]) extends EventStream[A] with InternalObserver[A] {

  private[eventbus] var isStarted = false

  private[eventbus] val sources: js.Array[EventBusSource[A]] = js.Array()

  // @TODO document why. Basically event bus breaks the "static DAG" requirement for topo ranking
  override protected[airstream] val topoRank: Int = 1

  @inline private[eventbus] def addSource(source: EventBusSource[A]): Unit = {
    sources.push(source)
    if (isStarted) {
      source.sourceStream.addInternalObserver(this)
    }
  }

  private[eventbus] def removeSource(source: EventBusSource[A]): Unit = {
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
    println("NEW TRX from EventBusStream")
    new Transaction(fire(nextValue, _))
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
