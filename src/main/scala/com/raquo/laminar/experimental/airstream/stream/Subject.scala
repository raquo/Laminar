package com.raquo.laminar.experimental.airstream.stream

import com.raquo.laminar.experimental.airstream.observation.{Observable, Observer}

// @TODO if this subject leaks, it will need an Owner

// @TODO Better name
// @TODO Is this specific to streams? If so, name it that
class Subject[A] extends Observable[A] with Observer[A] {

  override def onNext(nextValue: A): Unit = {
    ???
  }
}
