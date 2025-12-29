package com.raquo.laminar.defs.styles.units

import com.raquo.laminar.keys.DerivedStyleBuilder

/** Unit helpers that are available on all CSS props */
trait GlobalUnits[DSP[_]] { this: DerivedStyleBuilder[DSP] =>

  /** Reference a CSS var. [[https://developer.mozilla.org/en-US/docs/Web/CSS/var @ MDN]] */
  lazy val cssVar: DSP[String] = derivedStyle { s =>
    s"""var(${encodeVarName(s)})"""
  }
}
