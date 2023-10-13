package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Transaction
import com.raquo.laminar.nodes.ReactiveElement

/** This type represents an operation that has a side effect on a node of type [[El]].
  *
  * For example: `href := "http://example.com"` is a Modifier that sets the href attribute to an
  * example URL when invoked on an element (typically when the element is created, or if the modifier
  * is added after the fact using the `amend` method, or by manually calling its `apply` method).
  *
  * If you choose to extend this trait, make sure to understand how to use [[Transaction.onStart.shared]].
  * In simple cases, wrapping your callback in it similarly to [[Modifier.apply]] below will probably work.
  * See https://github.com/raquo/Airstream/#restarting-streams-that-depend-on-signals--signalchanges-
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

  /** This type is not public because it is generally useless. Consider `Base` below. */
  private[laminar] type Any = Modifier[_ <: ReactiveElement.Base]

  /** Modifier that is applicable to any element */
  type Base = Modifier[ReactiveElement.Base]

  /** Modifier that does nothing */
  val empty: Modifier.Base = new Modifier[ReactiveElement.Base] {}

  @deprecated("Use Modifier.empty instead of Modifier.noop", "0.15.0-RC1")
  @inline def noop: Modifier.Base = empty

  def apply[El <: ReactiveElement.Base](f: El => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        /** This is a safe default. See scaladoc for [[Transaction.onStart.shared]] */
        Transaction.onStart.shared {
          f(element)
        }
      }
    }
  }
}
