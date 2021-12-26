package com.raquo.laminar.modifiers

import com.raquo.laminar.nodes.ReactiveElement

// #TODO[API] Should Modifier be constrained to El <: ReactiveElement.Base?
//  - That's how it's used in practice, using it differently is pointless
//  - But then we won't be able to write Modifier[_]
//    - But why would we want to? It's a useless type without a bound
//  - So yes, we probably should do that... in a future version. 0.15.0 brings enough breakage as it is

/** This type represents an operation that has a side effect on a node of type [[El]].
  *
  * For example: `attrs.href := "http://example.com"` is a Modifier created using Scala DOM Builder syntax.
  *
  * We're defining a specific trait for this because we expect to have implicit conversions into this type.
  */
trait Modifier[-El] {

  /** You can count on this method being a no-op in your libraries and end user code.
    *
    * The reason this method is not abstract is to avoid broken SAM sugar in case of meta modifiers.
    * See https://github.com/raquo/scala-dom-types/issues/27
    */
  def apply(element: El): Unit = ()
}

object Modifier {

  type Base = Modifier[ReactiveElement.Base]
}
