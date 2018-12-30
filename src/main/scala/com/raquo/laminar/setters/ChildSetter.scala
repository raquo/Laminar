package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement}
import org.scalajs.dom

class ChildSetter(nodeObservable: Observable[ReactiveChildNode[dom.Node]])
  extends Modifier[ReactiveElement[dom.Element]] {

  override def apply(parentNode: ReactiveElement[dom.Element]): Unit = {
    var childNode: ReactiveChildNode[dom.Node] = new ReactiveComment("")

    // @TODO[Performance] In case of Signal we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)(DomApi.treeApi)
    parentNode.subscribe(nodeObservable)(onNext)

    @inline def onNext(newChildNode: ReactiveChildNode[dom.Node]): Unit = {
      parentNode.replaceChild(childNode, newChildNode)(DomApi.treeApi)
      childNode = newChildNode
    }
  }
}
