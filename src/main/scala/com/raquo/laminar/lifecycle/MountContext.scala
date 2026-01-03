package com.raquo.laminar.lifecycle

import com.raquo.airstream.ownership.Owner
import com.raquo.laminar.nodes.ReactiveElement

/**
  * MountContext is the context that is provided (at the time of mounting)
  * to child nodes that are being mounted into the parent node.
  *
  * @param thisNode - parent element where this is being mounted.
  * @param owner    - owner that is active until
  */
class MountContext[+El <: ReactiveElement.Base](
  val thisNode: El,
  implicit val owner: Owner
) {

  // @TODO I can't get this to work, unfortunately. Would have been a nice alias.
  // @inline def ref: El#Ref = thisNode.ref
}
