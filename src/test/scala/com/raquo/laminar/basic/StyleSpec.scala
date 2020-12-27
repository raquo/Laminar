package com.raquo.laminar.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

import scala.util.Random

class StyleSpec extends UnitSpec {

  it("sets styles") {
    val expectedDisplay = "block"
    val expectedHeight = s"${1 + Random.nextInt(15)}px"
    val expectedWidth = s"${15 + Random.nextInt(7)}px"

    mount("div", div(display.block))
    expectNode(div.of(display is expectedDisplay))
    unmount()

    mount("div [width, height]", div(
      width := expectedWidth,
      height := expectedHeight
    ))
    expectNode(
      div.of(
        width is expectedWidth,
        height is expectedHeight
      )
    )
    unmount()
  }
}
