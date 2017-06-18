package com.raquo.laminar.nodes

import com.raquo.dombuilder.jsdom
import org.scalajs.dom

class ReactiveText(override protected[this] var _text: String)
  extends ReactiveNode
  with jsdom.nodes.Text
  with jsdom.nodes.ChildNode[ReactiveNode, dom.Text]

