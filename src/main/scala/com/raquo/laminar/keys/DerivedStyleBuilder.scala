package com.raquo.laminar.keys

import com.raquo.ew._
import com.raquo.laminar.keys.DerivedStyleBuilder._

import scala.scalajs.js

trait DerivedStyleBuilder[DSP[_]] {

  protected def derivedStyle[InputV](encode: InputV => String): DSP[InputV]

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
}

object DerivedStyleBuilder {

  private val calcReplacePattern = new js.RegExp("[\"\'\n\r\f\\\\;]", flags = "g")

  private val urlReplacePattern = new js.RegExp("[\"\n\r\f\\\\]", flags = "g")

  private val calcReplacer: js.Function1[String, String] = { _ => " " }

  private val urlReplacer: js.Function1[String, String] = {
    case "\"" => "%22"
    case "\\" => "%5C"
    case _ => " "
  }
}
