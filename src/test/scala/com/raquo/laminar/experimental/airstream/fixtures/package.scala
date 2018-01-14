package com.raquo.laminar.experimental.airstream

import scala.collection.mutable

package object fixtures {

  case class Calculation[V](name: String, value: V)

  object Calculation {

    def log[V](name: String, to: mutable.Buffer[Calculation[V]])(value: V): V = {
      to += Calculation(name, value)
      value
    }
  }

  case class Effect[V](name: String, value: V)
}
