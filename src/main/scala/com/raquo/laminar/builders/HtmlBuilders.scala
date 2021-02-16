package com.raquo.laminar.builders

import com.raquo.domtypes.generic.builders._
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.api.StyleSetter
import com.raquo.laminar.keys._
import org.scalajs.dom
import org.scalajs.dom.Event

trait HtmlBuilders
  extends HtmlAttrBuilder[ReactiveHtmlAttr]
  with ReflectedHtmlAttrBuilder[ReactiveProp]
  with PropBuilder[ReactiveProp]
  with EventPropBuilder[ReactiveEventProp, dom.Event]
  with StyleBuilders[StyleSetter]
  with HtmlTagBuilder[HtmlTag, dom.html.Element]
{

  override protected def htmlAttr[V](key: String, codec: Codec[V, String]): ReactiveHtmlAttr[V] = {
    new ReactiveHtmlAttr(key, codec)
  }

  override protected def reflectedAttr[V, DomPropV](
    attrKey: String,
    propKey: String,
    attrCodec: Codec[V, String],
    propCodec: Codec[V, DomPropV]
  ): ReactiveProp[V, DomPropV] = {
    new ReactiveProp(propKey, propCodec)
  }

  override protected def prop[V, DomV](key: String, codec: Codec[V, DomV]): ReactiveProp[V, DomV] = {
    new ReactiveProp(key, codec)
  }

  override protected def eventProp[Ev <: Event](key: String): ReactiveEventProp[Ev] = {
    new ReactiveEventProp(key)
  }

  override protected def style[V](key: String): Style[V] = {
    new Style(name = key)
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

  override protected def htmlTag[Ref <: dom.html.Element](tagName: String, void: Boolean): HtmlTag[Ref] = {
    new HtmlTag[Ref](tagName, void)
  }

  /** Create custom HTML attr (Note: for SVG attrs, use L.svg.customSvgAttr)
    *
    * @param key   - name of the attribute, e.g. "value"
    * @param codec - used to translate V <-> String, e.g. StringAsIsCodec,
    *
    * @tparam V    - value type for this attr in Scala
    */
  @inline def customHtmlAttr[V](key: String, codec: Codec[V, String]): ReactiveHtmlAttr[V] = htmlAttr(key, codec)

  /** Create custom HTML element property
    *
    * @param key   - name of the prop in JS, e.g. "value"
    * @param codec - used to translate V <-> DomV, e.g. StringAsIsCodec,
    *
    * @tparam V    - value type for this prop in Scala
    * @tparam DomV - value type for this prop in the underlying JS DOM.
    */
  @inline def customProp[V, DomV](key: String, codec: Codec[V, DomV]): ReactiveProp[V, DomV] = prop(key, codec)

  /** Create custom event property
    *
    * @param key - event type in JS, e.g. "click"
    *
    * @tparam Ev - event type in JS, e.g. js.dom.MouseEvent
    */
  @inline def customEventProp[Ev <: Event](key: String): ReactiveEventProp[Ev] = eventProp(key)

  /** Create custom CSS property
    *
    * @param key - name of CSS property, e.g. "font-weight"
    *
    * @tparam V  - type of values recognized by JS for this property, e.g. Int
    *              Note: String is always allowed regardless of the type you put here.
    *              If unsure, use String type as V.
    */
  @inline def customStyle[V](key: String): ReactiveStyle[V] = new ReactiveStyle(style(key))

  /** Note: this simply creates an instance of HtmlTag.
    * - This does not create the element (to do that, call .apply on the returned tag instance)
    * - This does not register this tag name as a custom element
    *   - See https://developer.mozilla.org/en-US/docs/Web/Web_Components/Using_custom_elements
    *
    * @param tagName - e.g. "div" or "mwc-input"
    *
    * @tparam Ref    - type of elements with this tag, e.g. dom.html.Input for "input" tag
    */
  @inline def customHtmlTag[Ref <: dom.html.Element](tagName: String): HtmlTag[Ref] = htmlTag(tagName, void = false)
}
