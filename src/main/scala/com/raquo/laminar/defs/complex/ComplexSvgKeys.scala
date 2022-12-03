package com.raquo.laminar.defs.complex

import com.raquo.laminar.DomApi
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.defs.complex.ComplexSvgKeys._
import com.raquo.laminar.keys.{CompositeKey, SvgAttr}
import com.raquo.laminar.nodes.ReactiveSvgElement

trait ComplexSvgKeys {

  /**
   * This attribute is a list of the classes of the element.
   * Classes allow CSS and Javascript to select and access specific elements
   * via the class selectors or functions like the DOM method
   * document.getElementsByClassName
   */
  val className: CompositeSvgAttr[String] = stringCompositeSvgAttr("class", separator = " ")

  val cls: CompositeSvgAttr[String] = className

  lazy val role: CompositeSvgAttr[String] = stringCompositeSvgAttr("role", separator = " ")

  // --

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
