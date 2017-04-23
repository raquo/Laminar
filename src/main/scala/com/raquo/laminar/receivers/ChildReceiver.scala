package com.raquo.laminar.receivers

import com.raquo.dombuilder.modifiers.Modifier
import com.raquo.laminar
import com.raquo.laminar.ChildNode
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.xstream.XStream

class ChildReceiver($node: XStream[ChildNode]) extends Modifier[ReactiveElement] {

  override def applyTo(parentNode: ReactiveElement): Unit = {
    var childNode: ChildNode = laminar.commentBuilder.createNode()

    // @TODO[Performance] In case of memory stream we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)
    parentNode.subscribe($node, onNext)

    @inline def onNext(newChildNode: ChildNode): Unit = {
      parentNode.replaceChild(childNode, newChildNode)
      childNode = newChildNode
    }
  }
}

object ChildReceiver {

  def <--($node: XStream[ChildNode]): ChildReceiver = {
    new ChildReceiver($node)
  }
}
