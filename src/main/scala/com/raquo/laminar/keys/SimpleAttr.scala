package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.{SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

/** Common type of all simple (non-composite) attributes:
  *  - [[HtmlAttr]]
  *  - [[SvgAttr]]
  *  - [[AriaAttr]]
  *
  * See also attrs that are NOT simple:
  *  - [[CompositeAttr]]
  *    - [[CompositeHtmlAttr]]
  *    - [[CompositeSvgAttr]]
  */
trait SimpleAttr[+Self <: SimpleAttr[Self, V, El], V, -El <: ReactiveElement.Base]
extends SimpleKey[Self, V, El] { self: Self =>

  /** We override this here and not in subclasses in order to provide it to a universal SimpleAttrDomApi below */
  // override type Self[VV] = SimpleAttr[VV, ReactiveElement.Base]

  val name: String

  @inline def localName: String = name

  val codec: Codec[V, String]

  val namespaceUri: Option[String] = None

  override def :=(value: V): SimpleKeySetter[Self, V, El] =
    SimpleKeySetter[Self, V, El](this, value)(DomApi.setAttribute)

  override def <--(values: Source[V]): SimpleKeyUpdater[Self, V, El] =
    new SimpleKeyUpdater[Self, V, El](
      key = self,
      values = values.toObservable,
      update = (el, value) => {
        DomApi.setAttribute(el, self, value)
      }
    )
}

object SimpleAttr {

  type Of[V] = SimpleAttr[_, V, ReactiveElement.Base]
}
