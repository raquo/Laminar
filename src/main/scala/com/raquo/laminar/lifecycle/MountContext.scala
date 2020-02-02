package com.raquo.laminar.lifecycle

import com.raquo.airstream.ownership.Owner
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class MountContext[+El <: ReactiveElement[Ref], +Ref <: dom.Element](
  val thisNode: El,
  implicit val owner: Owner
) {
  @inline def ref: Ref = thisNode.ref
}
