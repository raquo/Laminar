package com.raquo.laminar.nodes

import org.scalajs.dom

trait ReactiveNode[+Ref <: dom.Node] {

  /** Reference to the real DOM node which this [[ReactiveNode]] represents.
    * Laminar nodes MUST NOT share the same `ref`. This isn't "virtual dom"!
    */
  val ref: Ref
}
