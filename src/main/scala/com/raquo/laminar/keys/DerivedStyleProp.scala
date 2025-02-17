package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.modifiers.SimpleKeySetter.DerivedStyleSetter
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.modifiers.SimpleKeyUpdater.DerivedStyleUpdater
import com.raquo.laminar.nodes.ReactiveHtmlElement

/** This class represents derived style props like `height.px` or `backgroundImage.url` */
class DerivedStyleProp[InputV](
  val key: StyleProp[_],
  val encode: InputV => String
) extends SimpleKey[InputV, String, ReactiveHtmlElement.Base] {

  override val name: String = key.name

  override def :=(value: InputV): DerivedStyleSetter[InputV] = {
    new DerivedStyleSetter(key = this, value)
  }

  override def <--(values: Source[InputV]): DerivedStyleUpdater[InputV] = {
    new SimpleKeyUpdater[ReactiveHtmlElement.Base, DerivedStyleProp[InputV], InputV](
      key = this,
      values = values.toObservable,
      update = (el, v, _) => DomApi.setHtmlStringStyle(el, key, encode(v))
    )
  }
}
