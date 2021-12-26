package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.laminar.keys.{Key, HtmlAttr, Prop, StyleProp, SvgAttr}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}

import scala.scalajs.js
import scala.scalajs.js.|

/** A modifier that updates a key from a source, e.g. `value <-- valueStream` */
class KeyUpdater[-El <: ReactiveElement.Base, +K <: Key, V] (
  val key: K,
  val $value: Observable[V],
  val update: (El, V) => Unit
) extends Binder[El] {

  override def bind(element: El): DynamicSubscription = {
    var lastSeenValue: js.UndefOr[V] = js.undefined
    ReactiveElement.bindFn(element, $value) { value =>
      if (!lastSeenValue.contains(value)) {
        lastSeenValue = value
        update(element, value)
      }
    }
  }
}

object KeyUpdater {

  type PropUpdater[V, DomV] = KeyUpdater[ReactiveHtmlElement.Base, Prop[V, DomV], V]

  type HtmlAttrUpdater[V] = KeyUpdater[ReactiveHtmlElement.Base, HtmlAttr[V], V]

  type SvgAttrUpdater[V] = KeyUpdater[ReactiveSvgElement.Base, SvgAttr[V], V]

  type StyleUpdater[V] = KeyUpdater[ReactiveHtmlElement.Base, StyleProp[V], V | String]

  type DerivedStyleUpdater[InputV, StyleV] = KeyUpdater[ReactiveHtmlElement.Base, StyleProp[StyleV], InputV]
}
