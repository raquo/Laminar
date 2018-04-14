package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement}
import com.raquo.laminar.receivers.MaybeChildReceiver.MaybeChildNode
import org.scalajs.dom

class MaybeChildSetter($maybeNode: Observable[MaybeChildNode])
  extends Modifier[ReactiveElement[dom.Element]] {

  // @TODO[Elegance] Unify this logic with ChildSetter? Or not... Almost the same thing.

  override def apply(parentNode: ReactiveElement[dom.Element]): Unit = {
    val sentinelNode = new ReactiveComment("")
    var childNode: ReactiveChildNode[dom.Node] = sentinelNode

    // @TODO[Performance] In case of memory stream we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)(DomApi.treeApi)
    parentNode.subscribe($maybeNode, onNext(_))

    @inline def onNext(maybeNewChildNode: MaybeChildNode): Unit = {
      val newChildNode = maybeNewChildNode.getOrElse(sentinelNode)
      // @TODO[Performance] Add an "if (childNode != newChildNode)" check? Here or in replaceChild method?
      parentNode.replaceChild(childNode, newChildNode)(DomApi.treeApi)
      childNode = newChildNode
    }
  }
}

