package com.raquo.laminar.inputs

import com.raquo.airstream.core.Observer
import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription}
import com.raquo.ew.JsArray
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L
import com.raquo.laminar.inputs.InputController.InputControllerConfig
import com.raquo.laminar.keys.{EventProcessor, EventProp, HtmlProp}
import com.raquo.laminar.modifiers.{Binder, EventListener, KeyUpdater}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import com.raquo.laminar.tags.CustomHtmlTag
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption

class InputController[Ref <: dom.html.Element, A, B](
  config: InputControllerConfig[Ref, A],
  element: ReactiveHtmlElement[Ref],
  updater: KeyUpdater[ReactiveHtmlElement[Ref], HtmlProp[A, _], A],
  listener: EventListener[_ <: dom.Event, B]
) {

  private var prevValue: A = config.initialValue // Note: this might not match `defaultValue` / `defaultChecked` prop (see below)

  private val resetProcessor = listener.eventProcessor.orElseEval { _ =>
    // If value is not filtered out, resetObserver will handle it.
    // But if it was filtered out, we need to reset input state to previous value
    setValue(prevValue)
  }

  // Force-override the `defaultValue` prop.
  // If updater.values is Signal, its initial value will in turn override this,
  // but if it's a stream, this will remain the effective initial value.
  setValue(config.initialValue, force = true) // this also sets prevValue

  def propDomName: String = updater.key.name

  def eventPropName: String = EventProcessor.eventProp(listener.eventProcessor).name

  private def setValue(nextValue: A, force: Boolean = false): Unit = {
    // Checking against current DOM value prevents cursor position reset in Safari
    if (force || nextValue != config.getDomValue(element)) {
      config.setDomValue(element, nextValue)
    }
    // We need to update prevValue regardless of the above condition (duh, it was only introduced to deal with a Safari DOM bug).
    // Otherwise, inputting *filtered out* values will clear the input value: https://github.com/raquo/Laminar/issues/100
    prevValue = nextValue
  }

  private def combinedObserver(owner: Owner): Observer[B] = {
    var latestSourceValue: Option[A] = None

    // @TODO When re-mounting a previously unmounted component, we probably want to read the latest state from the source
    //  - This is only relevant if `source` had other observers
    //  - This might be excessively hard to achieve without https://github.com/raquo/Airstream/issues/43

    updater.values.foreach { sourceValue =>
      latestSourceValue = Some(sourceValue)
      setValue(sourceValue)
    }(owner)

    val resetObserver = Observer[B] { _ =>
      // This needs to run after the event fired into `observer` has finished propagating
      // Browser events are always fired outside of the transaction, so wrapping this in Transaction is not required
      setValue(latestSourceValue.getOrElse(prevValue))
    }

    Observer.combine(Observer(listener.callback), resetObserver)
  }

  private[laminar] def bind(): DynamicSubscription = {
    ReactiveElement.bindSubscriptionUnsafe(element) { ctx =>

      // This should be run when the element's type property has already been set,
      // and doing this on bind gives the highest chance of that.
      checkControllerCompatibility()

      // Remove existing event listeners from the DOM
      //  - This does not touch `element.maybeEventSubscriptions` or `dynamicOwner.subscriptions`
      //  - We want to maintain the same DynamicSubscription references because users might be holding them too
      //    (e.g. as a result of calling .bind() on a listener), so we shouldn't kill them
      element.foreachEventListener(listener => DomApi.removeEventListener(element.ref, listener))

      // Add the controller listener as the first one
      //  - `unsafePrepend` is safe here because we've just removed event listeners from the DOM
      //  - This prepends this subscription to `element.maybeEventSubscriptions` and `dynamicOwner.subscriptions`
      val dynSub = (resetProcessor --> combinedObserver(ctx.owner)).bind(element, unsafePrepend = true)

      // Bring back prior event listeners (in the same relative order, except now they run after the controller listener)
      //  - This does not touch `element.maybeEventSubscriptions` or `dynamicOwner.subscriptions`
      //  - After this, the order of subscriptions and listeners is the same everywhere
      //  - Note that listener caches the js.Function so we're adding the same exact listener back to the DOM.
      //    So, other than the desired side effect, this whole patch is very transparent to the users.
      element.foreachEventListener(listener => DomApi.addEventListener(element.ref, listener))

      // @TODO[Performance] This rearrangement of listeners can be micro-optimized later, e.g.
      //  - Reduce scope of events that we're moving (we move all of them to maintain relative order between them)
      //  - Pre-register a pilot controller listener beforehand, and make other listeners aware of it via element
      //  - Or other ugly hacks. But in practice this is probably a non-issue just by the number of events / elements involved.

      // Summary: we have patched `element.maybeEventSubscriptions`, `dynamicOwner.subscriptions`,
      // and the listeners in the DOM to prepend `dynSub` to the list.
      // To undo this on unmount, all we need to do is kill `dynSub`.
      new Subscription(ctx.owner, cleanup = () => dynSub.kill())
    }
  }

  /** @throws Exception if you can't add such a controller to this element. */
  private[this] def checkControllerCompatibility(): Unit = {

    if (element.hasOtherControllerForSameProp(this)) {
      throw new Exception(InputController.errorMessage(propDomName, eventPropName, element)(
        reason = s"Element already has a `${propDomName}` controller."
      ))
    }

    if (element.hasBinderForControllableProp(propDomName)) {
      throw new Exception(InputController.errorMessage(propDomName, eventPropName, element)(
        reason = s"Element already has an uncontrolled `${propDomName} <-- ???` binder."
      ))
    }

    if (DomApi.isCustomElement(element.ref)) {
      element.tag match {
        case tag: CustomHtmlTag[Ref @unchecked] =>
          val maybeAllowedConfigs = tag.allowedControllerConfigs(element.ref)
          if (maybeAllowedConfigs.isEmpty || maybeAllowedConfigs.get.length == 0) {
            throw new Exception(InputController.errorMessage(propDomName, eventPropName, element)(
              reason = s"This element does not support any controlled props."
            ))
          } else {
            val maybeMatchingPropConfig = maybeAllowedConfigs.flatMap(_.asScalaJs.find(_.prop == updater.key).orUndefined)
            maybeMatchingPropConfig.fold(
              ifEmpty = {
                val expectedPropNames = s"`${maybeAllowedConfigs.get.map(_.prop.name).join("` or `")}`"
                throw new Exception(InputController.errorMessage(propDomName, eventPropName, element)(
                  reason = s"This element does not support `${propDomName}` controlled property",
                  suggestion = s"Use ${expectedPropNames} controlled property instead"
                ))
              }
            ) { config =>
              checkEventPropCompatibility(expectedEventProps = config.allowedEventProps)
            }
          }

        case _ =>
          // If nothing is specified, and user tries to use `controlled`,
          // they will get one of the errors above.
          ()
      }
    } else {
      val expectedPropName = config.prop.name
      if (propDomName != expectedPropName) {
        throw new Exception(InputController.errorMessage(propDomName, eventPropName, element)(
          reason = s"This element does not support `${propDomName}` controlled property",
          suggestion = s"Use `${expectedPropName}` controlled property instead"
        ))
      } else {
        checkEventPropCompatibility(expectedEventProps = config.allowedEventProps)
      }
    }
  }

  private[this] def checkEventPropCompatibility(
    expectedEventProps: JsArray[EventProp[_]]
  ): Unit = {
    val expectedEventProps = config.allowedEventProps
    if (!expectedEventProps.asScalaJs.exists(_.name == eventPropName)) {
      val expectedEventPropNames = s"`${expectedEventProps.map(_.name).join("` or `")}`"
      throw new Exception(InputController.errorMessage(propDomName, eventPropName, element)(
        reason = s"This element does not support `${eventPropName}` event for controlled inputs",
        suggestion = s"Use ${expectedEventPropNames} event instead"
      ))
    }
  }
}

object InputController {

  final class InputControllerConfig[-Ref <: dom.html.Element, A](
    val initialValue: A,
    val prop: HtmlProp[A, _],
    val allowedEventProps: JsArray[EventProp[_]],
    val getDomValue: ReactiveHtmlElement[Ref] => A,
    val setDomValue: (ReactiveHtmlElement[Ref], A) => Unit,
  )

  private val textValueConfig: InputControllerConfig[dom.html.Element, String] = new InputControllerConfig(
    initialValue = "",
    prop = L.value,
    allowedEventProps = JsArray(L.onInput),
    getDomValue = el => DomApi.getValue(el.ref).getOrElse(""),
    setDomValue = (el, v) => DomApi.setValue(el.ref, v)
  )

  private val selectValueConfig: InputControllerConfig[dom.html.Element, String] = new InputControllerConfig(
    initialValue = "",
    prop = L.value,
    allowedEventProps = JsArray(L.onInput, L.onChange), // Note: IE does not support onInput for select tags.
    getDomValue = el => DomApi.getValue(el.ref).getOrElse(""),
    setDomValue = (el, v) => DomApi.setValue(el.ref, v)
  )

  private val checkedConfig: InputControllerConfig[dom.html.Element, Boolean] = new InputControllerConfig(
    initialValue = false,
    prop = L.checked,
    allowedEventProps = JsArray(L.onInput, L.onClick), // #TODO does checkbox actually work with onInput?
    getDomValue = el => DomApi.getChecked(el.ref).getOrElse(false),
    setDomValue = (el, v) => DomApi.setChecked(el.ref, v)
  )

  /** Controller configs for custom properties.
    * This method is used to add input controller support to web components.
    */
  def customConfig[A](
    prop: HtmlProp[A, _],
    eventProps: JsArray[EventProp[_]],
    initial: A
  ): InputControllerConfig[dom.html.Element, A] = {
    new InputControllerConfig[dom.html.Element, A](
      initialValue = initial,
      prop = prop,
      allowedEventProps = eventProps,
      getDomValue = el => DomApi.getHtmlProperty(el, prop).get,
      setDomValue = (el, v) => DomApi.setHtmlProperty(el, prop, v)
    )
  }

  def controlled[Ref <: dom.html.Element, Ev <: dom.Event, A, B](
    listener: EventListener[Ev, B],
    updater: KeyUpdater[ReactiveHtmlElement[Ref], HtmlProp[A, _], A]
  ): Binder[ReactiveHtmlElement[Ref]] = {
    Binder[ReactiveHtmlElement[Ref]] { element =>
      val propDomName = updater.key.name
      val maybeControllableProps = element.controllableProps
      val isControllableProp = maybeControllableProps.exists(_.includes(propDomName))

      // We wait until mounting to check allowed props. By this time, the element's `type`
      // will certainly be set (assuming the user intended to set it), so we will get the
      // correct values (e.g. we'll know whether an element is <input type="checkbox">)

      if (isControllableProp) {
        val controller = {
          if (DomApi.isCustomElement(element.ref)) {
            element.tag match {
              case tag: CustomHtmlTag[Ref @unchecked] =>
                tag.allowableControllerConfigForProp(updater.key).fold(
                  ifEmpty = {
                    val eventPropName = EventProcessor.eventProp(listener.eventProcessor).name
                    throw new Exception(errorMessage(updater.key.name, eventPropName, element)(
                      reason = s"This element does not support `${propDomName}` controlled property in its current configuration",
                      suggestion = "Make sure you passed the right props / attributes, such as `type` for HTML inputs."
                    ))
                  }
                ) { config =>
                  new InputController(config, element, updater, listener)
                }
              case _ =>
                throw new Exception(s"Custom element `${nodeDescription(element)}` must use CustomHtmlTag in order to support `controlled` inputs.")
            }
          } else {
            allowedHtmlControllerConfig(element.ref).fold(
              ifEmpty = {
                val eventPropName = EventProcessor.eventProp(listener.eventProcessor).name
                throw new Exception(errorMessage(propDomName, eventPropName, element)(
                  reason = "This element does not support any controlled input props."
                ))
              }
            ) { config =>
              // Here we assume that the config is the correct one.
              // If not, InputController's checkControllerCompatibility method will
              // throw an exception when we call bindController below.
              val assumedConfig = config.asInstanceOf[InputControllerConfig[Ref, A]]
              new InputController(assumedConfig, element, updater, listener)
            }
          }
        }
        element.bindController(controller)

      } else {
        val eventPropName = EventProcessor.eventProp(listener.eventProcessor).name
        maybeControllableProps.fold(
          ifEmpty = throw new Exception(errorMessage(propDomName, eventPropName, element)(
            reason = "This element does not support any controlled input props."
          ))
        )(
          controllableProps => throw new Exception(errorMessage(propDomName, eventPropName, element)(
            reason = s"This element does not support `${propDomName}` controlled property",
            suggestion = s"Use `${controllableProps.join("` or `")}` controlled property instead"
          ))
        )
      }
    }
  }

  /** Standard HTML properties than can be `controlled` in Laminar. */
  private[laminar] val htmlControllableProps: JsArray[String] = JsArray("value", "checked")

  /** Returns the input controller config that we can use `controlled` for with this HTML element.
    *
    * Note: This method does not support web components.
    */
  def allowedHtmlControllerConfig[Ref <: dom.html.Element](element: Ref): js.UndefOr[InputControllerConfig[Ref, _]] = {
    // println("allowedHtmlControllerConfig? " + element.tagName)
    element match {

      case input: dom.html.Input =>
        input.`type` match {
          case "text" => textValueConfig // Tiny perf shortcut for the most common case
          case "checkbox" | "radio" => checkedConfig
          case "file" => js.undefined
          case _ => textValueConfig// All the other input types: email, color, date, etc.
        }

      case _: dom.html.TextArea =>
        textValueConfig

      case _: dom.html.Select =>
        // @TODO Allow onInput? it's the same but not all browsers support it.
        // Note: onChange browser event emits only when the selected value actually changes
        //       (clicking the same option doesn't trigger the event)
        selectValueConfig

      case _ =>
        js.undefined
    }
  }

  private def nodeDescription(element: ReactiveHtmlElement.Base): String = {
    val maybeTyp = DomApi.getHtmlAttributeRaw(element, L.typ)
    val typSuffix = maybeTyp.map(t => s" [type=$t]").getOrElse("")
    s"${DomApi.debugNodeDescription(element.ref)}$typSuffix"
  }

  private def errorMessage(
    propDomName: String,
    eventPropName: String,
    element: ReactiveHtmlElement.Base
  )(
    reason: String,
    suggestion: String = ""
  ): String = {
    JsArray(
      s"Can not add input controller (prop: `${propDomName}` + event: `${eventPropName}`) to element `${InputController.nodeDescription(element)}`",
      if (reason.nonEmpty) s"- Cause: $reason" else "",
      if (suggestion.nonEmpty) s"- Suggestion: $suggestion" else ""
    ).filter(_.nonEmpty).join("\n")
  }

}

