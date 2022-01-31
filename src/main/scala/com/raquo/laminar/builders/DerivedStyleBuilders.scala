package com.raquo.laminar.builders

import com.raquo.domtypes.generic.builders.DerivedStylePropBuilder
import com.raquo.ew._

import scala.scalajs.js

trait DerivedStyleBuilders[T[_]] extends DerivedStylePropBuilder[T] {

  override protected def encodeUrlValue(url: String): String = {
    // #TODO[Security] Review this.
    val escaped = url.ew.replace(
      DerivedStyleBuilders.urlPattern,
      DerivedStyleBuilders.urlReplacer
    ).str
    s"\"$escaped\"" // #Note output is wrapped in double quotes
  }

  override protected def encodeCalcValue(exp: String): String = {
    // #TODO[Security] Review this.
    val escaped = exp.ew.replace(
      DerivedStyleBuilders.calcPattern,
      DerivedStyleBuilders.calcReplacer
    ).str
    s"$escaped" // #Note output is NOT wrapped in double quotes
  }
}

object DerivedStyleBuilders {

  private val calcPattern = new js.RegExp("[\"\'\n\r\f\\\\;]", flags = "g")

  private val urlPattern = new js.RegExp("[\"\n\r\f\\\\]", flags = "g")

  private val calcReplacer: js.Function1[String, String] = { _ => " " }

  private val urlReplacer: js.Function1[String, String] = {
    case "\"" => "%22"
    case "\\" => "%5C"
    case _ => " "
  }
}
