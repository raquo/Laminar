package com.raquo.laminar.modifiers

import com.raquo.laminar.modifiers.ChildrenInserter.{Child, Children}

import scala.annotation.implicitNotFound
import scala.collection.immutable

/** `RenderableNode[Component]` is evidence that you can convert a Component to
  * a Laminar ChildNode.
  *
  * If you have an implicit val of RenderableNode[Component], Laminar can
  * render your Component-s by converting them to ChildNode-s, and will accept
  * your Component-s in `child <--`, `children <--`, etc.
  *
  * A `Component` must have a 1-to-1 relationship to a Laminar ChildNode.
  * Your Component class/trait should have something like `val node: ChildNode.Base`
  * or `lazy val node: ChildNode.Base` in it, it must not be a `var` or a `def`.
  *
  * See also – [[RenderableText]]
  */
@implicitNotFound("Implicit instance of RenderableNode[${Component}] not found. If you want to render `${Component}` as a node / element, define an implicit RenderableNode[${Component}] instance for it.")
trait RenderableNode[-Component] {

  /** For every component, this MUST ALWAYS return the exact same node reference. */
  def asNode(value: Component): Child

  /** For every component in the sequence, this MUST ALWAYS return the exact same node reference. */
  def asNodeSeq(values: immutable.Seq[Component]): Children

  /** For every component in the sequence, this MUST ALWAYS return the exact same node reference. */
  def asNodeIterable(values: Iterable[Component]): Iterable[Child]

  /** For every component, this MUST ALWAYS return the exact same node reference. */
  def asNodeOption(value: Option[Component]): Option[Child]
}

object RenderableNode {

  def apply[Component](
    renderNode: Component => Child,
    renderNodeSeq: immutable.Seq[Component] => Children,
    renderNodeIterable: Iterable[Component] => Iterable[Child],
    renderNodeOption: Option[Component] => Option[Child]
  ): RenderableNode[Component] = new RenderableNode[Component] {

    override def asNode(value: Component): Child = renderNode(value)

    override def asNodeSeq(values: immutable.Seq[Component]): Children = renderNodeSeq(values)

    override def asNodeIterable(values: Iterable[Component]): Iterable[Child] = renderNodeIterable(values)

    override def asNodeOption(value: Option[Component]): Option[Child] = renderNodeOption(value)
  }

  implicit val nodeRenderable: RenderableNode[Child] = RenderableNode[Child](identity, identity, identity, identity)

}
