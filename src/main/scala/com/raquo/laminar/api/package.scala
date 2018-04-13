package com.raquo.laminar

import com.raquo.dombuilder.generic.nodes.Element
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Attr
import com.raquo.laminar.implicits.Implicits
import com.raquo.laminar.nodes.ReactiveNode
import org.scalajs.dom

package object api extends Implicits {

  private[api] type ReflectedAttr[V, DomV] = Attr[V]

  private[api] type StyleSetter = Modifier[ReactiveNode with Element[ReactiveNode, dom.html.Element, dom.Node]]

  val L: Laminar.type = Laminar
}
