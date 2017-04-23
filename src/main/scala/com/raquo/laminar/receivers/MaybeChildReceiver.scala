package com.raquo.laminar.receivers

import com.raquo.dombuilder.modifiers.Modifier
import com.raquo.laminar
import com.raquo.laminar.ChildNode
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.xstream.XStream

class MaybeChildReceiver($maybeNode: XStream[MaybeChildReceiver.MaybeChildNode]) extends Modifier[ReactiveElement] {

  // @TODO[Elegance] Unify this logic with ChildReceiver? Or not...

  override def applyTo(parentNode: ReactiveElement): Unit = {
    val sentinelNode = laminar.commentBuilder.createNode()
    var childNode: ChildNode = sentinelNode

    // @TODO[Performance] In case of memory stream we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)
    parentNode.subscribe($maybeNode, onNext)

    @inline def onNext(maybeNewChildNode: MaybeChildReceiver.MaybeChildNode): Unit = {
      val newChildNode = maybeNewChildNode.getOrElse(sentinelNode)
      parentNode.replaceChild(childNode, newChildNode)
      childNode = newChildNode
    }
  }
}

object MaybeChildReceiver {

  type MaybeChildNode = Option[ChildNode]

  def <--($maybeNode: XStream[MaybeChildReceiver.MaybeChildNode]): MaybeChildReceiver = {
    new MaybeChildReceiver($maybeNode)
  }
}
