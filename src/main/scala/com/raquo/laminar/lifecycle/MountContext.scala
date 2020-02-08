package com.raquo.laminar.lifecycle

import com.raquo.airstream.ownership.Owner
import com.raquo.laminar.nodes.ReactiveElement

class MountContext[+El <: ReactiveElement.Base](
  val thisNode: El,
  implicit val owner: Owner
) {

  // @TODO I can't get this to work, unfortunately. Would have been a nice alias.
  // @inline def ref: El#Ref = thisNode.ref
}
