package com.raquo.laminar.fixtures.example.components

import com.raquo.laminar.api.L._
import org.scalajs.dom

class Counter private (
  val $count: Signal[Int],
  val node: HtmlElement
)

object Counter {
  def apply(label: String): Counter = {
    val incClickBus = new EventBus[dom.MouseEvent]
    val decClickBus = new EventBus[dom.MouseEvent]

    val $count = EventStream
      .merge(incClickBus.events.mapTo(1), decClickBus.events.mapTo(-1))
      .fold(initial = 0)(_ + _)
      // .debugWithLabel("$count")

    val node = div(
      className := "Counter",
      button(onClick --> decClickBus.writer, "–"),
      child <-- $count.map(count => span(s" :: $count ($label) :: ")),
      button(onClick --> incClickBus.writer, "+")
    )

    new Counter($count, node)
  }
}
