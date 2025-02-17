package com.raquo.laminar.defs.styles.units

import com.raquo.laminar.keys.StyleBuilder

trait Calc[DSP[_]] { this: StyleBuilder[_, DSP] =>

  /** Wrap the provided expression in CSS calc() function.
   *
   * @see https://developer.mozilla.org/en-US/docs/Web/CSS/calc()
   */
  lazy val calc: DSP[String] = derivedStyle(exp => s"calc(${encodeCalcValue(exp)})")
}
