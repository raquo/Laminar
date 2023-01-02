package com.raquo.laminar.modifiers

import com.raquo.laminar.nodes.ReactiveElement

/** This type represents an operation that has a side effect on a node of type [[El]].
  *
  * For example: `attrs.href := "http://example.com"` is a Modifier that sets the href attribute to an
  * example URL when invoked on an element (typically when the element is mounted, or if the modifier
  * is added after the fact using the `amend` method, or by manually calling its `apply` method).
  *
  * We're defining a specific trait for this because we expect to have implicit conversions into this type.
  */
trait Modifier[-El <: ReactiveElement.Base] {

  /** You can count on this method being a no-op in your libraries and end user code.
    *
    * The reason this method is not abstract is to avoid broken SAM sugar in case of meta modifiers.
    * See https://github.com/raquo/scala-dom-types/issues/27
    */
  def apply(element: El): Unit = ()
}

object Modifier {

  // #TODO[API] Should there be a `Modifier.Base` type alias similar to other such type aliases?
  //  - The problem is, `Modifier.Base` is not a supertype of all modifiers. Not sure if it's obvious enough.
  // type Base = Modifier[ReactiveElement.Base]

  type Any = Modifier[_ <: ReactiveElement.Base]

  val empty: Modifier[ReactiveElement.Base] = new Modifier[ReactiveElement.Base] {}

  def apply[El <: ReactiveElement.Base](f: El => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = f(element)
    }
  }
}
