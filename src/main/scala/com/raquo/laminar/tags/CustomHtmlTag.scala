package com.raquo.laminar.tags

import com.raquo.ew.JsArray
import com.raquo.laminar.keys.{EventProp, HtmlProp}
import org.scalajs.dom

import scala.scalajs.js

/** Html tag for custom elements (web components).
  *
  * Currently it is only used to support `controlled` blocks in
  * Web Components.
  *
  * @param allowedControlKeys Specifies which property and event can be
  *                           used in `controlled` block with this custom
  *                           element, if any. For most DOM input types the
  *                           pairing is "value" -> "input", for custom
  *                           elements it would typically be the equivalent
  *                           of this property and this event. For example,
  *                           for the Shoelace Input component, the correct
  *                           pairing is "value" -> "sl-input".
  */
class CustomHtmlTag[Ref <: dom.html.Element] (
  override val name: String,
  override val void: Boolean = false,
  val allowedControlKeys: Ref => js.UndefOr[(String, String)] = (_: Ref) => js.undefined,
  val allowableControlProps: JsArray[String]
) extends HtmlTag[Ref](name, void)
