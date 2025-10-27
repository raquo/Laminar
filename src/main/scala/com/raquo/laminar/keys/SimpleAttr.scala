package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.keyapi.{RWDomKeyApi, SimpleAttrDomApi}
import com.raquo.laminar.nodes.ReactiveElement

/** Common type of all simple (non-composite) attributes:
  *  - [[HtmlAttr]]
  *  - [[SvgAttr]]
  *  - [[AriaAttr]]
  *
  * See also attrs that are NOT simple:
  *  - [[CompositeHtmlAttr]]
  *  - [[CompositeSvgAttr]]
  */
trait SimpleAttr[V, -El <: ReactiveElement.Base]
extends SimpleKey[V, String, El] {

  /** We override this here and not in subclasses in order to provide it to a universal SimpleAttrDomApi below */
  override type Self[VV] = SimpleAttr.Of[VV]

  val name: String

  @inline def localName: String = name

  val codec: Codec[V, String]

  val namespaceUri: Option[String] = None

  override val domApi: RWDomKeyApi[Self, El] = SimpleAttrDomApi
}

object SimpleAttr {

  type Of[V] = SimpleAttr[V, ReactiveElement.Base]
}
