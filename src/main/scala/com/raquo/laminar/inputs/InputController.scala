package com.raquo.laminar.inputs

import com.raquo.airstream.core.Observer
import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription}
import com.raquo.ew.JsArray
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L
import com.raquo.laminar.keys.{EventProcessor, HtmlProp}
import com.raquo.laminar.modifiers.{Binder, EventListener, KeyUpdater}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import com.raquo.laminar.tags.CustomHtmlTag
import org.scalajs.dom

import scala.scalajs.js

// @TODO[Elegance,Cleanup] There's some redundancy between prop / getDomValue / setDomValue / updater. Clean up later.
//  - Also maybe this shouldn't be a class... or should be a Binder, or something
class InputController[A, B](
  initialValue: A,
  getDomValue: dom.html.Element => A,
  setDomValue: (dom.html.Element, A) => Unit,
  element: ReactiveHtmlElement.Base,
  updater: KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[A, _], A],
  listener: EventListener[_ <: dom.Event, B]
) {

  private var prevValue: A = initialValue // Note: this might not match `defaultValue` / `defaultChecked` prop (see below)

  private val resetProcessor = listener.eventProcessor.orElseEval { _ =>
    // If value is not filtered out, resetObserver will handle it.
    // But if it was filtered out, we need to reset input state to previous value
    setValue(prevValue)
  }

  // Force-override the `defaultValue` prop.
  // If updater.values is Signal, its initial value will in turn override this,
  // but if it's a stream, this will remain the effective initial value.
  setValue(initialValue, force = true) // this also sets prevValue

  def propDomName: String = updater.key.name

  private def setValue(nextValue: A, force: Boolean = false): Unit = {
    // Checking against current DOM value prevents cursor position reset in Safari
    if (force || nextValue != getDomValue(element.ref)) {
      setDomValue(element.ref, nextValue)
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

      // This should be run when the element's type property is properly set,
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
      throw new Exception(s"Can not add another `${propDomName}` controller to an element that already has one: ${InputController.nodeDescription(element)}")
    }

    if (element.hasBinderForControllableProp(propDomName)) {
      throw new Exception(s"Can not add `${propDomName}` controller to an element that already has a `${propDomName} <-- ???` binder: ${InputController.nodeDescription(element)}")
    }

    // @TODO[Warn] Consider warning if `type` is not set at this point. Might be annoying though.

    // Wait until mounting to check these conditions. By this time, the element's `type`
    // will certainly be set (assuming the user intended to set it).
    InputController.allowedControlKeys(element).fold(
      ifEmpty = throw new Exception(s"Can not add `${propDomName}` controller to unsupported kind of element: ${InputController.nodeDescription(element)}")

    ) { case (expectedProp, expectedEventProp) =>
      if (propDomName != expectedProp) {
        val suggestion = s"Use `${expectedProp}` prop instead of `${propDomName}` prop"
        throw new Exception(s"Can not add `${propDomName}` controller to this element: ${InputController.nodeDescription(element)}: $suggestion.")

      } else if (EventProcessor.eventProp(listener.eventProcessor).name != expectedEventProp) {
        val suggestion = s"Use `${expectedEventProp}` event instead of `${EventProcessor.eventProp(listener.eventProcessor).name}` event"
        throw new Exception(s"Can not add `${propDomName}` controller to this element: ${InputController.nodeDescription(element)}: $suggestion.")
      }
    }
  }
}

object InputController {

  def controlled[El <: ReactiveHtmlElement.Base, Ev <: dom.Event, V](
    updater: KeyUpdater[El, HtmlProp[V, _], V],
    listener: EventListener[Ev, _]
  ): Binder[El] = {
    Binder[El] { element =>
      val propDomName = updater.key.name
      val controllableProps = element.controllableProps
      val isControllableProp = controllableProps.exists(_.includes(propDomName))

      if (isControllableProp) {
        if (DomApi.isCustomElement(element.ref)) {
          // #TODO implement this
          ???
        } else {
          val controller = {
            if (propDomName == "value") {
              valueController(
                element,
                updater.asInstanceOf[KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[String, _], String]], // #TODO[Integrity]
                listener
              )
            } else {
              if (propDomName != "checked") {
                throw new Exception(s"Unexpected HTML controlled prop: ${propDomName}")
              }
              checkedController(
                element,
                updater.asInstanceOf[KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[Boolean, _], Boolean]], // #TODO[Integrity]
                listener
              )
            }
          }
          element.bindController(controller)
        }
      } else {
        controllableProps.fold(
          ifEmpty = throw new Exception(s"Can not add a controller for property `${propDomName}` to ${nodeDescription(element)} – this element type is not configured to allow controlled inputs. See docs on controlled inputs for details.")
        )(
          props => throw new Exception(s"Can not add a controller for property `${propDomName}` to ${nodeDescription(element)} – on this element type, only the following props can be controlled this way: `${props.join("`, `")}`. See docs on controlled inputs for details.")
        )
      }
    }
  }

  private[this] def valueController[A, El <: ReactiveHtmlElement.Base, Ev <: dom.Event](
    element: El,
    updater: KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[String, _], String],
    listener: EventListener[Ev, A]
  ): InputController[String, A] = {
    new InputController[String, A](
      initialValue = "",
      getDomValue = DomApi.getValue(_).getOrElse(""),
      setDomValue = DomApi.setValue,
      element = element,
      updater,
      listener
    )
  }

  private[this] def checkedController[A, El <: ReactiveHtmlElement.Base, Ev <: dom.Event](
    element: El,
    updater: KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[Boolean, _], Boolean],
    listener: EventListener[Ev, A]
  ): InputController[Boolean, A] = {
    new InputController[Boolean, A](
      initialValue = false,
      getDomValue = DomApi.getChecked(_).getOrElse(false),
      setDomValue = DomApi.setChecked,
      element = element,
      updater,
      listener
    )
  }

  /** Standard HTML properties than can be `controlled` in Laminar. */
  private[laminar] val htmlControllableProps: JsArray[String] = JsArray("value", "checked")

  /** Returns the prop and eventProp that we can use `controlled` for with this element.
    *
    * For custom elements / Web Components, you need to specify this in their
    * [[CustomHtmlTag]]'s `allowedControlKeys` property.
    *
    * @return Option((prop, eventProp))
    */
  def allowedControlKeys[Ref <: dom.html.Element](element: ReactiveHtmlElement[Ref]): js.UndefOr[(String, String)] = {
    element.ref match {

      case input: dom.html.Input =>
        input.`type` match {
          case "text" => "value" -> "input" // Tiny perf shortcut for the most common case
          case "checkbox" | "radio" => "checked" -> "click"
          case "file" => js.undefined
          case _ => "value" -> "input" // All the other input types: email, color, date, etc.
        }

      case _: dom.html.TextArea =>
        "value" -> "input"

      case _: dom.html.Select =>
        // @TODO Allow onInput? it's the same but not all browsers support it.
        // Note: onChange browser event emits only when the selected value actually changes
        //       (clicking the same option doesn't trigger the event)
        "value" -> "change"

      case el if DomApi.isCustomElement(el) =>
        element.tag match {
          case tag: CustomHtmlTag[Ref@unchecked] => tag.allowedControlKeys(element.ref)
          case _ => js.undefined // If nothing is specified, and user tries to use `controlled`, they will get one of the errors above.
        }

      case _ =>
        js.undefined
    }
  }

  private def nodeDescription(element: ReactiveHtmlElement.Base): String = {
    val maybeTyp = DomApi.getHtmlAttributeRaw(element, L.typ)
    val typSuffix = maybeTyp.map(t => s" [type=$t]").getOrElse("")
    s"${DomApi.debugNodeDescription(element.ref)}$typSuffix"
  }

}

