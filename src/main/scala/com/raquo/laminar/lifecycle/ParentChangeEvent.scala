package com.raquo.laminar.lifecycle

import com.raquo.laminar.nodes.ReactiveChildNode.BaseParentNode

case class ParentChangeEvent(
  alreadyChanged: Boolean,
  maybePrevParent: Option[BaseParentNode],
  maybeNextParent: Option[BaseParentNode]
)
