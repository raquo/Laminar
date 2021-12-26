package com.raquo.laminar.defs

import com.raquo.domtypes.generic
import com.raquo.domtypes.generic.builders.SvgAttrBuilder
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.laminar.DomApi
import com.raquo.laminar.defs.ComplexSvgKeys._
import com.raquo.laminar.keys.{CompositeKey, SvgAttr}
import com.raquo.laminar.nodes.ReactiveSvgElement

trait ComplexSvgKeys extends generic.defs.complex.ComplexSvgKeys[
  CompositeSvgAttr[String]
] { this: SvgAttrBuilder[SvgAttr] =>

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

object ComplexSvgKeys {

  type CompositeSvgAttr[V] = CompositeKey[SvgAttr[V], ReactiveSvgElement.Base]
}
