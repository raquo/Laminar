package com.raquo.laminar.keys

import com.raquo.laminar.domapi.keyapi.{DerivedStylePropDomApi, DomKeyApi}
import com.raquo.laminar.modifiers.SimpleKeySetter.DerivedStyleSetter
import com.raquo.laminar.nodes.ReactiveHtmlElement

/** This class represents derived style props like `height.px` or `backgroundImage.url` */
class DerivedStyleProp[V](
  val key: StyleProp[_],
  val encode: V => String
) extends SimpleKey[V, String, ReactiveHtmlElement.Base] {

  override type Self[VV] = DerivedStyleProp[VV]

  override val domApi: DomKeyApi[DerivedStyleProp, ReactiveHtmlElement.Base] = DerivedStylePropDomApi

  override val name: String = key.name

  // #nc special impl for the sake of existential type? Or... not sure... I guess it provides .cssValue? Can't we have a generic .domValue?
  override def :=(value: V): DerivedStyleSetter[V] = {
    // new KeySetter[StyleProp[V], V | String, String, HtmlElement](this, value.toString, DomApi.setHtmlAnyStyle)
    new DerivedStyleSetter(key = this, value)
  }

  // #nc remove
  // override def :=(value: InputV): DerivedStyleSetter[InputV] = {
  //   new DerivedStyleSetter(key = this, value)
  // }
  //
  // override def <--(values: Source[InputV]): DerivedStyleUpdater[InputV] = {
  //   new SimpleKeyUpdater[ReactiveHtmlElement.Base, DerivedStyleProp[InputV], InputV](
  //     key = this,
  //     values = values.toObservable,
  //     update = (el, v, _) => DomApi.setHtmlStringStyle(el, key, encode(v))
  //   )
  // }
}
