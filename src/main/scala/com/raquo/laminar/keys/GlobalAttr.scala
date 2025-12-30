package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.nodes.ReactiveElement

/**
  * This class represents an Attribute that applies to any type of Element (HTML, SVG, MathML),
  * such as `id` and `tabindex`, and `data-*` attributes.
  *
  * Most attrs are more specific: [[HtmlAttr]], [[SvgAttr]], [[MathMlAttr]].
  *
  * [[AriaAttr]] is a subclass of [[GlobalAttr]].
  *
  * Laminar [[GlobalAttr]]-s are a subset of [[https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Global_attributes @ HTML global attrs]].
  * The latter are "global" within HTML only.
  *
  * [[GlobalAttr]] only includes attrs with simple values.
  * Composite global attrs (e.g. `class`, `role`) are defined as [[CompositeAttr]] instead.
  *
  * @tparam V type of values that this Attribute can be set to
  */
class GlobalAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends SimpleAttr[GlobalAttr[V], V, ReactiveElement.Base] {

  override lazy val maybe: GlobalAttr[Option[V]] = {
    new GlobalAttr[Option[V]](name, codec.optAsNull)
  }
}
