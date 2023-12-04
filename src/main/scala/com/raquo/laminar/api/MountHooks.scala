package com.raquo.laminar.api

import com.raquo.airstream.ownership.{DynamicSubscription, Subscription}
import com.raquo.laminar.inserters.{DynamicInserter, InsertContext, Inserter, StaticInserter}
import com.raquo.laminar.lifecycle.MountContext
import com.raquo.laminar.modifiers.{Binder, Modifier, Setter}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

import scala.scalajs.js


trait MountHooks {

  /** Focus this element on mount */
  val onMountFocus: Modifier[ReactiveHtmlElement.Base] = onMountCallback(_.thisNode.ref.focus())

  /** Set a property / attribute / style on mount.
    * Similarly to other onMount methods, you only need this when:
    * a) you need to access MountContext
    * b) you truly need this to only happen on mount
    *
    * Example usage: `onMountSet(ctx => someAttr := someValue(ctx))`. See docs for details.
    */
  def onMountSet[El <: ReactiveElement.Base](fn: MountContext[El] => Setter[El]): Modifier[El] = {
    onMountCallback(c => fn(c)(c.thisNode))
  }

  /** Bind a subscription on mount
    *
    * Example usage: `onMountBind(ctx => someAttr <-- someObservable(ctx))`. See docs for details.
    */
  def onMountBind[El <: ReactiveElement.Base](
    fn: MountContext[El] => Binder[El]
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
      }
    )
  }

  /** Insert child node(s) on mount.
    *
    * Note: insert position is reserved as soon as this modifier is applied to the element.
    * Basically it will insert elements in the same position, where you'd expect, on every mount.
    *
    * Example usage: `onMountInsert(ctx => child <-- someObservable)`. See docs for details.
    */
  def onMountInsert[El <: ReactiveElement.Base](fn: MountContext[El] => Inserter): Modifier[El] = {
    Modifier[El] { element =>
      var maybeSubscription: Option[DynamicSubscription] = None
      // We start the context in loose mode for performance, because it's cheaper to go from there
      // to strict mode, than the other way. The inserters are able to handle any initial mode.
      val lockedInsertContext = InsertContext.reserveSpotContext(
        element, strictMode = false, hooks = js.undefined
      )
      element.amend(
        onMountUnmountCallback[El](
          mount = { mountContext =>
            val inserter = fn(mountContext)
            inserter match {
              case dynamicInserter: DynamicInserter =>
                maybeSubscription = Some(
                  dynamicInserter
                    .withContext(lockedInsertContext)
                    .bind(mountContext.thisNode)
                )
              case staticInserter: StaticInserter =>
                staticInserter.renderInContext(lockedInsertContext)
            }
          },
          unmount = { _ =>
            maybeSubscription.foreach(_.kill())
            maybeSubscription = None
          }
        )
      )
    }
  }

  /** Execute a callback on mount. Good for integrating third party libraries.
    *
    * The callback runs on every mount, not just the first one.
    * - Therefore, don't bind any subscriptions inside that you won't manually unbind on unmount.
    *   - If you fail to unbind manually, you will have N copies of them after mounting this element N times.
    *   - Use onMountBind or onMountInsert for that.
    *
    * When the callback is called, the element is already mounted.
    *
    * If you apply this modifier to an element that is already mounted, the callback
    * will not fire until and unless it is unmounted and mounted again.
    */
  def onMountCallback[El <: ReactiveElement.Base](fn: MountContext[El] => Unit): Modifier[El] = {
    Modifier[El] { element =>
      var ignoreNextActivation = ReactiveElement.isActive(element)
      ReactiveElement.bindCallback[El](element) { c =>
        if (ignoreNextActivation) {
          ignoreNextActivation = false
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
    * - Note that the same caveats apply as for those individual methods.
    * - See also: [[onMountUnmountCallbackWithState]]
    */
  def onMountUnmountCallback[El <: ReactiveElement.Base](
    mount: MountContext[El] => Unit,
    unmount: El => Unit
  ): Modifier[El] = {
    onMountUnmountCallbackWithState[El, Unit](mount, (el, _) => unmount(el))
  }

  /** Combines onMountCallback and onUnmountCallback for easier integration.
    * - Note that the same caveats apply as for those individual methods.
    * - The mount callback returns state which will be provided to the unmount callback.
    * - The unmount callback receives an Option of the state because it's possible that
    * onMountUnmountCallbackWithState was called *after* the element was already mounted,
    * in which case the mount callback defined here wouldn't have run.
    */
  def onMountUnmountCallbackWithState[El <: ReactiveElement.Base, A](
    mount: MountContext[El] => A,
    unmount: (El, Option[A]) => Unit
  ): Modifier[El] = {
    Modifier[El] { element =>
      var ignoreNextActivation = ReactiveElement.isActive(element)
      var state: Option[A] = None
      ReactiveElement.bindSubscriptionUnsafe[El](element) { c =>
        if (ignoreNextActivation) {
          ignoreNextActivation = false
        } else {
          state = Some(mount(c))
        }
        new Subscription(
          c.owner,
          cleanup = () => {
            unmount(element, state)
            state = None
          }
        )
      }
    }
  }

}
