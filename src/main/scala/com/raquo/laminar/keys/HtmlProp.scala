package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.{SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base

/**
  * This class represents a DOM Element Property. Meaning the key that can be set, not a key-value pair.
  *
  * Note: following the Javascript DOM Spec, Properties are distinct from Attributes even when they share a name.
  *
  * @tparam V type of values that this Property can be set to
  */
class HtmlProp[V](
  override val name: String,
  val reflectedAttrName: Option[String],
  val codec: Codec[V, _]
) extends SimpleKey[HtmlProp[V], V, ReactiveHtmlElement.Base] {

  override def :=(value: V): SimpleKeySetter[HtmlProp[V], V, ReactiveHtmlElement.Base] =
    SimpleKeySetter(this, value)(DomApi.setHtmlProperty)

  override def <--(values: Source[V]): SimpleKeyUpdater[HtmlProp[V], V, Base] = {
    val update = if (name == "value") {
      (element: ReactiveHtmlElement.Base, nextValue: V) =>
        // Deduplicating updates against current DOM value prevents cursor position reset in Safari https://github.com/raquo/Laminar/issues/110
        // #nc[Test] Verify that I didn't break this in Safari (I switched to non-raw DOM API). Also verify that Safari still needs this.
        if (!DomApi.getHtmlProperty(element, this).contains(nextValue)) {
          DomApi.setHtmlProperty(element, this, nextValue)
        }
    } else {
      (element: ReactiveHtmlElement.Base, nextValue: V) =>
        DomApi.setHtmlProperty(element, this, nextValue)
    }
    new SimpleKeyUpdater[HtmlProp[V], V, ReactiveHtmlElement.Base](
      key = this,
      values = values.toObservable,
      update = update
    )
  }
}
