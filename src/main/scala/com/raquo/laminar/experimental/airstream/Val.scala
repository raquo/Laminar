package com.raquo.laminar.experimental.airstream

class Val[A](value: A) extends Signal[A] {

  override protected var currentValue: A = value
}
