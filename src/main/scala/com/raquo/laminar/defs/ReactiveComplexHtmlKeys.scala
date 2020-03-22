package com.raquo.laminar.defs

import com.raquo.domtypes.generic.builders.{HtmlAttrBuilder, PropBuilder}
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.generic.defs.complex.ComplexHtmlKeys
import com.raquo.domtypes.generic.keys.HtmlAttr
import com.raquo.laminar.keys.{CompositeAttr, ReactiveHtmlAttr, ReactiveProp}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import ReactiveComplexHtmlKeys._

trait ReactiveComplexHtmlKeys extends ComplexHtmlKeys[
  CompositeHtmlAttr[String],
  CompositeHtmlAttr[String],
  CompositeHtmlAttr[String],
  ReactiveHtmlAttr[String],
  ReactiveHtmlAttr[String],
  ReactiveProp[String, String]
] { this: HtmlAttrBuilder[ReactiveHtmlAttr] with PropBuilder[ReactiveProp] =>

  override lazy val className: CompositeHtmlAttr[String] = new CompositeAttr(
    new HtmlAttr("class", StringAsIsCodec),
    separator = ' '
  )

  override lazy val rel: CompositeHtmlAttr[String] = new CompositeAttr(
    new HtmlAttr("rel", StringAsIsCodec),
    separator = ' '
  )

  override lazy val role: CompositeHtmlAttr[String] = new CompositeAttr(
    new HtmlAttr("role", StringAsIsCodec),
    separator = ' '
  )

  override def dataAttr(suffix: String): ReactiveHtmlAttr[String] = stringHtmlAttr(s"data-$suffix")

  override lazy val styleAttr: ReactiveHtmlAttr[String] = stringHtmlAttr("style")
}

object ReactiveComplexHtmlKeys {

  type CompositeHtmlAttr[V] = CompositeAttr[HtmlAttr[V], ReactiveHtmlElement.Base]
}
