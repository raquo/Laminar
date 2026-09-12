package com.raquo.laminar.api

import com.raquo.airstream.ownership.{DynamicSubscription, Subscription}
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.inserters.{DynamicInserter, InsertContext, Inserter, StaticInserter}
import com.raquo.laminar.lifecycle.MountContext
import com.raquo.laminar.modifiers.{Binder, Modifier, Setter}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import org.scalajs.dom

import scala.scalajs.js

trait MountHooks {

  /** Focus this element on mount.
    *
    * Note: Starting with Laminar 18.x, the focus is set even if the element
    *       was already mounted by the time this modifier is added to it.
    */
  val onMountFocus: Modifier[ReactiveHtmlElement.Base] = onMountCallback(_.thisNode.ref.focus())

  /** Select the entier contents of this element on mount, the way browser selects text.
    *  - For input and textarea elements, this also focuses the element.
    */
  val onMountSelect: Modifier[ReactiveElement.Base] = {
    onMountCallback { ctx =>
      // asyncDelay ensures that Laminar runs this AFTER any synchronous `value <--` or `text <--`,
      // which may be needed to populate the element's content that we actually want to select.
      ctx.thisNode.select(asyncDelay = true)
    }
  }

  /** Set a property / attribute / style on mount.
    * Similarly to other onMount methods, you only need this when:
    * a) you need to access MountContext
    * b) you truly need this to only happen on mount
    *
    * Example usage: `onMountSet(ctx => someAttr := someValue(ctx))`. See docs for details.
    *
    * @param ignoreAlreadyMounted If `false`, the `fn` mount callback will be called even if the
    *                             element has already been mounted by the time this modifier is
    *                             added to it.
    *                             Starting with Laminar 18.x, `false` is the default.
    */
  def onMountSet[El <: ReactiveElement.Base](
    fn: MountContext[El] => Setter[El],
    ignoreAlreadyMounted: Boolean = false
  ): Modifier[El] = {
    onMountCallback(c => fn(c)(c.thisNode), ignoreAlreadyMounted)
  }

  /** Bind a subscription on mount
    *
    * Example usage: `onMountBind(ctx => someAttr <-- someObservable(ctx))`. See docs for details.
    *
    * @param ignoreAlreadyMounted If `false`, the `fn` mount callback will be called even if the
    *                             element has already been mounted by the time this modifier is
    *                             added to it.
    *                             Starting with Laminar 18.x, `false` is the default.
    */
  def onMountBind[El <: ReactiveElement.Base](
    fn: MountContext[El] => Binder[El],
    ignoreAlreadyMounted: Boolean = false
  ): Modifier[El] = {
    var maybeSubscription: Option[DynamicSubscription] = None
    onMountUnmountCallback(
      mount = { c =>
        val binder = fn(c)
        maybeSubscription = Some(binder.bind(c.thisNode))
      },
      unmount = { _ =>
        maybeSubscription.foreach(_.kill())
        maybeSubscription = None
      },
      ignoreAlreadyMounted
    )
  }

  /** Insert child node(s) on mount.
    *
    * Note: insert position is reserved as soon as this modifier is applied to the element.
    * Basically it will insert elements in the same position, where you'd expect, on every mount.
    *
    * Example usage: `onMountInsert(ctx => child <-- someObservable)`. See docs for details.
    *
    * @param ignoreAlreadyMounted If `false`, the `fn` mount callback will be called even if the
    *                             element has already been mounted by the time this modifier is
    *                             added to it.
    *                             Starting with Laminar 18.x, `false` is the default.
    */
  def onMountInsert[El <: ReactiveElement.Base](
    fn: MountContext[El] => Inserter,
    ignoreAlreadyMounted: Boolean = false
  ): Modifier[El] = {
    Modifier[El] { element =>
      var ignoreNextActivation = ignoreAlreadyMounted && ReactiveElement.isActive(element)
      // Position is reserved synchronously (so content lands where expected);
      // only the insertion itself waits until the element is ready.
      val lockedInsertContext = InsertContext.reserveSpotContext(
        parentNode = element,
        hooks = js.undefined
      )
      ReactiveElement.bindSubscriptionUnsafe(element) { mountContext =>
        /** guards the (possibly async) wait – see [[onMountUnmountCallbackWithState]] */
        var awaitingReadyState = false
        var inserterSubOpt: Option[Subscription] = None
        if (ignoreNextActivation) {
          ignoreNextActivation = false
        } else {
          awaitingReadyState = true
          DomApi.whenElementReady(element) { () =>
            if (awaitingReadyState && ReactiveElement.isActive(element)) {
              awaitingReadyState = false
              fn(mountContext) match {
                case dynamicInserter: DynamicInserter =>
                  inserterSubOpt = Some(
                    dynamicInserter.subscribe(
                      insertContext = lockedInsertContext,
                      owner = mountContext.owner
                    )
                  )
                case staticInserter: StaticInserter =>
                  staticInserter.renderInContext(lockedInsertContext)
              }
            }
          }
        }
        // #Note: We're using this inside `bindSubscriptionUnsafe`,
        //  so this subscription must not be killed externally!
        new Subscription(
          mountContext.owner,
          cleanup = () => {
            awaitingReadyState = false
            inserterSubOpt.foreach(_.kill())
            inserterSubOpt = None
          }
        )
      }
    }
  }

  /** Execute a callback on mount. Good for integrating third party libraries.
    *
    * The callback runs on every mount, not just the first one.
    *  - Therefore, don't bind any subscriptions inside that you won't manually unbind on unmount.
    *    - If you fail to unbind manually, you will have N copies of them after mounting this element N times.
    *    - Use onMountBind or onMountInsert for that.
    *
    * When the callback is called, the element is already mounted. For regular
    * elements it fires synchronously on mount; for web components it fires once
    * the component is defined and rendered (see [[DomApi.whenElementReady]]), so
    * you can safely interact with it – necessarily asynchronously. To run
    * synchronously on mount instead, don't use `onMount*`: e.g.
    * `EventStream.fromValue(()) --> (_ => ...)` or plain `attr <-- ` subscriptions.
    *
    * If you apply this modifier to an element that is already mounted, the callback
    * will not fire until and unless it is unmounted and mounted again.
    *
    * @param ignoreAlreadyMounted If `false`, the `fn` mount callback will be called even if the
    *                             element has already been mounted by the time this modifier is
    *                             added to it.
    *                             Starting with Laminar 18.x, `false` is the default.
    */
  def onMountCallback[El <: ReactiveElement.Base](
    fn: MountContext[El] => Unit,
    ignoreAlreadyMounted: Boolean = false
  ): Modifier[El] = {
    Modifier[El] { element =>
      var ignoreNextActivation = ignoreAlreadyMounted && ReactiveElement.isActive(element)
      // Identifies the latest mount, so a deferred callback can tell whether
      // its mount is still the current one after an async wait.
      var latestMountId = 0
      ReactiveElement.bindCallback[El](element) { c =>
        if (ignoreNextActivation) {
          ignoreNextActivation = false
        } else if (DomApi.isCustomElement(element.ref)) {
          latestMountId += 1
          val mountId = latestMountId
          DomApi.whenElementReady(element) { () =>
            // Skip if unmounted, or unmounted and re-mounted, during the wait.
            if (mountId == latestMountId && ReactiveElement.isActive(element)) {
              fn(c)
            }
          }
        } else {
          fn(c)
        }
      }
    }
  }

  /** Execute a callback on unmount. Good for integrating third party libraries.
    *
    * When the callback is called, the element is still mounted.
    *
    * If you apply this modifier to an element that is already unmounted, the callback
    * will not fire until and unless it is mounted and then unmounted again.
    */
  def onUnmountCallback[El <: ReactiveElement.Base](fn: El => Unit): Modifier[El] = {
    Modifier[El] { element =>
      ReactiveElement.bindSubscriptionUnsafe(element) { c =>
        new Subscription(c.owner, cleanup = () => fn(element))
      }
    }
  }

  /** Combines onMountCallback and onUnmountCallback for easier integration.
    *  - Note that the same caveats apply as for those individual methods.
    *  - See also: [[onMountUnmountCallbackWithState]]
    *
    * @param ignoreAlreadyMounted If `false`, the `mount` callback will be called even if the
    *                             element has already been mounted by the time this modifier
    *                             is added to it.
    *                             Starting with Laminar 18.x, `false` is the default.
    */
  def onMountUnmountCallback[El <: ReactiveElement.Base](
    mount: MountContext[El] => Unit,
    unmount: El => Unit,
    ignoreAlreadyMounted: Boolean = false
  ): Modifier[El] = {
    onMountUnmountCallbackWithState(
      mount, (el, _: Any) => unmount(el), ignoreAlreadyMounted
    )
  }

  /** Combines `onMountCallback` and `onUnmountCallback` for easier integration.
    *  - Note that the same caveats apply as for those individual methods.
    *  - This implementation defaults to `ignoreAlreadyMounted = false`.
    *    - Because of this, `unmount` is guaranteed to be called only if `mount`
    *      was previously called, so we can get state as `A` instead of `Option[A]`
    */
  def onMountUnmountCallbackWithState[El <: ReactiveElement.Base, A](
    mount: MountContext[El] => A,
    unmount: (El, A) => Unit
  ): Modifier[El] = {
    onMountUnmountCallbackWithState(
      mount,
      (el, maybeState: Option[A]) => unmount(el, maybeState.get),
      ignoreAlreadyMounted = false
    )
  }

  /** Combines onMountCallback and onUnmountCallback for easier integration.
    *  - Note that the same caveats apply as for those individual methods.
    *  - The mount callback returns state which will be provided to the unmount callback.
    *  - In this overloaded version, the unmount callback receives an Option of the state
    *    because it's possible that onMountUnmountCallbackWithState was called *after*
    *    the element was already mounted, in which case the mount callback defined here
    *    wouldn't have run.
    *    - To avoid this Option, don't provide any value for the `ignoreAlreadyMounted` argument.
    *
    * @param ignoreAlreadyMounted If `false`, the `mount` callback will be called even if the
    *                             element has already been mounted by the time this modifier
    *                             is added to it.
    *                             Starting with Laminar 18.x, `false` is the default.
    */
  def onMountUnmountCallbackWithState[El <: ReactiveElement.Base, A](
    mount: MountContext[El] => A,
    unmount: (El, Option[A]) => Unit,
    ignoreAlreadyMounted: Boolean
  ): Modifier[El] = {
    Modifier[El] { element =>
      var ignoreNextActivation = ignoreAlreadyMounted && ReactiveElement.isActive(element)
      var state: Option[A] = None
      ReactiveElement.bindSubscriptionUnsafe[El](element) { c =>
        // Per-cycle flag (this closure runs fresh on each mount): true while a
        // deferred `mount` hasn't run yet. Lets `cleanup` cancel it and skip
        // `unmount`, keeping the two paired (and never `.get`-ing a None state).
        var awaitingReady = false
        if (ignoreNextActivation) {
          ignoreNextActivation = false
        } else {
          awaitingReady = true
          DomApi.whenElementReady(element) { () =>
            // isActive check is just in case, awaitingReady should be enough.
            if (awaitingReady && ReactiveElement.isActive(element)) {
              awaitingReady = false
              state = Some(mount(c))
            }
          }
        }
        new Subscription(
          c.owner,
          cleanup = () => {
            if (awaitingReady) {
              // Unmounted before it got ready:
              // `mount` never ran, so cancel it and skip `unmount`.
              awaitingReady = false
            } else {
              unmount(element, state)
              state = None
            }
          }
        )
      }
    }
  }

  /** Print message when this element is mounted or unmounted.
    *
    * If you add this modifier to an already mounted element, it will log
    * "already-mounted" instead of "mount" to distinguish this case.
    *
    * Note: Since Laminar v18, other onMount* modifiers default to `ignoreAlreadyMounted = false`.
    *
    * @param label
    *   If not provided, default node description including IDs and classes will be used.
    */
  def debugLogLifecycle(label: String = ""): Modifier.Base = {
    def debugNodeDescription(el: ReactiveElement.Base): String = {
      if (label.nonEmpty)
        label
      else
        DomApi.debugNodeDescription(el.ref)
    }
    Modifier { element =>
      val alreadyMounted = ReactiveElement.isActive(element)
      var mountIx = 0
      ReactiveElement.bindSubscriptionUnsafe(element) { c =>
        mountIx += 1
        if (alreadyMounted) {
          dom.console.log(s"${debugNodeDescription(element)}: [already-mounted #$mountIx]")
        } else {
          dom.console.log(s"${debugNodeDescription(element)}: [mount #$mountIx]")
        }
        new Subscription(
          c.owner,
          cleanup = () => {
            dom.console.log(s"${debugNodeDescription(element)}: [unmount #$mountIx]")
          }
        )
      }
    }
  }

}
