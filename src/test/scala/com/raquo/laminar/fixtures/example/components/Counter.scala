package com.raquo.laminar.fixtures.example.components

import com.raquo.laminar.api.L._
import org.scalajs.dom

class Counter private (
  val countSignal: Signal[Int],
  val node: HtmlElement
)

object Counter {
  def apply(label: String): Counter = {
    val incClickBus = new EventBus[dom.MouseEvent]
    val decClickBus = new EventBus[dom.MouseEvent]

    val countSignal = EventStream
      .merge(incClickBus.events.mapTo(1), decClickBus.events.mapTo(-1))
      .scanLeft(initial = 0)(_ + _)
      // .debugWithLabel("$count")

    val node = div(
      className := "Counter",
      button(onClick --> decClickBus.writer, "–"),
      child <-- countSignal.map(count => span(s" :: $count ($label) :: ")),
      button(onClick --> incClickBus.writer, "+")
    )

    new Counter(countSignal, node)
  }
}
