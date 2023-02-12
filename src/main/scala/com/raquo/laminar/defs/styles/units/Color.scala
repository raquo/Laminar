package com.raquo.laminar.defs.styles.units

import com.raquo.laminar.keys.DerivedStyleBuilder

// #TODO[API] Is it possible to remove the DSP type param from this trait?

trait Color[SS, DSP[_]] { this: DerivedStyleBuilder[SS, DSP] =>

  /** @param red   0..255
    * @param green 0..255
    * @param blue  0..255
    *
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/rgb
    */
  def rgb(red: Int, green: Int, blue: Int): SS =
    styleSetter(s"rgb($red, $green, $blue)")


  /** @param red   0..255
    * @param green 0..255
    * @param blue  0..255
    * @param alpha 0..1, indicating opacity (0 means transparent)
    *
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/rgb
    */
  def rgb(red: Int, green: Int, blue: Int, alpha: Double): SS = {
    val alphaStr = if (alpha < 0.999999999) s" / $alpha" else ""
    styleSetter(s"rgb($red $green $blue$alphaStr)")
  }


  /** @param red   0..255
    * @param green 0..255
    * @param blue  0..255
    * @param alpha 0..1, indicating opacity (0 means transparent)
    *
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/rgba
    */
  def rgba(red: Int, green: Int, blue: Int, alpha: Double): SS =
    styleSetter(s"rgba($red, $green, $blue, $alpha)")


  /** @param hue        0..360
    * @param saturation 0..100
    * @param lightness  0..100
    *
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/hsl
    */
  def hsl(hue: Double, saturation: Double, lightness: Double): SS =
    styleSetter(s"hsl($hue, $saturation%, $lightness%)")


  /** @param hue        0..360
    * @param saturation 0..100
    * @param lightness  0..100
    * @param alpha      0..1, indicating opacity (0 means transparent)
    *
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/hsl
    */
  def hsl(hue: Double, saturation: Double, lightness: Double, alpha: Double): SS = {
    val alphaStr = if (alpha < 0.999999999) s" / $alpha" else ""
    styleSetter(s"hsl($hue $saturation% $lightness%$alphaStr)")
  }


  /** @param hue        0..360
    * @param saturation 0..100
    * @param lightness  0..100
    * @param alpha      0..1, indicating opacity (0 means transparent)
    *
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/hsla
    */
  def hsla(hue: Double, saturation: Double, lightness: Double, alpha: Double): SS =
    styleSetter(s"hsla($hue, $saturation%, $lightness%, $alpha)")

}
