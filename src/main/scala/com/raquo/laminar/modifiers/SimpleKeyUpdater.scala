package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.laminar.keys._
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveMathMlElement, ReactiveSvgElement}

import scala.scalajs.js.|

/**
  * A modifier that updates a key from a source, e.g. `value <-- valueStream`
  *
  * @param update `(element, newValue, reason) => ()`
  *               The reason is used for updating CompositeKey-s.
  */
class SimpleKeyUpdater[ //
  +K <: SimpleKey[_, _, El],
  V,
  -El <: ReactiveElement.Base
](
  val key: K,
  val values: Observable[V],
  val update: (El, V | Null) => Unit
) extends Binder[El] { self =>

  override def bind(element: El): DynamicSubscription = {
    element.onBoundKeyUpdater(key)
    ReactiveElement.bindFn(element, values) { value =>
      update(element, value)
    }
  }
}

object SimpleKeyUpdater {

  // #nc add tests to make sure we're getting precise types from `:=` and `<--`

  type HtmlPropUpdater[V] = SimpleKeyUpdater[HtmlProp[V], V, ReactiveHtmlElement.Base]

  type HtmlAttrUpdater[V] = SimpleKeyUpdater[HtmlAttr[V], V, ReactiveHtmlElement.Base]

  type SvgAttrUpdater[V] = SimpleKeyUpdater[SvgAttr[V], V, ReactiveSvgElement.Base]

  type MathMlAttrUpdater[V] = SimpleKeyUpdater[MathMlAttr[V], V, ReactiveMathMlElement]

  type GlobalAttrUpdater[V] = SimpleKeyUpdater[GlobalAttr[V], V, ReactiveElement.Base]

  type AriaAttrUpdater[V] = SimpleKeyUpdater[AriaAttr[V], V, ReactiveElement.Base]

  type StylePropUpdater[V] = SimpleKeyUpdater[StyleProp[V], V, ReactiveHtmlElement.Base]

  type DerivedStylePropUpdater[InputV] = SimpleKeyUpdater[DerivedStyleProp[InputV], InputV, ReactiveHtmlElement.Base]

}
