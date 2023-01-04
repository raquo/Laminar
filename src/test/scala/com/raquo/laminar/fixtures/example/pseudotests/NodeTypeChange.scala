package com.raquo.laminar.fixtures.example.pseudotests

import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.laminar.api._
import com.raquo.laminar.fixtures.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

object NodeTypeChange {

  def boldOrItalic(
    useBStream: EventStream[Boolean],
    bigFontSignal: Signal[Boolean]
  ): EventStream[ReactiveElement.Base] = {
    val fontSizeSignal = fontSizeStream(bigFontSignal) // @TODO use remember()?
    useBStream.map { useB =>
      dom.console.warn("useB: " + useB)
      if (useB) {
        L.b(
          "B",
          L.fontSize <-- fontSizeSignal
        )
      } else {
        L.i(
          L.fontSize <-- fontSizeSignal,
          "I"
        )
      }
    }
  }

  def fontSizeStream(isBig: Signal[Boolean]): Signal[String] = {
    isBig.map(big => if (big) {
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
        L.child <-- boldOrItalic(useBStream = toggle.checkedStream, bigFontSignal = toggle2.checkedStream.toSignal(false))
        //        div(
        //          color <-- myColor(toggle.checkedStream),
        //          b("COLOR")
        //        )
        //        counter.vNode
      )
    )
  }
}
