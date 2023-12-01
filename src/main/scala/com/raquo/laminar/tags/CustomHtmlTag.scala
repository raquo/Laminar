package com.raquo.laminar.tags

import com.raquo.ew.JsArray
import com.raquo.laminar.inputs.InputController.InputControllerConfig
import org.scalajs.dom

import scala.scalajs.js

/** If you are creating custom elements, you should use this class instead of [[HtmlTag]].
  *
  * Currently this class is only used to support `controlled` blocks in Web Components,
  * but it may get more features in the future.
  *
  * @param allowedInputControllerConfigs Specifies which property and event can be
  *                                      used in `controlled` block with this custom
  *                                      element, if any. For most DOM input types the
  *                                      pairing is "value" -> "input", for custom
  *                                      elements it would typically be the equivalent
  *                                      of this property and this event. For example,
  *                                      for the Shoelace Input component, the correct
  *                                      pairing is "value" -> "sl-input".
  *
  *                                      Note: Unlike [[allowableControlProps]], this one
  *                                      should be precise, because it has access to the
  *                                      element and its props, so e.g. it knows whether we're
  *                                      looking at `<input type="text">` or
  *                                      `<input type="checkbox">` (well, the Web Component
  *                                      equivalent of those).
  *
  * @param allowableControlProps         Must return an array of html property DOM names that
  *                                      [[allowedInputControllerConfigs]] can output configs
  *                                      for. This is needed to warn users about conflicts
  *                                      with non-controlled binders for these properties.
  *                                      (i.e prevent situations where you have `value <-- ...`
  *                                      both controlled and uncontrolled on the same element.)
  */
class CustomHtmlTag[Ref <: dom.html.Element](
  override val name: String,
  override val void: Boolean = false,
  val allowedInputControllerConfigs: Ref => js.UndefOr[InputControllerConfig[Ref, _]] = (_: Ref) => js.undefined,
  val allowableControlProps: js.UndefOr[JsArray[String]] = js.undefined
) extends HtmlTag[Ref](name, void)
