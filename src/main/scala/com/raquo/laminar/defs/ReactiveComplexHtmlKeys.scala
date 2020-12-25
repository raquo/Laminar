package com.raquo.laminar.defs

import com.raquo.domtypes.generic.builders.HtmlAttrBuilder
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.generic.defs.complex.ComplexHtmlKeys
import com.raquo.domtypes.generic.keys.{HtmlAttr, Prop}
import com.raquo.laminar.keys.{CompositeKey, ReactiveHtmlAttr, ReactiveProp}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import ReactiveComplexHtmlKeys._
import com.raquo.laminar.DomApi

trait ReactiveComplexHtmlKeys extends ComplexHtmlKeys[
  CompositeProp[String],
  CompositeHtmlAttr[String],
  CompositeHtmlAttr[String],
  ReactiveHtmlAttr[String],
  ReactiveHtmlAttr[String],
  ReactiveProp[String, String]
] { this: HtmlAttrBuilder[ReactiveHtmlAttr] =>

  override lazy val className: CompositeProp[String] = stringCompositeProp("className", separator = ' ')

  override lazy val rel: CompositeHtmlAttr[String] = stringCompositeHtmlAttr("rel", separator = ' ')

  override lazy val role: CompositeHtmlAttr[String] = stringCompositeHtmlAttr("role", separator = ' ')

  override def dataAttr(suffix: String): ReactiveHtmlAttr[String] = stringHtmlAttr(s"data-$suffix")

  override lazy val styleAttr: ReactiveHtmlAttr[String] = stringHtmlAttr("style")

  protected def stringCompositeProp(name: String, separator: Char): CompositeProp[String] = {
    val prop = new Prop(name, StringAsIsCodec)
    new CompositeKey(
      key = prop,
      getDomValue = el => {
        CompositeKey.normalize(DomApi.getHtmlProperty(el, prop), separator)
      },
      setDomValue = (el, value) => {
        DomApi.setHtmlProperty(el, prop, value.mkString(separator.toString))
      },
      separator = separator: Char
    )
  }

  protected def stringCompositeHtmlAttr(name: String, separator: Char): CompositeHtmlAttr[String] = {
    val attr = new HtmlAttr(name, StringAsIsCodec)
    new CompositeKey(
      key = attr,
      getDomValue = el => {
        CompositeKey.normalize(DomApi.getHtmlAttribute(el, attr).getOrElse(""), separator)
      },
      setDomValue = (el, value) => {
        DomApi.setHtmlAttribute(el, attr, value.mkString(separator.toString))
      },
      separator = separator: Char
    )
  }
}

object ReactiveComplexHtmlKeys {

  type CompositeProp[V] = CompositeKey[Prop[V, V], ReactiveHtmlElement.Base]

  type CompositeHtmlAttr[V] = CompositeKey[HtmlAttr[V], ReactiveHtmlElement.Base]
}
