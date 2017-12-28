package com.raquo.laminar.experimental.airstream.stream

import com.raquo.laminar.experimental.airstream.observation.{Observable, Observer, Subscription}
import com.raquo.laminar.experimental.airstream.ownership.Owner

// @TODO Unused

class Stream[+A] extends Observable[A] {

  def map[B](project: A => B): Stream[B] = ???

  def filter(passes: A => Boolean): Stream[A] = ???

  def flatten[B](implicit ev: A <:< Stream[B]): Stream[B] = ???

  def compose[B](operator: Stream[A] => Stream[B]): Stream[B] = {
    operator(this)
  }

  override def addObserver[B >: A](observer: Observer[B])(implicit subscriptionOwner: Owner): Subscription[B] = {
    super.addObserver(observer)
  }

}
