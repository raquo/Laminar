package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.core.Observable

import scala.scalajs.js

class Val[A](value: A) extends Signal[A] {

  override protected var currentValue: A = value

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    false
  }
}
