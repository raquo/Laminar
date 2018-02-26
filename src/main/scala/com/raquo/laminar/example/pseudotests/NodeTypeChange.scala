package com.raquo.laminar.example.pseudotests

import com.raquo.laminar.bundle._
import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.experimental.airstream.signal.Signal
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

object NodeTypeChange {

  def boldOrItalic(
    $useB: EventStream[Boolean],
    $bigFont: Signal[Boolean]
  ): EventStream[ReactiveElement[dom.Element]] = {
    val $fontSize = fontSizeStream($bigFont) // @TODO use remember()?
    $useB.map { useB =>
      dom.console.warn("useB: " + useB)
      if (useB) {
        b(
          "B",
          fontSize <-- $fontSize
        )
      } else {
        i(
          fontSize <-- $fontSize,
          "I"
        )
      }
    }
  }

  def fontSizeStream($big: Signal[Boolean]): Signal[String] = {
    $big.map(ok => if (ok) {
      "45px"
    } else {
      "30px"
    })
  }

  def apply(): ReactiveElement[dom.Element] = {

    val toggle = Toggle("Bold")
    val toggle2 = Toggle("Big")
    //    val counter = Counter()
    div(
      "APP",
      div(
        toggle.node,
        toggle2.node,
        child <-- boldOrItalic($useB = toggle.$checked, $bigFont = toggle2.$checked.toSignal(false))
        //        div(
        //          color <-- myColor(toggle.$checked),
        //          b("COLOR")
        //        )
        //        counter.vNode
      )
    )
  }
}
