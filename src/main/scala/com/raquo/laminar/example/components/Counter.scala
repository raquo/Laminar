package com.raquo.laminar.example.components

import com.raquo.laminar._
import com.raquo.laminar.attrs.cls
import com.raquo.laminar.events.onClick
import com.raquo.laminar.tags.{button, div, span}
import com.raquo.xstream.{MemoryStream, XStream}
import org.scalajs.dom.raw.MouseEvent

class Counter private (
  val $count: MemoryStream[Int, Nothing],
  val node: RNode
)

object Counter {
  def apply(): Counter = {
    val $incClick = XStream.create[MouseEvent]()
    val $decClick = XStream.create[MouseEvent]()
    val $count = XStream
      .merge($incClick.mapTo(1), $decClick.mapTo(-1))
      .fold((a: Int, b: Int) => a + b, seed = 0)
      .debugWithLabel("$count")

    val node = div(
      cls := "Counter"
    )(
      button(onClick --> $decClick, "–"),
      child <-- $count.map(count => span(s" :: $count :: ")),
      button(onClick --> $incClick, "+")
    )

    new Counter($count, node)
  }
}
