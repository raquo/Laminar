package com.raquo.laminar.defs.styles.units

import com.raquo.laminar.keys.DerivedStyleBuilder

import scala.scalajs.js.|

trait Length[DSP[_]] extends Calc[DSP] { this: DerivedStyleBuilder[DSP] =>

  // -- Most common units --

  /** Pixels */
  lazy val px: DSP[Int | Double] = derivedStyle(n => s"${n}px")

  /** 1 pt is 4/3 of a pixel. */
  lazy val pt: DSP[Int | Double] = derivedStyle(n => s"${n}pt")

  lazy val percent: DSP[Int | Double] = derivedStyle(n => s"${n}%")



  // -- Font-relative lengths --

  /**
   * `em` represents the calculated font-size of the element. If used on the
   * font-size property itself, it represents the inherited font-size of the
   * element.
   */
  lazy val em: DSP[Int | Double] = derivedStyle(n => s"${n}em")

  /**
   * `rem` represents the font-size of the root element (typically <html>).
   * When used within the root element font-size, it represents its initial
   * value (a common browser default is 16px, but user-defined preferences
   * may modify this).
   */
  lazy val rem: DSP[Int | Double] = derivedStyle(n => s"${n}rem")

  /**
   * `ch` represents the width, or more precisely the advance measure, of the
   * glyph "0" (zero, the Unicode character U+0030) in the element's font.
   */
  lazy val ch: DSP[Int | Double] = derivedStyle(n => s"${n}ch")



  // -- Viewport-percentage lengths --

  /** 1 vh is equal to 1% of the height of the viewport's initial containing block */
  lazy val vh: DSP[Int | Double] = derivedStyle(n => s"${n}vh")

  /** 1 vw is equal to 1% of the width of the viewport's initial containing block */
  lazy val vw: DSP[Int | Double] = derivedStyle(n => s"${n}vw")

  /** 1 vmax is equal to the larger of 1 vw and 1 vh */
  lazy val vmax: DSP[Int | Double] = derivedStyle(n => s"${n}vmax")

  /** 1 vmin is equal to the smaller of 1 vw and 1 vh */
  lazy val vmin: DSP[Int | Double] = derivedStyle(n => s"${n}vmin")
}
