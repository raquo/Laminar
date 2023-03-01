package com.raquo.laminar.modifiers

import com.raquo.laminar.nodes.ChildNode

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
@implicitNotFound("Implicit instance of RenderableNode[${Component}] not found. If `${Component}` is a custom component that you want to render as a node / element, define an implicit RenderableNode[${Component}] instance for it. For rendering as a string or primitive value, define RenderableText[${Component}] instead, and perhaps use `child.text <--` instead of `child <-- ...`.")
trait RenderableNode[-Component] {

  /** For every component, this MUST ALWAYS return the exact same node reference. */
  def asNode(value: Component): ChildNode.Base

  /** For every component in the sequence, this MUST ALWAYS return the exact same node reference. */
  def asNodeSeq(values: immutable.Seq[Component]): immutable.Seq[ChildNode.Base]

  /** For every component in the sequence, this MUST ALWAYS return the exact same node reference. */
  def asNodeIterable(values: Iterable[Component]): Iterable[ChildNode.Base]

  /** For every component, this MUST ALWAYS return the exact same node reference. */
  def asNodeOption(value: Option[Component]): Option[ChildNode.Base]
}

object RenderableNode {

  /**
   * A `Component` must have a 1-to-1 relationship to a Laminar ChildNode.
   * Your Component class/trait should have something like `val node: ChildNode.Base`
   * or `lazy val node: ChildNode.Base` in it, it must not be a `var` or a `def`.
   */
  def apply[Component](
    renderNode: Component => ChildNode.Base,
    renderNodeSeq: immutable.Seq[Component] => immutable.Seq[ChildNode.Base],
    renderNodeIterable: Iterable[Component] => Iterable[ChildNode.Base],
    renderNodeOption: Option[Component] => Option[ChildNode.Base]
  ): RenderableNode[Component] = new RenderableNode[Component] {

    override def asNode(value: Component): ChildNode.Base = renderNode(value)

    override def asNodeSeq(values: immutable.Seq[Component]): immutable.Seq[ChildNode.Base] = renderNodeSeq(values)

    override def asNodeIterable(values: Iterable[Component]): Iterable[ChildNode.Base] = renderNodeIterable(values)

    override def asNodeOption(value: Option[Component]): Option[ChildNode.Base] = renderNodeOption(value)
  }

  implicit val nodeRenderable: RenderableNode[ChildNode.Base] =
    RenderableNode[ChildNode.Base](identity, identity, identity, identity)

}
