package com.raquo.laminar

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.setters.{Style, StyleSetter}
import com.raquo.xstream.XStream

class StyleReceiver[V](val style: Style[V, RNode, RNodeData]) extends AnyVal {

  def <--($value: XStream[V]): Modifier[RNode, RNodeData] = {
    new Modifier[RNode, RNodeData] {
      override def applyTo(node: RNode): Unit = {
        node.registerSubscriptionRequest(
          $value,
          onNext = (value: V, newNode: RNode) => {
            new StyleSetter(style, value).applyTo(newNode)
          }
        )
      }
    }
  }
}
