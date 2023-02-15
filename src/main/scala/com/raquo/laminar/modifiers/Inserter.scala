package com.raquo.laminar.modifiers

import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription}
import com.raquo.laminar.lifecycle.InsertContext
import com.raquo.laminar.nodes.ReactiveElement

// @TODO[API] Inserter really wants to extend Binder. And yet.

/** Inserter is a modifier that lets you insert child node(s) on mount.
  * When used with onMountInsert, it "immediately" reserves an insertion
  * spot and then on every mount it inserts the node(s) into the same spot.
  *
  * Note: As a Modifier this is not idempotent, but overall
  * it behaves as you would expect. See docs for more details.
  *
  * Note: If you DO provide initialContext, its parentNode MUST always
  * be the same `element` that you apply this Modifier to.
  */
class Inserter[-El <: ReactiveElement.Base] (
  initialContext: Option[InsertContext[El]] = None,
  preferStrictMode: Boolean,
  insertFn: (InsertContext[El], Owner) => Subscription,
) extends Modifier[El] {

  def bind(element: El): DynamicSubscription = {
    // @TODO[Performance] The way it's used in `onMountInsert`, we create a DynSub inside DynSub.
    //  - Currently this does not seem avoidable as we don't want to expose a `map` on DynSub
    //  - That would allow you to create leaky resources without having a reference to the owner
    //  - But maybe we require the user to provide proof of owner: dynSub.map(project)(owner) that must match DynSub
    // #Note we want to remember this context even after subscription is deactivated.
    //  Yes, we expect the subscription to re-activate with this initial state
    //  because it would match the state of the DOM upon reactivation
    //  (unless some of the managed child elements were externally removed from the DOM,
    //  which Laminar should be able to recover from).
    val insertContext = initialContext.getOrElse(InsertContext.reserveSpotContext(element, strictMode = preferStrictMode))

    ReactiveElement.bindSubscriptionUnsafe(element) { mountContext =>
      insertFn(insertContext, mountContext.owner)
    }
  }

  override def apply(element: El): Unit = {
    bind(element)
  }

  /** Call this to get a copy of Inserter with a context locked to a certain element.
    * We use this to "reserve a spot" for future nodes when a bind(c => inserter) modifier
    * is initialized, as opposed to waiting until subscription is activated.
    *
    * The arrangement is admittedly a bit weird, but is required to build a smooth end user API.
    */
  def withContext(context: InsertContext[El]): Inserter[El] = {
    // Note: preferStrictMode has no effect here, because initial context is defined.
    new Inserter[El](Some(context), preferStrictMode = false, insertFn)
  }
}

object Inserter {

  type Base = Inserter[ReactiveElement.Base]
}
