package com.raquo.laminar.streams

import com.raquo.xstream.{Listener, Producer}

/** WriteBus encapsulates a simple callback-based producer to make sure the end user doesn't try
  * to use the producer more than once (a Producer can only have one listener – the stream that was
  * created out of it).
  *
  * To *listen* to those events you need to an instance of [[EventBus]].
  */
trait WriteBus[A] {

  protected var maybeOutboundListener: Option[Listener[A]] = None

  protected[this] val producer: Producer[A] = Producer(onStart, onStop)

  protected[this] def onStart(listener: Listener[A]): Unit = {
    maybeOutboundListener = Some(listener)
  }

  protected[this] def onStop(): Unit = {
    maybeOutboundListener = None
  }

  /** This method is used for efficiency to avoid allocating an extra stream and an extra producer
    * for each registered event listener. In end user code, use [[MergeBus]] and [[MergeWriteBus]].
    */
  protected[laminar] def sendNext(value: A): Unit = {
    maybeOutboundListener.foreach(_.next(value))
  }
}
