package com.raquo.laminar.example.pseudotests

import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar._
import com.raquo.snabbdom.VNode
import com.raquo.snabbdom.tags._
import com.raquo.snabbdom.styles._
import com.raquo.xstream.XStream
import org.scalajs.dom

object NodeTypeChange {

  def boldOrItalic($useB: XStream[Boolean], $bigFont: XStream[Boolean]): XStream[VNode] = {
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

  def fontSizeStream($big: XStream[Boolean]): XStream[String] = {
    $big.map(ok => if (ok) {
      "45px"
    } else {
      "30px"
    })
  }

  def apply(): VNode = {

    val toggle = Toggle("Bold")
    val toggle2 = Toggle("Big")
    //    val counter = Counter()
    div(
      "APP",
      div(
        toggle.vnode,
        toggle2.vnode,
        child <-- boldOrItalic($useB = toggle.$checked, $bigFont = toggle2.$checked.remember())
        //        div(
        //          color <-- myColor(toggle.$checked),
        //          b("COLOR")
        //        )
        //        counter.vNode
      )
    )
  }
}
