package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.keyapi.{HtmlPropDomApi, RWDomKeyApi}
import com.raquo.laminar.nodes.ReactiveHtmlElement

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
  val reflectedAttrName: Option[String],
  val codec: Codec[V, DomV]
) extends SimpleKey[V, DomV, ReactiveHtmlElement.Base] {

  override type Self[VV] = HtmlProp[VV, DomV]

  override val domApi: RWDomKeyApi[Self, ReactiveHtmlElement.Base] = {
    HtmlPropDomApi.asInstanceOf[RWDomKeyApi[Self, ReactiveHtmlElement.Base]]
  } // #nc safe?

  // def :=(value: V): PropSetter[V, DomV] = {
  //   // new KeySetter[HtmlProp[V, DomV], V, DomV, HtmlElement](this, value, DomApi.setHtmlProperty)
  //   new PropSetter(this, value)
  // }

  // #nc BRING THIS VALUE SPECIAL CASE BACK BUT MOVE IT

  // override def <--(values: Source[V]): PropUpdater[V, DomV] = {
  //   // #nc can we move this to keyapi?
  //   val update = if (name == "value") {
  //     (element: HtmlElement, nextValue: V, reason: Modifier.Any) =>
  //       // Deduplicating updates against current DOM value prevents cursor position reset in Safari
  //       val nextDomValue = codec.encode(nextValue)
  //       if (!DomApi.getHtmlPropertyRaw(element.ref, name).contains(nextDomValue)) {
  //         DomApi.setHtmlPropertyRaw(element.ref, name, nextDomValue)
  //       }
  //   } else {
  //     (element: HtmlElement, nextValue: V, reason: Modifier.Any) =>
  //       DomApi.setHtmlProperty(element, this, nextValue)
  //   }
  //   new SimpleKeyUpdater[HtmlElement, HtmlProp[V, DomV], V](
  //     key = this,
  //     values = values.toObservable,
  //     update = update
  //   )
  // }

}

object HtmlProp {

  type Of[V] = HtmlProp[V, _]
}
