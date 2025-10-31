package com.raquo.laminar.nodes

import com.raquo.laminar.keys.SimpleKey
import com.raquo.laminar.tags.MathMlTag
import org.scalajs.dom

class ReactiveMathMlElement(
  override val tag: MathMlTag,
  final override val ref: dom.MathMLElement
) extends ReactiveElement[dom.MathMLElement] {

  override private[laminar] def onBoundKeyUpdater(key: SimpleKey[_, _, _]): Unit = ()

  override def toString: String = {
    // `ref` is not available inside ReactiveElement's constructor due to initialization order, so fall back to `tag`.
    s"ReactiveMathMlElement(${if (ref != null) ref.outerHTML else s"tag=${tag.name}"})"
  }
}
