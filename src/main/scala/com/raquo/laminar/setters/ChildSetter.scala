package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}
import com.raquo.laminar.setters.ChildrenSetter.Child

class ChildSetter(nodeObservable: Observable[Child])
  extends Modifier[ReactiveElement.Base] {

  override def apply(parentNode: ReactiveElement.Base): Unit = {
    var childNode: Child = new CommentNode("")

    // @TODO[Performance] In case of Signal we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)
    parentNode.subscribe(nodeObservable)(onNext)

    @inline def onNext(newChildNode: Child): Unit = {
      parentNode.replaceChild(childNode, newChildNode)
      childNode = newChildNode
    }
  }
}
