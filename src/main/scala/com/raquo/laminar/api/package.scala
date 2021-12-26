package com.raquo.laminar

package object api extends Implicits {

  val A: Airstream = new Airstream {}

  val L: Laminar.type = Laminar
}
