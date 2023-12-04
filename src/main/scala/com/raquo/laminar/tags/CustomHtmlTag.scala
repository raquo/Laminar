package com.raquo.laminar.tags

import com.raquo.ew.JsArray
import com.raquo.laminar.inputs.InputController
import com.raquo.laminar.inputs.InputController.InputControllerConfig
import com.raquo.laminar.keys.{EventProp, HtmlProp}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption

/** If you are creating custallowedInputControllerConfigIndiceshis class instead of [[HtmlTag]].
  *
  * Currently this class is only used to support `controlled` blocks in Web Components,
  * but it may get more features in the future.
  *
  * Note: We allow multiple input controller configs per custom element type in case there are weird cases like:
  *  - A web component with two HtmlProp-s defined for the same underlying prop, with different codecs
  *    - e.g. valueString vs valueNumber vs valueList
  *  - A web component with two input fields that can both be controlled independently.
  *
  * @param allowedInputControllerConfigIndices Specifies which of the configs defined in
  *                                            [[allowableInputControllerConfigs]] are actually
  *                                            allowed to be used in a `controlled` on a given
  *                                            element. This takes into account the element's
  *                                            details such as whether it's a checkbox or a text
  *                                            input, if that is not already apparent from the
  *                                            tag name.
  *
  *                                            This returns a list of 0-based indices of the
  *                                            allowed config from [[allowableInputControllerConfigs]].
  *
  * @param allowableInputControllerConfigs     Returns an array of input controller configs that
  *                                            can in principle be used on this element type.
  *                                            This is needed to warn users about conflicts
  *                                            with non-controlled binders for these properties.
  *                                            (i.e prevent situations where you have `value <-- ...`
  *                                            both controlled and uncontrolled on the same element)
  */
class CustomHtmlTag[Ref <: dom.html.Element](
  override val name: String,
  val allowedInputControllerConfigIndices: Ref => js.UndefOr[JsArray[Int]] = (_: Ref) => js.undefined,
  val allowableInputControllerConfigs: js.UndefOr[JsArray[InputControllerConfig[Ref, _]]] = js.undefined
) extends HtmlTag[Ref](name, void = false) {

  private[laminar] def allowedControllerConfigs(el: Ref): js.UndefOr[JsArray[InputControllerConfig[Ref, _]]] = {
    val maybeIndices = allowedInputControllerConfigIndices(el)
    maybeIndices.flatMap { indices =>
      allowableInputControllerConfigs.map { configs =>
        configs.filterWithIndex((_, ix) => indices.includes(ix))
      }
    }
  }

  private[laminar] def allowableControlProps: js.UndefOr[JsArray[String]] = {
    allowableInputControllerConfigs.map(props => props.map(_.prop.name))
  }

  private[laminar] def allowableControllerConfigForProp[A](prop: HtmlProp[A, _]): js.UndefOr[InputControllerConfig[Ref, A]] = {
    allowableInputControllerConfigs.flatMap { configs =>
      // We force the type to have `A`, because we know that this prop's config is a config of `A`.
      configs.asScalaJs.find(_.prop == prop).orUndefined.asInstanceOf[js.UndefOr[InputControllerConfig[Ref, A]]]
    }
  }

}

object CustomHtmlTag {

  /** For simple cases when configArray.length == 1 and the prop is always allowed */
  private[this] val allIndices = JsArray(0)

  def withControlledInput[Ref <: dom.html.Element, A, Ev <: dom.Event](
    tagName: String,
    prop: HtmlProp[A, _],
    initial: A,
    eventProp: EventProp[Ev]
  ): CustomHtmlTag[Ref] = {
    val config = InputController.customConfig(prop, JsArray(eventProp), initial)
    val configArray = JsArray[InputControllerConfig[Ref, _]](config)
    new CustomHtmlTag[Ref](tagName, _ => allIndices, configArray)
  }

  def withControlledInputs[Ref <: dom.html.Element, A, Ev <: dom.Event](
    tagName: String,
    prop: HtmlProp[A, _],
    initial: A,
    eventProps: EventProp[Ev]*
  ): CustomHtmlTag[Ref] = {
    val eventPropsArr: JsArray[EventProp[_]] = JsArray.from(eventProps)
    val allIndices = eventPropsArr.mapWithIndex((_, ix) => ix)
    val config = InputController.customConfig(prop, eventPropsArr, initial)
    val configArray = JsArray[InputControllerConfig[Ref, _]](config)
    new CustomHtmlTag[Ref](tagName, _ => allIndices, configArray)
  }

}
