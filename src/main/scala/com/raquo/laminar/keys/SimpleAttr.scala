package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

/** Common type of all simple (non-composite) attributes:
  *  - [[GlobalAttr]]
  *    - [[AriaAttr]]
  *  - [[HtmlAttr]]
  *  - [[SvgAttr]]
  *  - [[MathMlAttr]]
  *
  * See also attrs that are NOT simple:
  *  - [[CompositeAttr]]
  */
trait SimpleAttr[+Self <: SimpleAttr[Self, V, El], V, -El <: ReactiveElement.Base]
extends SimpleKey[Self, V, El] { self: Self =>

  val name: String

  @inline def localName: String = name

  val codec: Codec[V, String]

  val namespaceUri: Option[String] = None

  override protected def set(el: El, value: V | Null): Unit = {
    DomApi.setAttribute(el, self, value)
  }
}

object SimpleAttr {

  type Of[V] = SimpleAttr[_, V, ReactiveElement.Base]
}
