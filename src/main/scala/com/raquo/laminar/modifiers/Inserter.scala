package com.raquo.laminar.modifiers

import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription}
import com.raquo.laminar.api.L.seqToModifier
import com.raquo.laminar.lifecycle.InsertContext
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement, TextNode}

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
  * So, you can use [[Inserter.Base]] essentially as (an implicit-powered)
  * sypertype of regular laminar elements and dynamic inserters like
  * `children <-- ...`. We use it this way in `onMountInsert`, for example.
  */
sealed trait Inserter[-El <: ReactiveElement.Base] extends Modifier[El]

object Inserter {

  type Base = Inserter[ReactiveElement.Base]
}

sealed trait StaticInserter[El <: ReactiveElement.Base] extends Inserter[El] {

  def renderInContext(ctx: InsertContext[El]): Unit
}

/** Inserter for a single static node */
class StaticTextInserter[El <: ReactiveElement.Base](
  text: String
) extends StaticInserter[El] {

  override def apply(element: El): Unit = {
    element.amend(new TextNode(text))
  }

  def renderInContext(ctx: InsertContext[El]): Unit = {
    ChildTextInserter.switchToText(new TextNode(text), ctx)
  }
}

/** Inserter for a single static node */
class StaticChildInserter[El <: ReactiveElement.Base](
  child: ChildNode.Base
) extends StaticInserter[El] {

  override def apply(element: El): Unit = {
    element.amend(child)
  }

  def renderInContext(ctx: InsertContext[El]): Unit = {
    ChildInserter.switchToChild(
      maybeLastSeenChild = js.undefined,
      newChildNode = child,
      ctx
    )
  }
}

/**
  * Inserter for multiple static nodes.
  * This can also insert a single nodes, just a bit less efficiently
  * than SingleStaticInserter.
  */
class StaticChildrenInserter[El <: ReactiveElement.Base](
  nodes: collection.Seq[ChildNode.Base]
) extends StaticInserter[El] {

  override def apply(element: El): Unit = {
    element.amend(nodes)
  }

  def renderInContext(ctx: InsertContext[El]): Unit = {
    ChildrenInserter.switchToChildren(nodes.toList, ctx)
  }
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
class DynamicInserter[-El <: ReactiveElement.Base](
  initialContext: Option[InsertContext[El]] = None,
  preferStrictMode: Boolean,
  insertFn: (InsertContext[El], Owner) => Subscription,
) extends Inserter[El] {

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
  def withContext(context: InsertContext[El]): DynamicInserter[El] = {
    // Note: preferStrictMode has no effect here, because initial context is defined.
    new DynamicInserter[El](Some(context), preferStrictMode = false, insertFn)
  }
}

object DynamicInserter {

  type Base = DynamicInserter[ReactiveElement.Base]
}
