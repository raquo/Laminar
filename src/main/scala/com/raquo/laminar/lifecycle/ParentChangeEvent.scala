package com.raquo.laminar.lifecycle

import com.raquo.laminar.nodes.ParentNode

case class ParentChangeEvent(
  alreadyChanged: Boolean,
  maybePrevParent: Option[ParentNode.Base],
  maybeNextParent: Option[ParentNode.Base]
)
