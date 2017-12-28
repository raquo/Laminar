package com.raquo.laminar.experimental.airstream

package object fixtures {

  case class Calculation[V](name: String, value: V)

  case class Effect[V](name: String, value: V)
}
