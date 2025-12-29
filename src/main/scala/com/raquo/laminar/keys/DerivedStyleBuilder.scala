package com.raquo.laminar.keys

import com.raquo.ew._
import com.raquo.laminar.keys.DerivedStyleBuilder._

import scala.scalajs.js

trait DerivedStyleBuilder[DSP[_]] {

  protected def derivedStyle[InputV](encode: InputV => String): DSP[InputV]

  protected def encodeVarName(cssVarName: String): String = {
    if (simplifiedVarNamePattern.test(cssVarName)) {
      cssVarName
    } else {
      throw new Exception(s"Invalid CSS var name: `${cssVarName}`") // #TODO[API] Should we throw on invalid values? Other encoders mangle the values on some bad inputs.
    }
  }

  protected def encodeUrlValue(url: String): String = {
    // #TODO[Security] Review this.
    val escaped = url.ew.replace(urlReplacePattern, urlReplacer).str
    s""""$escaped"""" // #Note output is wrapped in double quotes
  }

  protected def encodeCalcValue(exp: String): String = {
    // #TODO[Security] Review this.
    val escaped = exp.ew.replace(calcReplacePattern, calcReplacer).str
    s"$escaped" // #Note output is NOT wrapped in double quotes
  }

  // JSDOM does not support window.CSS yet, so we can't really use it: https://github.com/jsdom/jsdom/issues/3991
  // Note that this escaping is intended for escaping class names and IDs, it's overkill (but I think safe?) for other CSS purposes.
  // protected def cssEscape(css: String): String = {
  //   dom.window.asInstanceOf[js.Dynamic].selectDynamic("CSS").applyDynamic("escape")(css).asInstanceOf[String]
  // }
}

object DerivedStyleBuilder {

  /** For simplicity, this pattern for CSS var names disallows some valid characters, specifically:
    *  - non-ASCII characters
    *  - invalid characters even if they're escaped with `\`
    */
  private val simplifiedVarNamePattern = js.RegExp("^--[a-zA-Z0-9_-]+$")

  /** Finds unsafe things to replace in `calc(str)` pattern */
  private val calcReplacePattern = new js.RegExp("[\"\'\n\r\f\\\\;]", flags = "g")

  /** Finds unsafe things to replace in `url(str)` pattern */
  private val urlReplacePattern = new js.RegExp("[\"\n\r\f\\\\]", flags = "g")

  /** Replaces things found by `calcReplacePattern` with sanitized or encoded values. */
  private val calcReplacer: js.Function1[String, String] = { _ => " " }

  /** Replaces things found by `urlReplacePattern` with sanitized or encoded values. */
  private val urlReplacer: js.Function1[String, String] = {
    case "\"" => "%22"
    case "\\" => "%5C"
    case _ => " "
  }
}
