package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.dombuilder.generic.modifiers.Setter
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToModifier}

import scala.scalajs.js.|

// @TODO[API] idea for style syntax: display(_.inlineBlock) – where `_` could be coming from either display itself or some other object

/** Note: Unlike other reactive keys, this has to be a wrapping value class because Scala DOM Types
  *       instantiates its own Style objects. It is a known deficiency in its API, see
  *       https://github.com/raquo/scala-dom-types/issues/2
  */
class ReactiveStyle[V](val style: Style[V]) extends AnyVal {

  // @TODO[API] Should this accept V or V | String?
  @inline def apply(value: V): Modifier[HtmlElement] = {
    this := value
  }

  def maybe(value: Option[V | String]): Modifier[HtmlElement] = {
    value.map(v => this := v)
  }

  def :=(value: V | String): Modifier[HtmlElement] = {
    new Setter[Style[V], V | String, HtmlElement](style, value, DomApi.htmlElementApi.setAnyStyle)
  }

  def :=(value: String): Modifier[HtmlElement] = {
    new Setter[Style[V], String, HtmlElement](style, value, DomApi.htmlElementApi.setStringStyle)
  }

  def <--($value: Observable[V | String]): Modifier[HtmlElement] = {
    new Modifier[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.subscribe($value) { value =>
          DomApi.htmlElementApi.setAnyStyle(element, style, value)
        }
      }
    }
  }
}
