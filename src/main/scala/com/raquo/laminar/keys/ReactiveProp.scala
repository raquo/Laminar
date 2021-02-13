package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.Prop
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, checked, optionToSetter, value}
import com.raquo.laminar.modifiers.{Binder, KeySetter, Setter}
import com.raquo.laminar.nodes.ReactiveElement

class ReactiveProp[V, DomV](
  override val name: String,
  override val codec: Codec[V, DomV]
) extends Prop[V, DomV](name, codec) {

  @inline def apply(value: V): Setter[HtmlElement] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[HtmlElement] = {
    value.map(v => this := v)
  }

  def :=(value: V): Setter[HtmlElement] = {
    new KeySetter[ReactiveProp[V, DomV], V, HtmlElement](this, value, DomApi.setHtmlProperty)
  }

  def <--($value: Observable[V]): Binder[HtmlElement] = {
    Binder { element =>
      if (this == value) {
        if (element.hasValueController) {
          throw new Exception(s"Can not add a `value <-- ???` binder to an element that already has a `value` controller: ${DomApi.debugNodeDescription(element.ref)}")
        } else {
          element.hasValueBinder = true
        }
      } else if (this == checked) {
        if (element.hasCheckedController) {
          throw new Exception(s"Can not add a `checked <-- ???` binder to an element that already has a `checked` controller: ${DomApi.debugNodeDescription(element.ref)}")
        } else {
          element.hasCheckedBinder = true
        }
      }
      ReactiveElement.bindFn(element, $value) { value =>
        DomApi.setHtmlProperty(element, this, value)
      }
    }
  }
}
