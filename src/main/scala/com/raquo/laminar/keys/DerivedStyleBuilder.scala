package com.raquo.laminar.keys

import com.raquo.ew._

import scala.scalajs.js

trait DerivedStyleBuilder[SS, DSP[_]] {

  protected def styleSetter(value: String): SS

  // #Note: You can make this public if you wish
  protected def derivedStyle[A](encode: (A => String)): DSP[A]

  protected def encodeUrlValue(url: String): String = {
    // #TODO[Security] Review this.
    val escaped = url.ew.replace(
      DerivedStyleBuilder.urlPattern,
      DerivedStyleBuilder.urlReplacer
    ).str
    s""""$escaped"""" // #Note output is wrapped in double quotes
  }

  protected def encodeCalcValue(exp: String): String = {
    // #TODO[Security] Review this.
    val escaped = exp.ew.replace(
      DerivedStyleBuilder.calcPattern,
      DerivedStyleBuilder.calcReplacer
    ).str
    s"$escaped" // #Note output is NOT wrapped in double quotes
  }
}

object DerivedStyleBuilder {

  private val calcPattern = new js.RegExp("[\"\'\n\r\f\\\\;]", flags = "g")

  private val urlPattern = new js.RegExp("[\"\n\r\f\\\\]", flags = "g")

  private val calcReplacer: js.Function1[String, String] = { _ => " " }

  private val urlReplacer: js.Function1[String, String] = {
    case "\"" => "%22"
    case "\\" => "%5C"
    case _ => " "
  }
}
