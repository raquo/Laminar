package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.{ReactiveComment, ReactiveElement}
import com.raquo.laminar.setters.ChildrenSetter.Child
import org.scalajs.dom

class ChildSetter(nodeObservable: Observable[Child])
  extends Modifier[ReactiveElement[dom.Element]] {

  override def apply(parentNode: ReactiveElement[dom.Element]): Unit = {
    var childNode: Child = new ReactiveComment("")

    // @TODO[Performance] In case of Signal we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)(DomApi.treeApi)
    parentNode.subscribe(nodeObservable)(onNext)

    @inline def onNext(newChildNode: Child): Unit = {
      parentNode.replaceChild(childNode, newChildNode)(DomApi.treeApi)
      childNode = newChildNode
    }
  }
}
