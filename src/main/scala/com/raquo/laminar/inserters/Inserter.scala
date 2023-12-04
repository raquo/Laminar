package com.raquo.laminar.inserters

import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription}
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js

/** Inserter is a class that can insert child nodes into [[InsertContext]].
  *
  * This is needed in `onMountInsert`, or when rendering dynamic children
  * with `child <-- ...`, `children <-- ...`, etc.
  *
  * If you don't have a [[InsertContext]], you can render the Inserter
  * just by calling its `apply` method. It will work the same way as
  * rendering any other child node, static or dynamic, would.
  *
  * So, you can use [[Inserter]] essentially as (an implicit-powered)
  * sypertype of regular laminar elements and dynamic inserters like
  * `children <-- ...`. We use it this way in `onMountInsert`, for example.
  */
sealed trait Inserter extends Modifier[ReactiveElement.Base]

trait Hookable[+Self <: Inserter] { this: Inserter =>

  /** Create a copy of the inserter that will apply these
    * additional hooks after the original inserter's hooks.
    */
  def withHooks(addHooks: InserterHooks): Self
}

trait StaticInserter extends Inserter {

  def renderInContext(ctx: InsertContext): Unit
}


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
class DynamicInserter(
  initialContext: Option[InsertContext] = None,
  preferStrictMode: Boolean,
  insertFn: (InsertContext, Owner, js.UndefOr[InserterHooks]) => Subscription,
  hooks: js.UndefOr[InserterHooks] = js.undefined
) extends Inserter with Hookable[DynamicInserter] {

  def bind(element: ReactiveElement.Base): DynamicSubscription = {
    // @TODO[Performance] The way it's used in `onMountInsert`, we create a DynSub inside DynSub.
    //  - Currently this does not seem avoidable as we don't want to expose a `map` on DynSub
    //  - That would allow you to create leaky resources without having a reference to the owner
    //  - But maybe we require the user to provide proof of owner: dynSub.map(project)(owner) that must match DynSub
    // #Note we want to remember this context even after subscription is deactivated.
    //  Yes, we expect the subscription to re-activate with this initial state
    //  because it would match the state of the DOM upon reactivation
    //  (unless some of the managed child elements were externally removed from the DOM,
    //  which Laminar should be able to recover from).
    val insertContext = initialContext.getOrElse(
      InsertContext.reserveSpotContext(element, strictMode = preferStrictMode, hooks)
    )

    ReactiveElement.bindSubscriptionUnsafe(element) { mountContext =>
      insertFn(insertContext, mountContext.owner, hooks)
    }
  }

  override def apply(element: ReactiveElement.Base): Unit = {
    bind(element)
  }

  override def withHooks(addHooks: InserterHooks): DynamicInserter = {
    val newHooks = addHooks.appendTo(hooks)
    new DynamicInserter(initialContext, preferStrictMode, insertFn, newHooks)
  }

  /** Call this to get a copy of Inserter with a context locked to a certain element.
    * We use this to "reserve a spot" for future nodes when a bind(c => inserter) modifier
    * is initialized, as opposed to waiting until subscription is activated.
    *
    * The arrangement is admittedly a bit weird, but is required to build a smooth end user API.
    */
  def withContext(context: InsertContext): DynamicInserter = {
    // Note: preferStrictMode has no effect here, because initial context is defined.
    new DynamicInserter(Some(context), preferStrictMode = false, insertFn, hooks)
  }
}
