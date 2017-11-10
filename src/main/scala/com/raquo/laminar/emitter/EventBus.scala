package com.raquo.laminar.emitter

import com.raquo.laminar.emitter.EventBus.WriteBus
import com.raquo.xstream.{Listener, Producer, XStream}

import scala.collection.mutable

// @TODO[API] Not sure if this belongs in this package

/** Event encapsulates a producer into which you can send events.
  *
  * This has two common usages:
  * - Receive events from [[EventPropOps]] when using `onClick --> eventBus` syntax
  * - Shared event stream that is populated from multiple sources which are otherwise
  *   hard or inefficient to compose into a single stream. For instance, when dealing
  *   with streams of lists this is a simpler and better performing alternative to
  *   [[com.raquo.laminar.collection.ReactiveCollection]]. See laminar-examples repo.
  * */
class EventBus[A] extends WriteBus[A] {

  /** Stream of produced events */
  val $: XStream[A] = XStream.create(producer)

  /** A reference to this event bus that can not read produced events */
  val asWriteBus: WriteBus[A] = this
}

object EventBus {

  /** When using EventBus as a shared event stream */
  trait WriteBus[A] { self =>

    protected val sourceStreams: mutable.Set[XStream[A]] = mutable.Set()

    protected var maybeListener: Option[Listener[A]] = None

    protected val producer: Producer[A] = Producer(onStart, onStop)

    protected def onStart(listener: Listener[A]): Unit = {
      maybeListener = Some(listener)
      sourceStreams.foreach(_.addListener(listener))
    }

    protected def onStop(): Unit = {
      maybeListener.foreach(listener =>
        sourceStreams.foreach(_.removeListener(listener))
      )
      maybeListener = None
    }

    // @TODO[API] I don't think we need this sendNext, I think we can just have a streaming API
    def sendNext(event: A): Unit = {
      maybeListener.foreach(listener => listener.next(event))
    }

    // @TODO map / compose are pretty bad for memory management since they call addSource internally

    /** Create a new Event bus and set it as a source to this Event bus
      * @param project  converts new events from the newly created event bus into events for this event bus
      */
    def map[B](project: B => A): EventBus[B] = {
      val newBus = new EventBus[B]
      addSource(newBus.$.map(project))
      newBus
    }

    /** Create a new Event bus and set it as a source to this Event bus
      * @param operator  converts stream of events from the newly created event bus into a source stream for this event bus
      */
    def compose[B](operator: XStream[B] => XStream[A]): EventBus[B] = {
      val newBus = new EventBus[B]
      addSource(newBus.$.compose(operator))
      newBus
    }

    // @TODO[Naming] addSource has a different "add" semantic than addListener – maybe rename?
    def addSource(sourceStream: XStream[A]): Unit = {
      if (!sourceStreams.contains(sourceStream)) {
        sourceStreams.add(sourceStream)
        maybeListener.foreach(sourceStream.addListener)
      }
    }

    def removeSource(sourceStream: XStream[A]): Unit = {
      if (sourceStreams.contains(sourceStream)) {
        sourceStreams.remove(sourceStream)
        maybeListener.foreach(sourceStream.removeListener)
      }
    }
  }
}
