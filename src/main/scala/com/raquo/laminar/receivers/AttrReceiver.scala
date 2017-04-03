package com.raquo.laminar.receivers

import com.raquo.laminar.{RNode, RNodeData}
import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.setters.{Attr, AttrSetter}
import com.raquo.xstream.XStream

class AttrReceiver[V](val attr: Attr[V, RNode, RNodeData]) extends AnyVal {

  def <--($value: XStream[V]): Modifier[RNode, RNodeData] = {
    new Modifier[RNode, RNodeData] {
      override def applyTo(node: RNode): Unit = {
        node.subscribe(
          $value,
          onNext = (value: V, activeNode: RNode) => {
            val attrSetter = new AttrSetter(attr, value)
            if (!activeNode.isMounted) {
              attrSetter.applyTo(activeNode)
              activeNode
            } else {
              val newNode = activeNode.copy()
              attrSetter.applyTo(newNode)
              newNode
            }
          }
        )
      }
    }
  }
}
