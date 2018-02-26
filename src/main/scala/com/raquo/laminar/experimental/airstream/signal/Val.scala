package com.raquo.laminar.experimental.airstream.signal

class Val[A](value: A) extends Signal[A] {

  override protected[airstream] val topoRank: Int = 1

  override protected[this] def initialValue(): A = value
}

object Val {

  def apply[A](value: A): Val[A] = new Val(value)
}
