package com.raquo.laminar.defs

import com.raquo.domtypes.generic.builders.SvgAttrBuilder
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.generic.defs.complex.ComplexSvgKeys
import com.raquo.domtypes.generic.keys.SvgAttr
import com.raquo.laminar.keys.{CompositeAttr, ReactiveSvgAttr}

trait ReactiveComplexSvgKeys extends ComplexSvgKeys[
  CompositeAttr[SvgAttr[String]]
] { this: SvgAttrBuilder[ReactiveSvgAttr] =>

  override lazy val className: CompositeAttr[SvgAttr[String]] = new CompositeAttr(
    new SvgAttr("class", StringAsIsCodec, namespace = None),
    separator = ' '
  )
}
