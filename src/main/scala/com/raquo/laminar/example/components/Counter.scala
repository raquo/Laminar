package com.raquo.laminar.example.components

import com.raquo.laminar._
import com.raquo.snabbdom.VNode
import com.raquo.snabbdom.attrs.cls
import com.raquo.snabbdom.events.onClick
import com.raquo.snabbdom.tags.{button, div, span}
import com.raquo.xstream.{MemoryStream, XStream}
import org.scalajs.dom.raw.MouseEvent

class Counter private (
  val $count: MemoryStream[Int, Nothing],
  val vNode: VNode
)

object Counter {
  def apply(): Counter = {
    val $incClick = XStream.create[MouseEvent]()
    val $decClick = XStream.create[MouseEvent]()
    val $count = XStream
      .merge($incClick.mapTo(1), $decClick.mapTo(-1))
      .fold((a: Int, b: Int) => a + b, seed = 0)
      .debugWithLabel("$count")

    val vNode = div(
      cls := "Counter",
      button(onClick --> $decClick, "–"),
      $count.map(count => span(s" :: $count :: ")),
      button(onClick --> $incClick, "+")
    )

    new Counter($count, vNode)
  }
}
