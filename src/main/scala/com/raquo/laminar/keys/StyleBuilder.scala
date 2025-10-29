package com.raquo.laminar.keys

/** This trait is extended by:
  *  - `class StyleProp[V] ... with StyleBuilder[V, StyleSetter[V, String]]`
  *      - def styleSetter returns `key := value` setters
  *  - `L.style` via trait StyleUnitsApi[String, StyleEncoder] extends StyleBuilder[String, String]`
  *      - def styleSetter returns raw string values
  *
  * Shared usage of def styleSetter via units:
  *    {{{
  *    trait units.Color {
  *      def rgb(red: Int, green: Int, blue: Int): SSS =
  *        styleSetter(s"rgb($red, $green, $blue)")
  *    }
  *    style.rgb(0, 0, 0)      // "rgb(0, 0, 0)" String
  *    background.rgb(0, 0, 0) // StyleSetter[String, String] that sets this rgb color
  *    }}}
  *
  * Shared usage of def derivedStyle:
  *    {{{
  *    trait units.Length {
  *      lazy val px: DSP[Int | Double] = derivedStyle(n => s"${n}px")
  *    }
  *    }}}
  *    style.px(12)      // "12px" String
  *    marginTop.px(12) // DerivedStyleSetter[Int] that sets this marginTop value
  *
  * Relative advantages of styleSetter and derivedStyle:
  *  - derivedStyle returns DSP[V] where you can choose any DSP (we use StyleSetter[_, String] and ~Function1[_, String]),
  *    but it can only accept one, unnamed argument of type InputV.
  *  - styleSetter can accept multiple named arguments of any types,
  *    but it returns a single type SSS (e.g. StyleSetter[String, String] or String)
  */
trait StyleBuilder[V, SSS] {

  protected def styleSetter(value: String): SSS
}
