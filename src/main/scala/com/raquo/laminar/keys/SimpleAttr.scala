package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.{SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

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

  /** We override this here and not in subclasses in order to provide it to a universal SimpleAttrDomApi below */
  // override type Self[VV] = SimpleAttr[VV, ReactiveElement.Base]

  val name: String

  @inline def localName: String = name

  val codec: Codec[V, String]

  val namespaceUri: Option[String] = None

  override def :=[ThisV <: V](value: ThisV): SimpleKeySetter[Self, ThisV, El] =
    SimpleKeySetter[Self, ThisV, El](this, value)(DomApi.setAttribute)

  override def <--[ThisV](
    values: Source[ThisV]
  )(implicit
    ev: ThisV => V
  ): SimpleKeyUpdater[Self, ThisV, El] =
    new SimpleKeyUpdater(
      key = self,
      values = values.toObservable,
      update = (el, value) => DomApi.setAttribute(el, self, ev(value))
    )
}

object SimpleAttr {

  type Of[V] = SimpleAttr[_, V, ReactiveElement.Base]
}
