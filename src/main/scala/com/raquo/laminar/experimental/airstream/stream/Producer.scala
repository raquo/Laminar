package com.raquo.laminar.experimental.airstream.stream

// @TODO Unused

class Producer[A] {

  // producer is a Subject with only one possible stream (...)
  // override these

  def onStart(stream: Stream[A]): Unit = ???

  def onStop(): Unit = ???

}
