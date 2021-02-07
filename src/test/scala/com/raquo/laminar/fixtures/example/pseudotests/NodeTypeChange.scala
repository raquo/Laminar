package com.raquo.laminar.fixtures.example.pseudotests

import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.laminar.api._
import com.raquo.laminar.fixtures.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

object NodeTypeChange {

  def boldOrItalic(
    $useB: EventStream[Boolean],
    $bigFont: Signal[Boolean]
  ): EventStream[ReactiveElement.Base] = {
    val $fontSize = fontSizeStream($bigFont) // @TODO use remember()?
    $useB.map { useB =>
      dom.console.warn("useB: " + useB)
      if (useB) {
        L.b(
          "B",
          L.fontSize <-- $fontSize
        )
      } else {
        L.i(
          L.fontSize <-- $fontSize,
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

  def apply(): ReactiveElement.Base = {

    val toggle = Toggle("Bold")
    val toggle2 = Toggle("Big")
    //    val counter = Counter()
    L.div(
      "APP",
      L.div(
        toggle.node,
        toggle2.node,
        L.child <-- boldOrItalic($useB = toggle.$checked, $bigFont = toggle2.$checked.toSignal(false))
        //        div(
        //          color <-- myColor(toggle.$checked),
        //          b("COLOR")
        //        )
        //        counter.vNode
      )
    )
  }
}
