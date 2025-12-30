package com.raquo.laminar.defs.complex

import com.raquo.laminar.keys


object ComplexSvgKeys {

  // #Note: Complex SVG keys moved into ComplexGlobalKeys as none of them are SVG-specific.

  @deprecated("CompositeSvgAttr was dropped in favor of keys.CompositeAttr.Base (CompositeAttr available on api.L.) (there are no SVG-specific composite attrs)", "18.0.0-M1")
  type CompositeSvgAttr = keys.CompositeAttr.Base
}
