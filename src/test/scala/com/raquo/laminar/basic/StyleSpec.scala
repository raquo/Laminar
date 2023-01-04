package com.raquo.laminar.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.keys.DerivedStyleBuilder
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

  it("encoding of CSS values") {

    // Expose methods to the public so that we can test them
    class TestableBuilder extends DerivedStyleBuilder[String, StyleEncoder] {

      override protected def styleSetter(value: String): String = value

      override protected def derivedStyle[A](encode: A => String): StyleEncoder[A] = {
        new StyleEncoder[A] {
          override def apply(v: A): String = encode(v)
        }
      }

      override def encodeCalcValue(exp: String): String = super.encodeCalcValue(exp)

      override def encodeUrlValue(url: String): String = super.encodeUrlValue(url)
    }

    assertEquals(
      style.calc("100px + 50vh * 2%"),
      "calc(100px + 50vh * 2%)"
    )

    assertEquals(
      style.url("https://example.com\"\\\n); evil"),
      "url(\"https://example.com%22%5C ); evil\")"
    )

    val x = new TestableBuilder
    assertEquals(
      x.encodeCalcValue("20px + 50%"),
      "20px + 50%"
    )

    assertEquals(
      x.encodeCalcValue("20px + 50%; \"evil"),
      "20px + 50%   evil"
    )

    assertEquals(
      x.encodeUrlValue("https://example.com?q=test&foo-bar"),
      "\"https://example.com?q=test&foo-bar\""
    )

    assertEquals(
      x.encodeUrlValue("https://example.com\"); evil"),
      "\"https://example.com%22); evil\""
    )

    assertEquals(
      x.encodeUrlValue("https://example.com\"\\\n); evil"),
      "\"https://example.com%22%5C ); evil\""
    )
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
