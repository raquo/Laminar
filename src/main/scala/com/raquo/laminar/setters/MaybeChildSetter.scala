package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ReactiveElement}
import com.raquo.laminar.setters.ChildrenSetter.Child

class MaybeChildSetter(maybeNodeObservable: Observable[Option[Child]])
  extends Modifier[ReactiveElement.Base] {

  // @TODO[Elegance] Unify this logic with ChildSetter? Or not... Almost the same thing.

  override def apply(parentNode: ReactiveElement.Base): Unit = {
    val sentinelNode = new CommentNode("")
    var childNode: ChildNode.Base = sentinelNode

    // @TODO[Performance] In case of Signal we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)
    parentNode.subscribe(maybeNodeObservable)(onNext)

    @inline def onNext(maybeNewChildNode: Option[Child]): Unit = {
      val newChildNode = maybeNewChildNode.getOrElse(sentinelNode)
      // @TODO[Performance] Add an "if (childNode != newChildNode)" check? Here or in replaceChild method?
      parentNode.replaceChild(childNode, newChildNode)
      childNode = newChildNode
    }
  }
}

