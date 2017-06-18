package com.raquo.laminar.nodes

import com.raquo.dombuilder.jsdom
import org.scalajs.dom

class ReactiveComment(override protected[this] var _text: String)
  extends ReactiveChildNode[dom.Comment]
  with jsdom.nodes.Comment

