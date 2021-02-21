package com.raquo.laminar.inputs

import com.raquo.airstream.core.Observer
import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription}
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar
import com.raquo.laminar.keys.{EventProcessor, ReactiveEventProp, ReactiveProp}
import com.raquo.laminar.modifiers.{EventListener, KeyUpdater}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import org.scalajs.dom

// @TODO[Elegance,Cleanup] There's some redundancy between prop / getDomValue / setDomValue / updater. Clean up later.
//  - Also maybe this shouldn't be a class... or shouuld be a Binder, or something
class ValueController[A, B](
  initialValue: A,
  setDomValue: (dom.html.Element, A) => Unit,
  element: ReactiveHtmlElement.Base,
  updater: KeyUpdater[ReactiveHtmlElement.Base, ReactiveProp[A, _], A],
  listener: EventListener[_ <: dom.Event, B]
) {

  private var prevValue: A = initialValue // Note: this might not match `defaultValue` / `defaultChecked` prop (see below)

  private val resetProcessor = listener.eventProcessor.orElseEval { _ =>
    // If value is not filtered out, resetObserver will handle it.
    // But if it was filtered out, we need to reset input state to previous value
    setValue(prevValue)
  }

  // Override the `defaultValue` prop.
  // If updater.$value is Signal, its initial value will in turn override this,
  // but if it's a stream, this will remain the effective initial value.
  setValue(initialValue) // this also sets prevValue

  private def setValue(nextValue: A): Unit = {
    // Don't add a currValue != nextValue check here for no reason
    // - Needless updates do not seem to affect cursor
    // - Having this check prevents setValue above from setting an empty value, thus
    //   failing to override defaultValue if that attribute is added after the controller
    setDomValue(element.ref, nextValue)
    prevValue = nextValue
  }

  private def combinedObserver(owner: Owner): Observer[B] = {
    var latestSourceValue: Option[A] = None

    // @TODO When re-mounting a previously unmounted component, we probably want to read the latest state from the source
    //  - This is only relevant if `source` had other observers
    //  - This might be excessively hard to achieve without https://github.com/raquo/Airstream/issues/43

    updater.$value.foreach { sourceValue =>
      latestSourceValue = Some(sourceValue)
      setValue(sourceValue)
    }(owner)

    val resetObserver = Observer[B] { _ =>
      // This needs to run after the event fired into `observer` has finished propagating
      // Browser events are always fired outside of the transaction, so wrapping this in Transaction is not required
      setValue(latestSourceValue.getOrElse(prevValue))
    }.setDisplayName("resetObserver").debugLog()
    Observer.combine(Observer(listener.callback).setDisplayName("listener.callback observer").debugLog(), resetObserver).setDisplayName("combinedObserver").debugLog()
  }

  private[laminar] def bind(): DynamicSubscription = {
    ReactiveElement.bindSubscription(element) { ctx =>

      // This should be run when the element's type property is properly set,
      // and doing this on bind gives the highest chance of that.
      ValueController.checkControllerCompatibility(this, updater.key, listener.eventProcessor, element)

      val otherEventSubscriptions = element.eventSubscriptions

      // Remove existing event listeners from the DOM
      //  - This does not touch `element.maybeEventSubscriptions` or `dynamicOwner.subscriptions`
      //  - We want to maintain the same DynamicSubscription references because users might be holding them too
      //    (e.g. as a result of calling .bind() on a listener), so we shouldn't kill them
      otherEventSubscriptions.foreach(s => DomApi.removeEventListener(element, s.listener))

      // Add the controller listener as the first one
      //  - `unsafePrepend` is safe here because we've just removed event listeners from the DOM
      //  - This prepends this subscription to `element.maybeEventSubscriptions` and `dynamicOwner.subscriptions`
      val dynSub = (resetProcessor --> combinedObserver(ctx.owner)).bind(element, unsafePrepend = true)

      // Bring back prior event listeners (in the same relative order, except now they run after the controller listener)
      //  - This does not touch `element.maybeEventSubscriptions` or `dynamicOwner.subscriptions`
      //  - After this, the order of subscriptions and listeners is the same everywhere
      //  - Note that listener caches the js.Function so we're adding the same exact listener back to the DOM.
      //    So, other than the desired side effect, this whole patch is very transparent to the users.
      otherEventSubscriptions.foreach(s => DomApi.addEventListener(element, s.listener))

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
}

object ValueController {

  private def nodeDescription(element: ReactiveHtmlElement.Base): String = {
    val maybeTyp = DomApi.getHtmlAttribute(element, Laminar.typ)
    val typSuffix = maybeTyp.map(t => s" [type=$t]").getOrElse("")
    s"${DomApi.debugNodeDescription(element.ref)}$typSuffix"
  }

  private def hasBinder(element: ReactiveHtmlElement.Base, prop: ReactiveProp[_, _]): Boolean = {
    if (prop == Laminar.value) {
      element.hasValueBinder
    } else if (prop == Laminar.checked) {
      element.hasCheckedBinder
    } else {
      false
    }
  }

  private def hasOtherController(
    thisController: ValueController[_, _],
    element: ReactiveHtmlElement.Base,
    prop: ReactiveProp[_, _]
  ): Boolean = {
    if (prop == Laminar.value) {
      element.hasOtherValueController(thisController)
    } else if (prop == Laminar.checked) {
      element.hasOtherCheckedController(thisController)
    } else {
      false
    }
  }

  /** @throws Exception if you can't add such a controller to this element. */
  private def checkControllerCompatibility(
    thisController: ValueController[_, _],
    prop: ReactiveProp[_, _],
    eventProcessor: EventProcessor[_ <: dom.Event, _],
    element: ReactiveHtmlElement.Base
  ): Unit = {

    if (hasOtherController(thisController, element, prop)) {
      throw new Exception(s"Can not add another `${prop.name}` controller to an element that already has one: ${nodeDescription(element)}")
    }

    if (hasBinder(element, prop)) {
      throw new Exception(s"Can not add `${prop.name}` controller to an element that already has a `${prop.name} <-- ???` binder: ${nodeDescription(element)}")
    }

    // @TODO[Warn] Consider warning if `type` is not set at this point. Might be annoying though.

    // Wait until mounting to check these conditions. By this time, the element's `type`
    // will certainly be set (assuming the user intended to set it).
    ValueController.expectedControlPairing(element) match {
      case None =>
        throw new Exception(s"Can not add `${prop.name}` controller to unsupported kind of element: ${nodeDescription(element)}")

      case Some((expectedProp, expectedEventProp)) =>
        if (prop != expectedProp) {
          val suggestion = s"Use `${expectedProp.name}` prop instead of `${prop.name}` prop"
          throw new Exception(s"Can not add `${prop.name}` controller to this element: ${nodeDescription(element)}: $suggestion.")
        } else if (EventProcessor.eventProp(eventProcessor) != expectedEventProp) {
          val suggestion = s"Use `${expectedEventProp.name}` event instead of `${EventProcessor.eventProp(eventProcessor).name}` event"
          throw new Exception(s"Can not add `${prop.name}` controller to this element: ${nodeDescription(element)}: $suggestion.")
        }
    }

  }

  /** @return Option((prop, eventProp)) */
  def expectedControlPairing(element: ReactiveHtmlElement.Base): Option[(ReactiveProp[_, _], ReactiveEventProp[_ <: dom.Event])] = {
    element.ref match {

      case input: dom.html.Input =>
        input.`type` match {
          case "text" => Some((Laminar.value, Laminar.onInput)) // Tiny perf shortcut for the most common case
          case "checkbox" | "radio" => Some((Laminar.checked, Laminar.onClick))
          case "file" => None
          case _ => Some((Laminar.value, Laminar.onInput)) // All the other input types: email, color, date, etc.
        }

      case _: dom.html.TextArea =>
        Some(Laminar.value, Laminar.onInput)

      case _: dom.html.Select =>
        // @TODO Allow onInput? it's the same but not all browsers support it.
        // Note: onChange browser event emits only when the selected value actually changes
        //       (clicking the same option doesn't trigger the event)
        Some(Laminar.value, Laminar.onChange)

      case el if DomApi.isCustomElement(el) =>
        // @TODO Not sure if custom elements can actually work that way.
        //  I think yes, if they fire onInput events and have a value prop. But is that how they usually work?
        Some(Laminar.value, Laminar.onInput)
    }
  }
}

