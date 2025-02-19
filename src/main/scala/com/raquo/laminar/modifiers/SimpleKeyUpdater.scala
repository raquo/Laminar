package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.laminar.keys._
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}

import scala.scalajs.js.|

/**
 * A modifier that updates a key from a source, e.g. `value <-- valueStream`
 *
 * @param update `(element, newValue, reason) => ()`
 *               The reason is used for updating CompositeKey-s.
 */
class SimpleKeyUpdater[-El <: ReactiveElement.Base, +K <: SimpleKey[V, _, El], V](
  val key: K,
  val values: Observable[V],
  val update: (El, V, Modifier.Any) => Unit
) extends Binder[El] { self =>

  override def bind(element: El): DynamicSubscription = {
    element.onBoundKeyUpdater(key)
    ReactiveElement.bindFn(element, values) { value =>
      update(element, value, self)
    }
  }
}

object SimpleKeyUpdater {

  type PropUpdater[V, DomV] = SimpleKeyUpdater[ReactiveHtmlElement.Base, HtmlProp[V, DomV], V]

  type HtmlAttrUpdater[V] = SimpleKeyUpdater[ReactiveHtmlElement.Base, HtmlAttr[V], V]

  type SvgAttrUpdater[V] = SimpleKeyUpdater[ReactiveSvgElement.Base, SvgAttr[V], V]

  type AriaAttrUpdater[V] = SimpleKeyUpdater[ReactiveElement.Base, AriaAttr[V], V]

  type StyleUpdater[V] = SimpleKeyUpdater[ReactiveHtmlElement.Base, StyleProp[V], V]

  type DerivedStyleUpdater[InputV] = SimpleKeyUpdater[ReactiveHtmlElement.Base, DerivedStyleProp[InputV], InputV]

  // class DerivedStyleUpdater[V, InputV](
  //   val key: StyleProp[V],
  //   val values: Observable[InputV],
  //   // val encode: InputV => V,
  //   val update: (ReactiveHtmlElement.Base, InputV, Modifier.Any) => Unit
  // ) extends Binder[ReactiveHtmlElement.Base] { self =>
  //
  //   override def bind(element: ReactiveHtmlElement.Base): DynamicSubscription = {
  //     element.onBoundKeyUpdater(key)
  //     ReactiveElement.bindFn(element, values) { value =>
  //       update(element, value, self)
  //     }
  //   }
  // }
}
