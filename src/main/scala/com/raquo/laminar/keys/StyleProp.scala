package com.raquo.laminar.keys

import com.raquo.laminar.api.Implicits
import com.raquo.laminar.defs.styles.traits.GlobalKeywords
import com.raquo.laminar.domapi.keyapi.{DomKeyApi, StylePropDomApi}
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter
import com.raquo.laminar.nodes.ReactiveHtmlElement

class StyleProp[V](
  override val name: String,
  val prefixes: Seq[String] = Nil
)
extends SimpleKey[V, String, ReactiveHtmlElement.Base]
with GlobalKeywords[V]
with StyleBuilder[StyleSetter[String], DerivedStyleProp]
with Implicits.StyleImplicits {

  // @inline def asStringProp: StyleProp[String] = this

  override type Self[VV] = StyleProp[VV]

  override val domApi: DomKeyApi[StyleProp, ReactiveHtmlElement.Base] = StylePropDomApi

  // #nc special impl for the sake of existential type? Or... not sure...
  // override def :=(value: V): StyleSetter[V] = {
  //   // new KeySetter[StyleProp[V], V | String, String, HtmlElement](this, value.toString, DomApi.setHtmlAnyStyle)
  //   // new StyleSetter(this, value)
  //   ??? // #nc
  // }
  //
  // /** Source[V] and Source[String] are of course also accepted. */
  // def <--(values: Source[V | String]): StyleUpdater[V] = {
  //   new SimpleKeyUpdater[ReactiveHtmlElement.Base, StyleProp[V], V | String](
  //     key = this,
  //     values = values.toObservable,
  //     update = (el, v, _) => DomApi.setHtmlStyle(el, this, v)
  //   )
  // }

  /** Create a new key for this style with these prefixes */
  def withPrefixes(ps: (StyleVendorPrefixes.type => String)*): StyleProp[V] = {
    new StyleProp[V](name, ps.map(_(StyleVendorPrefixes)))
  }

  override protected def styleSetter(value: String): StyleSetter[String] = {
    this := value // .asInstanceOf[V] // #nc explain why safe... is it?
  }

  // override protected def valueAsString(value: StyleSetter[V]): String = {
  //   value.value
  // }

  override protected def derivedStyle[InputV](encode: InputV => String): DerivedStyleProp[InputV] = {
    new DerivedStyleProp[InputV](key = this, encode)
  }
}
