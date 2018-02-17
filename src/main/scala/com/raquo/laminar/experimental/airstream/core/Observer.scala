package com.raquo.laminar.experimental.airstream.core

import org.scalajs.dom

import scala.scalajs.js

trait Observer[-A] {

  def onNext(nextValue: A): Unit

  // @TODO this needs to be implemented, and probably with an implicit owner, just like WriteBus

  // @TODO are map and filter good names? Maybe use lens/zoom/etc?

  /** Creates another Observer such that calling its onNext will call this observer's onNext
    * with the value processed by the `project` function.
    *
    * This is useful when you need to pass down an Observer[A] to a child component
    * which should not know anything about the type A, but both child and parent know
    * about type `B`, and the parent knows how to translate B into A.
    */
  def map[B](project: B => A): Observer[B]

  // @TODO [B <: A], really?
  /** Creates another Observer such that calling its onNext will call this observer's onNext
    * with the same value, but only if it passes the test.
    */
  def filter[B <: A](passes: B => Boolean): Observer[B]

  // @TODO add "collect" method
}

object Observer {

  def apply[A](onNext: A => Unit): Observer[A] = {
    val onNextParam = onNext // It's beautiful on the outside
    new Observer[A] {

      override def onNext(nextValue: A): Unit = {
        dom.console.log(s"===== Observer(${hashCode()}).onNext", nextValue.asInstanceOf[js.Any])
        onNextParam(nextValue)
      }

      override def map[B](project: B => A): Observer[B] = {
        Observer(nextValue => onNextParam(project(nextValue)))
      }

      override def filter[B <: A](passes: B => Boolean): Observer[B] = {
        Observer(nextValue => {
          if (passes(nextValue)) {
            onNextParam(nextValue)
          }
        })
      }
    }
  }
}
