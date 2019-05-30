package com.raquo.laminar.builders

import com.raquo.domtypes.generic.builders._
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.api.StyleSetter
import com.raquo.laminar.keys._
import org.scalajs.dom
import org.scalajs.dom.Event

trait ReactiveHtmlBuilders
  extends HtmlAttrBuilder[ReactiveHtmlAttr]
  with ReflectedHtmlAttrBuilder[ReactiveReflectedProp]
  with PropBuilder[ReactiveProp]
  with EventPropBuilder[ReactiveEventProp, dom.Event]
  with StyleBuilders[StyleSetter]
  with HtmlTagBuilder[ReactiveHtmlTag, dom.html.Element]
{

  override protected def htmlAttr[V](key: String, codec: Codec[V, String]): ReactiveHtmlAttr[V] = {
    new ReactiveHtmlAttr(key, codec)
  }

  override protected def reflectedAttr[V, DomPropV](
    attrKey: String,
    propKey: String,
    attrCodec: Codec[V, String],
    propCodec: Codec[V, DomPropV]
  ): ReactiveReflectedProp[V, DomPropV] = {
    new ReactiveProp(propKey, propCodec)
  }

  override protected def prop[V, DomV](key: String, codec: Codec[V, DomV]): ReactiveProp[V, DomV] = {
    new ReactiveProp(key, codec)
  }

  override protected def eventProp[Ev <: Event](key: String): ReactiveEventProp[Ev] = {
    new ReactiveEventProp(key)
  }

  override protected def style[V](key: String, cssKey: String): Style[V] = {
    new Style(name = key, cssName = cssKey)
  }

  override protected def buildDoubleStyleSetter(style: Style[Double], value: Double): StyleSetter = {
    new ReactiveStyle[Double](style) := value
  }

  override protected def buildIntStyleSetter(style: Style[Int], value: Int): StyleSetter = {
    new ReactiveStyle[Int](style) := value
  }

  override protected def buildStringStyleSetter(style: Style[_], value: String): StyleSetter = {
    new ReactiveStyle(style) := value
  }

  override def htmlTag[Ref <: dom.html.Element](tagName: String, void: Boolean): ReactiveHtmlTag[Ref] = {
    new ReactiveHtmlTag[Ref](tagName, void)
  }

}
