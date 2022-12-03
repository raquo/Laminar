package com.raquo.laminar.defs.styles.units

import com.raquo.laminar.keys.DerivedStyleBuilder

trait Length[DSP[_], Num] extends Calc[DSP] { this: DerivedStyleBuilder[_, DSP] =>

  // -- Most common units --

  /** Pixels */
  lazy val px: DSP[Num] = derivedStyle(n => s"${n}px")

  /** 1 pt is 4/3 of a pixel. */
  lazy val pt: DSP[Num] = derivedStyle(n => s"${n}pt")

  lazy val percent: DSP[Num] = derivedStyle(n => s"${n}%")



  // -- Font-relative lengths --

  /**
   * `em` represents the calculated font-size of the element. If used on the
   * font-size property itself, it represents the inherited font-size of the
   * element.
   */
  lazy val em: DSP[Num] = derivedStyle(n => s"${n}em")

  /**
   * `rem` represents the font-size of the root element (typically <html>).
   * When used within the root element font-size, it represents its initial
   * value (a common browser default is 16px, but user-defined preferences
   * may modify this).
   */
  lazy val rem: DSP[Num] = derivedStyle(n => s"${n}rem")

  /**
   * `ch` represents the width, or more precisely the advance measure, of the
   * glyph "0" (zero, the Unicode character U+0030) in the element's font.
   */
  lazy val ch: DSP[Num] = derivedStyle(n => s"${n}ch")



  // -- Viewport-percentage lengths --

  /** 1 vh is equal to 1% of the height of the viewport's initial containing block */
  lazy val vh: DSP[Num] = derivedStyle(n => s"${n}vh")

  /** 1 vw is equal to 1% of the width of the viewport's initial containing block */
  lazy val vw: DSP[Num] = derivedStyle(n => s"${n}vw")

  /** 1 vmax is equal to the larger of 1 vw and 1 vh */
  lazy val vmax: DSP[Num] = derivedStyle(n => s"${n}vmax")

  /** 1 vmin is equal to the smaller of 1 vw and 1 vh */
  lazy val vmin: DSP[Num] = derivedStyle(n => s"${n}vmin")
}
