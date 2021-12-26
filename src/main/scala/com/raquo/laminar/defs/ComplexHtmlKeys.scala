package com.raquo.laminar.defs

import com.raquo.domtypes.generic
import com.raquo.domtypes.generic.builders.HtmlAttrBuilder
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.laminar.DomApi
import com.raquo.laminar.defs.ComplexHtmlKeys._
import com.raquo.laminar.keys.{CompositeKey, HtmlAttr, Prop}
import com.raquo.laminar.nodes.ReactiveHtmlElement

trait ComplexHtmlKeys extends generic.defs.complex.ComplexHtmlKeys[
  CompositeProp[String],
  CompositeHtmlAttr[String],
  CompositeHtmlAttr[String],
  HtmlAttr[String],
  HtmlAttr[String],
  Prop[String, String]
] { this: HtmlAttrBuilder[HtmlAttr] =>

  override lazy val className: CompositeProp[String] = stringCompositeProp("className", separator = " ")

  override lazy val rel: CompositeHtmlAttr[String] = stringCompositeHtmlAttr("rel", separator = " ")

  override lazy val role: CompositeHtmlAttr[String] = stringCompositeHtmlAttr("role", separator = " ")

  override def dataAttr(suffix: String): HtmlAttr[String] = stringHtmlAttr(s"data-$suffix")

  override lazy val styleAttr: HtmlAttr[String] = stringHtmlAttr("style")

  protected def stringCompositeProp(name: String, separator: String): CompositeProp[String] = {
    val prop = new Prop(name, StringAsIsCodec)
    new CompositeKey(
      key = prop,
      getDomValue = el => {
        CompositeKey.normalize(DomApi.getHtmlProperty(el, prop), separator)
      },
      setDomValue = (el, value) => {
        DomApi.setHtmlProperty(el, prop, value.mkString(separator))
      },
      separator = separator
    )
  }

  protected def stringCompositeHtmlAttr(name: String, separator: String): CompositeHtmlAttr[String] = {
    val attr = new HtmlAttr(name, StringAsIsCodec)
    new CompositeKey(
      key = attr,
      getDomValue = el => {
        CompositeKey.normalize(DomApi.getHtmlAttribute(el, attr).getOrElse(""), separator)
      },
      setDomValue = (el, value) => {
        DomApi.setHtmlAttribute(el, attr, value.mkString(separator.toString))
      },
      separator = separator
    )
  }
}

object ComplexHtmlKeys {

  type CompositeProp[V] = CompositeKey[Prop[V, V], ReactiveHtmlElement.Base]

  type CompositeHtmlAttr[V] = CompositeKey[HtmlAttr[V], ReactiveHtmlElement.Base]
}
