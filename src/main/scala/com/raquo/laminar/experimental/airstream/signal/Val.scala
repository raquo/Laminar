package com.raquo.laminar.experimental.airstream.signal

class Val[A](value: A) extends Signal[A] {

  override protected var currentValue: A = value

  override protected[airstream] val topoRank: Int = 1
}
