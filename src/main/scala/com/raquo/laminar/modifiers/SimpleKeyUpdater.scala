package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.laminar.PlatformSpecific.StringOr
import com.raquo.laminar.keys._
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}

/**
 * A modifier that updates a key from a source, e.g. `value <-- valueStream`
 *
 * @param update `(element, newValue, reason) => ()`
 *               The reason is used for updating CompositeKey-s.
 */
class SimpleKeyUpdater[+K <: SimpleKey[_, _, El], V, -El <: ReactiveElement.Base](
  val key: K,
  val values: Observable[V],
  val update: (El, V) => Unit
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

  type OfHtmlProp[V] = SimpleKeyUpdater[HtmlProp[V], V, ReactiveHtmlElement.Base]

  type OfHtmlAttr[V] = SimpleKeyUpdater[HtmlAttr[V], V, ReactiveHtmlElement.Base]

  type OfSvgAttr[V] = SimpleKeyUpdater[SvgAttr[V], V, ReactiveSvgElement.Base]

  type OfAriaAttr[V] = SimpleKeyUpdater[AriaAttr[V], V, ReactiveElement.Base]

  type OfStyleProp[V] = SimpleKeyUpdater[StyleProp[V], StringOr[V], ReactiveHtmlElement.Base]

  type OfDerivedStyleProp[InputV] = SimpleKeyUpdater[DerivedStyleProp[InputV], InputV, ReactiveHtmlElement.Base]

}
