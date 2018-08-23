package com.raquo.laminar.builders

import com.raquo.domtypes.generic.builders.{EventPropBuilder, SvgAttrBuilder, SvgTagBuilder}
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.laminar.keys.{ReactiveEventProp, ReactiveSvgAttr}
import org.scalajs.dom
import org.scalajs.dom.Event

trait ReactiveSvgBuilders
  extends EventPropBuilder[ReactiveEventProp, dom.Event]
  with SvgAttrBuilder[ReactiveSvgAttr]
  with SvgTagBuilder[ReactiveSvgTag, dom.svg.Element] {

  override protected def eventProp[Ev <: Event](key: String): ReactiveEventProp[Ev] = {
    new ReactiveEventProp(key)
  }

  override protected def svgAttr[V](key: String, codec: Codec[V, String], namespace: Option[String]): ReactiveSvgAttr[V] = {
    new ReactiveSvgAttr(key, codec, namespace)
  }

  override def svgTag[Ref <: dom.svg.Element](tagName: String, void: Boolean): ReactiveSvgTag[Ref] = {
    new ReactiveSvgTag[Ref](tagName, void)
  }
}
