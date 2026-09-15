package com.raquo.laminar.fixtures

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.keys.HtmlProp
import com.raquo.laminar.nodes.Slot
import org.scalajs.dom

import scala.scalajs.js

object DoubleRangeElement extends WebComponent("sl-range-double") {

  override type Ref = RangeComponent with dom.HTMLElement

  lazy val value: HtmlProp[Double] = HtmlProp("value", reflectedAttrName = None, Codec.doubleAsIs)

  object slots {

    val prefix = new Slot("prefix")
  }

  @js.native
  trait RangeComponent extends js.Object { this: dom.HTMLElement =>
    val value: Double
  }
}
