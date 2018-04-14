package com.raquo.laminar

import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

package object api extends Implicits {

  private[laminar] type StyleSetter = Modifier[ReactiveHtmlElement[dom.html.Element]]

  val L: Laminar.type = Laminar
}
