package com.raquo.laminar.modifiers

import com.raquo.laminar.nodes
import com.raquo.laminar.nodes.ParentNode

/** This type represents an operation that has a side effect on a node of type [[El]].
  *
  * For example: `attrs.href := "http://example.com"` is a Modifier that sets the href attribute to an
  * example URL when invoked on an element (typically when the element is mounted, or if the modifier
  * is added after the fact using the `amend` method, or by manually calling its `apply` method).
  *
  * We're defining a specific trait for this because we expect to have implicit conversions into this type.
  */
trait Modifier[-El <: ParentNode.Base] {

  /** You can count on this method being a no-op in your libraries and end user code.
    *
    * The reason this method is not abstract is to avoid broken SAM sugar in case of meta modifiers.
    * See https://github.com/raquo/scala-dom-types/issues/27
    */
  def apply(element: El): Unit = ()
}

object Modifier {

  type Base = Modifier[nodes.ParentNode.Base]

  val empty: Modifier.Base = new Modifier[ParentNode.Base] {}

  def apply[El <: ParentNode.Base](f: El => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = f(element)
    }
  }
}
