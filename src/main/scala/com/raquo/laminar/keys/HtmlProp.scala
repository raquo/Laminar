package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToSetter}
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.modifiers.KeyUpdater.PropUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}

/**
  * This class represents a DOM Element Property. Meaning the key that can be set, not a key-value pair.
  *
  * Note: following the Javascript DOM Spec, Properties are distinct from Attributes even when they share a name.
  *
  * @tparam V type of values that this Property can be set to
  * @tparam DomV type of values that this Property holds in the native Javascript DOM
  */
class HtmlProp[V, DomV](
  override val name: String,
  val codec: Codec[V, DomV]
) extends Key { self =>

  @inline def apply(value: V): Setter[HtmlElement] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[HtmlElement] = {
    optionToSetter(value.map(v => this := v))
  }

  def :=(value: V): Setter[HtmlElement] = {
    new KeySetter[HtmlProp[V, DomV], V, HtmlElement](this, value, DomApi.setHtmlProperty)
  }

  def <--($value: Source[V]): PropUpdater[V, DomV] = {
    val update = if (name == "value") {
      (element: HtmlElement, nextValue: V) =>
        // Checking against current DOM value prevents cursor position reset in Safari
        val currentDomValue = DomApi.getHtmlProperty(element, this)
        if (nextValue != currentDomValue) {
          DomApi.setHtmlProperty(element, this, nextValue)
        }
    } else {
      (element: HtmlElement, nextValue: V) => {
        DomApi.setHtmlProperty(element, this, nextValue)
      }
    }
    new KeyUpdater[HtmlElement, HtmlProp[V, DomV], V](
      this,
      $value.toObservable,
      update
    )
  }

}
