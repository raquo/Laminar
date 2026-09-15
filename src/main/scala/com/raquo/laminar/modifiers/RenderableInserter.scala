package com.raquo.laminar.modifiers

import com.raquo.laminar.inserters.Inserter

import scala.annotation.implicitNotFound

/** `RenderableInserter[Component]` is evidence that you can render a Component as
  * one item of a `children <--` list.
  *
  * This is the `children <--` counterpart of [[RenderableNode]]. Unlike a node, an
  * item can be a dynamic inserter (`child <--`, `children <--`) that manages its
  * own span of DOM nodes and its own lifecycle – which is what lets you nest
  * dynamic inserters directly among the children (Laminar issue #157).
  *
  * Implicit instances come from two places, with no duplication between them:
  *  - any [[Inserter]] renders as itself – see [[RenderableInserter.inserterRenderable]]
  *  - anything that has a [[RenderableNode]] instance renders as a single static node.
  */
@implicitNotFound("Implicit instance of RenderableInserter[${Component}] not found. Values in a `children <--` list must be Laminar nodes, components with a RenderableNode instance, or Inserters like `child <-- ...` / `children <-- ...`.")
trait RenderableInserter[-Component] {

  /** Render this value as the [[Inserter]] that serves as its `children <--`
    * list-item handle. The children diff uses the returned inserter both to obtain
    * the item's stable anchor identity ([[Inserter.stableFirstNode]], used to match
    * items across observable emissions) and to insert / move / remove the item's span.
    *
    * This must be cheap and idempotent: it is called for every item on every
    * emission, and it MUST return the same anchor (`asInserter(value).stableFirstNode`)
    * for the same logical item across emissions (this holds for a node – its `ref`
    * is stable – and for an inserter – its leading sentinel is stable per instance;
    * for `split` the callback is memoized per key).
    */
  def asInserter(value: Component): Inserter
}

object RenderableInserter {

  /** Any Inserter (e.g. `child <-- ...`) renders as itself; its anchor node (its
    * leading sentinel, for a dynamic inserter) is its identity.
    *
    * Components with a [[RenderableNode]] instance do not need an instance here:
    * `RenderableNode` extends `RenderableInserter`, so their `RenderableNode` instance
    * is found directly.
    */
  implicit val inserterRenderable: RenderableInserter[Inserter] = new RenderableInserter[Inserter] {

    override def asInserter(value: Inserter): Inserter = value
  }
}
