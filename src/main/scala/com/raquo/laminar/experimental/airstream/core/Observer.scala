package com.raquo.laminar.experimental.airstream.core

import org.scalajs.dom

import scala.scalajs.js

trait Observer[-A] {

  def onNext(nextValue: A): Unit
}

object Observer {

  def apply[A](onNext: A => Unit): Observer[A] = {
    val onNextParam = onNext // It's beautiful on the outside
    new Observer[A] { // @TODO[Elegance] Make this a Single Abstract Method eventually (2.12) https://stackoverflow.com/a/4387368/2601788
      override def onNext(nextValue: A): Unit = {
        dom.console.log(s"===== Observer(${hashCode()}).onNext", nextValue.asInstanceOf[js.Any])
        onNextParam(nextValue)
      }
    }
  }
}
