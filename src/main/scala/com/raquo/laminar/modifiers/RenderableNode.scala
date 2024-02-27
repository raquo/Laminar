package com.raquo.laminar.modifiers

import com.raquo.ew.JsVector
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
@implicitNotFound("Implicit instance of RenderableNode[${Component}] not found. If `${Component}` is a custom component that you want to render as a node / element, define an implicit RenderableNode[${Component}] instance for it. For rendering as a string or primitive value, define RenderableText[${Component}] instead, and use `text <--` instead of `child <-- ...`.")
trait RenderableNode[-Component] {

  /** For every component, this MUST ALWAYS return the exact same node reference. */
  def asNode(value: Component): ChildNode.Base

  /** For every component in the sequence, this MUST ALWAYS return the exact same node reference. */
  def asNodeSeq(values: immutable.Seq[Component]): immutable.Seq[ChildNode.Base]

  /** For every component in the sequence, this MUST ALWAYS return the exact same node reference. */
  def asNodeJsVector(values: JsVector[Component]): JsVector[ChildNode.Base]

  /** For every component, this MUST ALWAYS return the exact same node reference. */
  def asNodeOption(value: Option[Component]): Option[ChildNode.Base]
}

object RenderableNode {

  /**
   * A `Component` must have a 1-to-1 relationship to a Laminar ChildNode.
   * Your Component class/trait should have something like `val node: ChildNode.Base`
   * or `lazy val node: ChildNode.Base` in it, it must not be a `var` or a `def`.
    *
    * Note: This implementation works for essentially all use cases,
    *       and while it is USUALLY the most efficient, that is not always the case.
    *       For example, `nodeRenderable` below is a special implementation that
    *       avoids mapping over the input collections because that is not needed.
   */
  def apply[Component](
    renderNode: Component => ChildNode.Base
  ): RenderableNode[Component] = new RenderableNode[Component] {

    override def asNode(value: Component): ChildNode.Base = renderNode(value)

    override def asNodeSeq(values: immutable.Seq[Component]): immutable.Seq[ChildNode.Base] = values.map(renderNode)

    override def asNodeJsVector(values: JsVector[Component]): JsVector[ChildNode.Base] = values.map(renderNode)

    override def asNodeOption(value: Option[Component]): Option[ChildNode.Base] = value.map(renderNode)
  }

  /** This low level implementation avoids mapping over collections for maximum efficiency. */
  implicit val nodeRenderable: RenderableNode[ChildNode.Base] = new RenderableNode[ChildNode.Base] {

    override def asNode(value: ChildNode.Base): ChildNode.Base = value

    override def asNodeSeq(values: Seq[ChildNode.Base]): Seq[ChildNode.Base] = values

    override def asNodeJsVector(values: JsVector[ChildNode.Base]): JsVector[ChildNode.Base] = values

    override def asNodeOption(value: Option[ChildNode.Base]): Option[ChildNode.Base] = value
  }

}
