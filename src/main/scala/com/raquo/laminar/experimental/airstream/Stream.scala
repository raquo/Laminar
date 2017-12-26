package com.raquo.laminar.experimental.airstream

// @TODO Unused

class Stream[+A] {

  def map[B](project: A => B): Stream[B] = ???

  def filter(passes: A => Boolean): Stream[Boolean] = ???

  def foreach[B >: A](action: B => Unit, context: Context): Unit = ???

}
