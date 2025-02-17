package com.raquo.laminar.defs.styles.units

import com.raquo.laminar.keys.StyleBuilder

/** @see https://developer.mozilla.org/en-US/docs/Web/CSS/time */
trait Time[DSP[_]] extends Calc[DSP] { this: StyleBuilder[_, DSP] =>

  /** Seconds */
  lazy val s: DSP[Double] = derivedStyle(n => s"${n}s")

  /** Milliseconds */
  lazy val ms: DSP[Double] = derivedStyle(n => s"${n}ms")
}
