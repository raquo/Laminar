package com.raquo.laminar.defs

import com.raquo.domtypes.generic.builders.SvgAttrBuilder
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.generic.defs.complex.ComplexSvgKeys
import com.raquo.domtypes.generic.keys.SvgAttr
import com.raquo.laminar.keys.{CompositeAttr, ReactiveSvgAttr}
import com.raquo.laminar.nodes.ReactiveSvgElement
import ReactiveComplexSvgKeys._

trait ReactiveComplexSvgKeys extends ComplexSvgKeys[
  CompositeSvgAttr[String]
] { this: SvgAttrBuilder[ReactiveSvgAttr] =>

  override lazy val className: CompositeSvgAttr[String] = new CompositeAttr(
    new SvgAttr("class", StringAsIsCodec, namespace = None),
    separator = ' '
  )
}

object ReactiveComplexSvgKeys {

  type CompositeSvgAttr[V] = CompositeAttr[SvgAttr[V], ReactiveSvgElement.Base]
}
