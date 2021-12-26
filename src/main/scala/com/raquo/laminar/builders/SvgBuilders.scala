package com.raquo.laminar.builders

import com.raquo.domtypes.generic.builders.{EventPropBuilder, SvgAttrBuilder, SvgTagBuilder}
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.laminar.keys.{EventProp, SvgAttr}
import com.raquo.laminar.tags.SvgTag
import org.scalajs.dom
import org.scalajs.dom.Event

trait SvgBuilders
  extends EventPropBuilder[EventProp, dom.Event]
  with SvgAttrBuilder[SvgAttr]
  with SvgTagBuilder[SvgTag, dom.svg.Element] {

  override protected def eventProp[Ev <: Event](key: String): EventProp[Ev] = {
    new EventProp(key)
  }

  override protected def svgAttr[V](
    key: String,
    codec: Codec[V, String],
    namespace: Option[String]
  ): SvgAttr[V] = {
    new SvgAttr(key, codec, namespace)
  }

  override def svgTag[Ref <: dom.svg.Element](tagName: String, void: Boolean): SvgTag[Ref] = {
    new SvgTag[Ref](tagName, void)
  }

  /** @param key - event type in JS, e.g. "click"
    *
    * @tparam Ev - event type in JS, e.g. js.dom.MouseEvent
    */
  @inline def customEventProp[Ev <: Event](key: String): EventProp[Ev] = eventProp(key)

  /** Create custom SVG attr (Note: for HTML attrs, use L.customHtmlAttr)
    *
    * @param key   - name of the attribute, e.g. "value"
    * @param codec - used to translate V <-> String, e.g. StringAsIsCodec,
    *
    * @tparam V    - value type for this attr in Scala
    */
  @inline def customSvgAttr[V](
    key: String,
    codec: Codec[V, String],
    namespace: Option[String] = None
  ): SvgAttr[V] = {
    svgAttr(key, codec, namespace)
  }

  /** Note: this simply creates an instance of SvgTag.
    * - This does not create the element (to do that, call .apply on the returned tag instance)
    *
    * @param tagName - e.g. "circle"
    *
    * @tparam Ref    - type of elements with this tag, e.g. dom.svg.Circle for "circle" tag
    */
  @inline def customSvgTag[Ref <: dom.svg.Element](tagName: String): SvgTag[Ref] = svgTag(tagName, void = false)
}
