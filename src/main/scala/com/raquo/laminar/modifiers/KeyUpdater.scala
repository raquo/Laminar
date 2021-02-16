package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.domtypes.generic.keys.{Key, Style}
import com.raquo.laminar.keys.{ReactiveHtmlAttr, ReactiveProp, ReactiveSvgAttr}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}

import scala.scalajs.js.|

/** A modifier that updates a key from a source, e.g. `value <-- valueStream` */
class KeyUpdater[-El <: ReactiveElement.Base, +K <: Key, V] (
  val key: K,
  val $value: Observable[V],
  val update: (El, V) => Unit
) extends Binder[El] {

  override def bind(element: El): DynamicSubscription = {
    ReactiveElement.bindFn(element, $value) { value =>
      update(element, value)
    }
  }
}

object KeyUpdater {

  type PropUpdater[V, DomV] = KeyUpdater[ReactiveHtmlElement.Base, ReactiveProp[V, DomV], V]

  type HtmlAttrUpdater[V] = KeyUpdater[ReactiveHtmlElement.Base, ReactiveHtmlAttr[V], V]

  type SvgAttrUpdater[V] = KeyUpdater[ReactiveSvgElement.Base, ReactiveSvgAttr[V], V]

  type StyleUpdater[V] = KeyUpdater[ReactiveHtmlElement.Base, Style[V], V | String]
}
