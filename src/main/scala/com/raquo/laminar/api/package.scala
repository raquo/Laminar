package com.raquo.laminar

import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.ReactiveHtmlElement

package object api extends Implicits {

  private[laminar] type StyleSetter = Modifier[ReactiveHtmlElement.Base]

  val A: Airstream = new Airstream {}

  val L: Laminar.type = Laminar
}
