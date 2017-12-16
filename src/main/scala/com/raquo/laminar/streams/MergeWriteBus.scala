package com.raquo.laminar.streams

import com.raquo.xstream.{Listener, XStream}

import scala.collection.mutable

/** MergeWriteBus is essentially the equivalent of XStream.merge(stream1, stream2, ...) except for
  * when the list of streams you want to merge changes over time. You could achieve the same result
  * with e.g. streams of lists of streams of ... – but that's complicated and often inefficient.
  *
  * MergeWriteBus is lazy, similar to XStream streams – It only calls addListener/removeListener when
  * the resulting stream acquires its first listener or loses its last listener. That means that
  * calling addSource(stream1) is equivalent to creating a new stream out of stream1 rather than
  * adding a listener to it, in terms of memory management.
  *
  * You would normally use MergeWriteBus as an instance of [[MergeBus]] which exposes a stream of
  * events that MergeWriteBus receives
  *
  * TODO[Integrity] This needs a more advanced API to handle error propagation / completed streams. Probably call methods on maybeOutputListener manually.
  * TODO[Integrity] When a source stream completes, it should be removed from the list of streams
  * TODO[Integrity] When a source stream errs, should it err the produced stream?
  */
trait MergeWriteBus[A] extends WriteBus[A] {

  // TODO[Performance] Benchmark native JS data structures as well – js Arrays, ES6 sets maybe.
  protected[laminar] val sourceStreams: mutable.Set[XStream[A]] = mutable.Set()

  override protected[this] def onStart(listener: Listener[A]): Unit = {
    sourceStreams.foreach(_.addListener(listener))
    super.onStart(listener)
  }

  override protected[this] def onStop(): Unit = {
    maybeOutboundListener.foreach(listener =>
      sourceStreams.foreach(_.removeListener(listener))
    )
    super.onStop()
  }

  // @TODO API all methods below need automatic lifecycle management, I think.

  // TODO[Naming] addSource has a different "add" semantic than addListener – maybe rename?
  def addSource(sourceStream: XStream[A]): Unit = {
    if (sourceStreams.add(sourceStream)) {
      maybeOutboundListener.foreach(sourceStream.addListener)
    }
  }

  def removeSource(sourceStream: XStream[A]): Unit = {
    if (sourceStreams.remove(sourceStream)) {
      maybeOutboundListener.foreach(sourceStream.removeListener)
    }
  }

  /** Create a new MergeBus and add it as a source to this bus
    * @param project  converts events from the newly created event bus into events for this event bus
    */
  def map[B](project: B => A): MergeBus[B] = {
    val newBus = new MergeBus[B]
    addSource(newBus.$.map(project))
    newBus
  }

  // TODO[API] Add filter and collect methods

   /** Create a new MergeBus and set it as a source to thisbus
    * @param operator  converts stream of events exposed by the newly created event bus into a source stream for this event bus
    */
  def compose[B](operator: XStream[B] => XStream[A]): MergeBus[B] = {
    val newBus = new MergeBus[B]
    addSource(newBus.$.compose(operator))
    newBus
  }
}
