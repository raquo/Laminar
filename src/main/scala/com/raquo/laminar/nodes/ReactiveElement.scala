package com.raquo.laminar.nodes

import com.raquo.dombuilder.jsdom
import org.scalajs.dom

class ReactiveElement[+Ref <: dom.Element](
  override val tagName: String,
  override val void: Boolean = false
)
  extends ReactiveChildNode[Ref]
  with jsdom.nodes.Element[Ref]
  with jsdom.nodes.ParentNode[ReactiveNode, Ref]
  with jsdom.nodes.EventfulNode[ReactiveNode, Ref]
