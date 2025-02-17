package com.raquo.laminar.keys

import com.raquo.ew._

import scala.scalajs.js

trait StyleBuilder[SS, DSP[_]] {

  protected def styleSetter(value: String): SS

  // #Note: You can make this public if you wish
  protected def derivedStyle[InputV](encode: InputV => String): DSP[InputV]

  protected def encodeUrlValue(url: String): String = {
    // #TODO[Security] Review this.
    val escaped = url.ew.replace(
      StyleBuilder.urlPattern,
      StyleBuilder.urlReplacer
    ).str
    s""""$escaped"""" // #Note output is wrapped in double quotes
  }

  protected def encodeCalcValue(exp: String): String = {
    // #TODO[Security] Review this.
    val escaped = exp.ew.replace(
      StyleBuilder.calcPattern,
      StyleBuilder.calcReplacer
    ).str
    s"$escaped" // #Note output is NOT wrapped in double quotes
  }
}

object StyleBuilder {

  private val calcPattern = new js.RegExp("[\"\'\n\r\f\\\\;]", flags = "g")

  private val urlPattern = new js.RegExp("[\"\n\r\f\\\\]", flags = "g")

  private val calcReplacer: js.Function1[String, String] = { _ => " " }

  private val urlReplacer: js.Function1[String, String] = {
    case "\"" => "%22"
    case "\\" => "%5C"
    case _ => " "
  }
}
