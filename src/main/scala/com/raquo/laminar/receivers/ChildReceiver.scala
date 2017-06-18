package com.raquo.laminar.receivers

import com.raquo.dombuilder.generic.modifiers.Modifier
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement}
import com.raquo.xstream.XStream
import org.scalajs.dom

class ChildReceiver($node: XStream[ReactiveChildNode[dom.Node]])
  extends Modifier[ReactiveElement[dom.Element]] {

  override def applyTo(parentNode: ReactiveElement[dom.Element]): Unit = {
    var childNode: ReactiveChildNode[dom.Node] = new ReactiveComment("")

    // @TODO[Performance] In case of memory stream we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)
    parentNode.subscribe($node, onNext)

    @inline def onNext(newChildNode: ReactiveChildNode[dom.Node]): Unit = {
      parentNode.replaceChild(childNode, newChildNode)
      childNode = newChildNode
    }
  }
}

object ChildReceiver {

  def <--($node: XStream[ReactiveChildNode[dom.Node]]): ChildReceiver = {
    new ChildReceiver($node)
  }
}
