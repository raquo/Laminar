package com.raquo.laminar.keys

import com.raquo.ew._

import scala.scalajs.js

/** This trait is extended by:
  *  - StyleProp[StyleSetter[V], DerivedStyleProp]
  *    - def styleSetter returns `key := value` setters
  *  - L.style via trait StyleUnitsApi[String, StyleEncoder]
  *    - def styleSetter returns string values
  * Shared usage of def styleSetter via units:
  *    {{{
  *    trait units.Color {
  *      def rgb(red: Int, green: Int, blue: Int): SS =
  *        styleSetter(s"rgb($red, $green, $blue)")
  *    }
  *    style.rgb(0, 0, 0)      // "rgb(0, 0, 0)" String
  *    background.rgb(0, 0, 0) // StyleSetter[String] that sets this rgb color
  *    }}}
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
  *  - derivedStyle returns DSP[V] where you can choose any DSP (we use StyleSetter[_] and ~Function1[_, String]),
  *    but it can only accept one, unnamed argument of type InputV.
  *  - styleSetter can accept multiple named arguments of any types,
  *    but it returns a single type SS (e.g. StyleSetter[V] or V)
  */
trait StyleBuilder[SS, DSP[_]] {

  protected def styleSetter(value: String): SS

  protected def derivedStyle[InputV](encode: InputV => String): DSP[InputV]

  protected def encodeUrlValue(url: String): String = {
    // #TODO[Security] Review this.
    val escaped = url.ew.replace(
      StyleBuilder.urlReplacePattern,
      StyleBuilder.urlReplacer
    ).str
    s""""$escaped"""" // #Note output is wrapped in double quotes
  }

  protected def encodeCalcValue(exp: String): String = {
    // #TODO[Security] Review this.
    val escaped = exp.ew.replace(
      StyleBuilder.calcReplacePattern,
      StyleBuilder.calcReplacer
    ).str
    s"$escaped" // #Note output is NOT wrapped in double quotes
  }
}

object StyleBuilder {

  private val calcReplacePattern = new js.RegExp("[\"\'\n\r\f\\\\;]", flags = "g")

  private val urlReplacePattern = new js.RegExp("[\"\n\r\f\\\\]", flags = "g")

  private val calcReplacer: js.Function1[String, String] = { _ => " " }

  private val urlReplacer: js.Function1[String, String] = {
    case "\"" => "%22"
    case "\\" => "%5C"
    case _ => " "
  }
}
