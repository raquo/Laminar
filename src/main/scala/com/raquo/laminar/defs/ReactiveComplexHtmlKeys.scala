package com.raquo.laminar.defs

import com.raquo.domtypes.generic.builders.{HtmlAttrBuilder, PropBuilder}
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.generic.defs.complex.ComplexHtmlKeys
import com.raquo.domtypes.generic.keys.HtmlAttr
import com.raquo.laminar.keys.{CompositeAttr, ReactiveHtmlAttr, ReactiveProp}

trait ReactiveComplexHtmlKeys extends ComplexHtmlKeys[
  CompositeAttr[HtmlAttr[String]],
  CompositeAttr[HtmlAttr[String]],
  CompositeAttr[HtmlAttr[String]],
  ReactiveHtmlAttr[String],
  ReactiveHtmlAttr[String],
  ReactiveProp[String, String]
] { this: HtmlAttrBuilder[ReactiveHtmlAttr] with PropBuilder[ReactiveProp] =>

  override lazy val className: CompositeAttr[HtmlAttr[String]] = new CompositeAttr(
    new HtmlAttr("class", StringAsIsCodec),
    separator = ' '
  )

  override lazy val rel: CompositeAttr[HtmlAttr[String]] = new CompositeAttr(
    new HtmlAttr("rel", StringAsIsCodec),
    separator = ' '
  )

  override lazy val role: CompositeAttr[HtmlAttr[String]] = new CompositeAttr(
    new HtmlAttr("role", StringAsIsCodec),
    separator = ' '
  )

  override def dataAttr(suffix: String): ReactiveHtmlAttr[String] = stringHtmlAttr(s"data-$suffix")

  override lazy val styleAttr: ReactiveHtmlAttr[String] = stringHtmlAttr("style")
}
