package com.raquo.laminar.example.components

import com.raquo.laminar._
import com.raquo.laminar.attrs.cls
import com.raquo.laminar.events.onClick
import com.raquo.laminar.nodes.ReactiveNode
import com.raquo.laminar.tags.{button, div, span}
import com.raquo.xstream.{MemoryStream, XStream}
import org.scalajs.dom

class Counter private (
  val $count: MemoryStream[Int],
  val node: ReactiveNode
)

object Counter {
  def apply(label: String): Counter = {
    val $incClick = XStream.create[dom.MouseEvent]()
    val $decClick = XStream.create[dom.MouseEvent]()

    val $count = XStream
      .merge($incClick.mapTo(1), $decClick.mapTo(-1))
      .fold((a: Int, b: Int) => a + b, seed = 0)
      .debugWithLabel("$count")

    val node = div(
      cls := "Counter",
      button(onClick --> $decClick, "–"),
      child <-- $count.map(count => span(s" :: $count ($label) :: ")),
      button(onClick --> $incClick, "+")
    )

    new Counter($count, node)
  }
}
