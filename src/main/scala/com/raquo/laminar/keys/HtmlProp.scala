package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.{SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveHtmlElement

/**
  * This class represents a DOM Element Property. Meaning the key that can be set, not a key-value pair.
  *
  * Note: following the Javascript DOM Spec, Properties are distinct from Attributes even when they share a name.
  *
  * @tparam V type of values that this Property can be set to
  */
abstract class HtmlProp[V](
  override val name: String,
  val reflectedAttrName: Option[String]
) extends SimpleKey[HtmlProp[V], V, ReactiveHtmlElement.Base] { self =>

  /** This prop's type in the native JS DOM */
  type DomV

  val codec: Codec[V, DomV]

  override def :=[ThisV <: V](value: ThisV): SimpleKeySetter[HtmlProp[V], ThisV, ReactiveHtmlElement.Base] =
    SimpleKeySetter(this, value)(DomApi.setHtmlProperty)

  override def <--[ThisV](values: Source[ThisV])(implicit ev: ThisV => V): SimpleKeyUpdater[HtmlProp[V], ThisV, ReactiveHtmlElement.Base] = {
    val update = if (name == "value") {
      (element: ReactiveHtmlElement.Base, nextValue: ThisV) =>
        // Deduplicating updates against current DOM value prevents cursor position reset in Safari https://github.com/raquo/Laminar/issues/110
        // #nc[Test] Verify that I didn't break this in Safari (I switched to non-raw DOM API). Also verify that Safari still needs this.
        val _nextValue = ev(nextValue)
        if (!DomApi.getHtmlProperty(element, this).contains(_nextValue)) {
          DomApi.setHtmlProperty(element, this, _nextValue)
        }
    } else {
      (element: ReactiveHtmlElement.Base, nextValue: ThisV) =>
        val _nextValue = ev(nextValue)
        DomApi.setHtmlProperty(element, this, _nextValue)
    }
    new SimpleKeyUpdater(
      key = this,
      values = values.toObservable,
      update = update
    )
  }

  override lazy val maybe: HtmlProp[Option[V]] = {
    new HtmlProp[Option[V]](name, reflectedAttrName) {
      override type DomV = self.DomV
      override val codec: Codec[Option[V], DomV] = self.codec.optAsNull
    }
  }
}
