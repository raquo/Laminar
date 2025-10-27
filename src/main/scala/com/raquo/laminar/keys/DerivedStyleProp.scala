package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.SimpleKeySetter.DerivedStyleSetter
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base

/** This class represents derived style props like `height.px` or `backgroundImage.url` */
class DerivedStyleProp[V](
  val key: StyleProp[_],
  val encode: V => String
) extends SimpleKey[DerivedStyleProp[V], V, ReactiveHtmlElement.Base] {

  override val name: String = key.name

  override def :=(value: V): DerivedStyleSetter[V] = {
    // More specific return type that offers with .cssValue: String
    // I think the concrete Type[V] also helps us avoid problems with existential types in Scala 3.
    new DerivedStyleSetter(key = this, value)
  }

  override def apply(value: V): DerivedStyleSetter[V] =
    this := value // Same impl. as super, just more specific return type

  override def <--(values: Source[V]): SimpleKeyUpdater[DerivedStyleProp[V], V, Base] =
    new SimpleKeyUpdater[DerivedStyleProp[V], V, ReactiveHtmlElement.Base](
      key = this,
      values = values.toObservable,
      update = (el, value) => {
        DomApi.setHtmlDerivedStyle(el, this, value)
      }
    )

}
