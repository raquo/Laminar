package com.raquo.laminar.defs

import com.raquo.domtypes.generic.builders.SvgAttrBuilder
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.generic.defs.complex.ComplexSvgKeys
import com.raquo.domtypes.generic.keys.SvgAttr
import com.raquo.laminar.keys.{CompositeKey, ReactiveSvgAttr}
import com.raquo.laminar.nodes.ReactiveSvgElement
import ReactiveComplexSvgKeys._
import com.raquo.laminar.DomApi

trait ReactiveComplexSvgKeys extends ComplexSvgKeys[
  CompositeSvgAttr[String]
] { this: SvgAttrBuilder[ReactiveSvgAttr] =>

  override lazy val className: CompositeSvgAttr[String] = stringCompositeSvgAttr("class", separator = " ")

  protected def stringCompositeSvgAttr(name: String, separator: String): CompositeSvgAttr[String] = {
    val attr = new SvgAttr(name, StringAsIsCodec, namespace = None)
    new CompositeKey(
      key = attr,
      getDomValue = el => {
        CompositeKey.normalize(DomApi.getSvgAttribute(el, attr).getOrElse(""), separator)
      },
      setDomValue = (el, value) => {
        DomApi.setSvgAttribute(el, attr, value.mkString(separator))
      },
      separator = separator
    )
  }
}

object ReactiveComplexSvgKeys {

  type CompositeSvgAttr[V] = CompositeKey[SvgAttr[V], ReactiveSvgElement.Base]
}
