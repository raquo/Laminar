package com.raquo.laminar.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

import scala.util.Random

class StyleSpec extends UnitSpec {

  it("sets static styles") {
    val expectedHeight = s"${1 + Random.nextInt(15)}px"
    val expectedWidth = s"${15 + Random.nextInt(7)}px"

    assert(display.block.value == "block")
    assert(display.inline.value == "inline")

    mount("div [display.block]", div(display.block))
    expectNode(div.of(display is "block"))
    unmount()

    mount("div [display := inline]", div(display := "inline"))
    expectNode(div.of(display is "inline"))
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

  it("sets derived styles") {
    val expectedHeightNum = 1 + Random.nextInt(15)
    val expectedWidthNum = 15 + Random.nextInt(7)
    val expectedHeight = s"${expectedHeightNum}px"
    val expectedWidth = s"${expectedWidthNum}px"

    mount("span [width.px, height.px] as int", span(
      width.px := expectedWidthNum,
      height.px := expectedHeightNum
    ))
    expectNode(
      span.of(
        width is expectedWidth,
        height is expectedHeight
      )
    )
    unmount()

    mount("span [width.px, height.px] as double", span(
      width.px := expectedWidthNum.toDouble,
      height.px := expectedHeightNum.toDouble
    ))
    expectNode(
      span.of(
        width is expectedWidth,
        height is expectedHeight
      )
    )
    unmount()

    // #TODO[Test] jsdom seems to behave differently from browsers when
    //  it comes to URLs (especially those with special characters that
    //  need escaping), so I can't test this fully here

    assertEquals(style.url("""example'.com"""), """url("example'.com")""")
    assertEquals(style.url("""example".com"""), """url("example%22.com")""")
    assertEquals(style.url("""exa\mple.com\"""), """url("exa%5Cmple.com%5C")""")

    mount("span [backgroundImage.url]", span(
      backgroundImage.url := "example.jpg",
    ))
    expectNode(
      span.of(
        backgroundImage is """url(example.jpg)"""
      )
    )
    unmount()
  }

  // #TODO[Test] Safari seemed to work in practice, but jsdom does not seem to support prefixes
  //it("sets styles with prefixes") {
  //
  //  val el = span(
  //    backgroundClip.withPrefixes(_.webkit) := "text",
  //  )
  //
  //  mount("opacity", el)
  //  expectNode(
  //    span.of(
  //      backgroundClip is "text",
  //      customStyle("-webkit-background-clip") is "text",
  //      customStyle("-moz-background-clip") is "text"
  //    )
  //  )
  //}
}
