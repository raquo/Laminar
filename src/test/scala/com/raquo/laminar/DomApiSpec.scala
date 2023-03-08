package com.raquo.laminar

import com.raquo.laminar.api.L.{svg => s}
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class DomApiSpec extends UnitSpec {

  it("HTML: parses") {
    expectNode(
      DomApi.unsafeParseHtmlString("<div class='foo bar'>Hello <b>world</b></div>"),
      div of (
        className is "foo bar",
        "Hello ",
        b of "world"
      )
    )
  }

  it("HTML: parses [div]") {
    expectNode(
      DomApi.unsafeParseHtmlString(div, "<div class='foo bar'>Hello <b>world</b></div>"),
      div of (
        className is "foo bar",
        "Hello ",
        b of "world"
      )
    )
  }

  it("HTML: fails on multiple elements") {
    val caught = intercept[Exception] {
      DomApi.unsafeParseHtmlString(div, "<div class='foo bar'>Hello <b>world</b></div><span></span")
    }
    assert(caught.getMessage == "Error parsing HTML string: expected exactly 1 element, got 2")
  }

  it("HTML: fails on incorrect tag name when expected tag is provided") {
    val caught = intercept[Exception] {
      DomApi.unsafeParseHtmlString(span, "<div class='foo bar'>Hello <b>world</b></div>")
    }
    assert(caught.getMessage == "Error parsing HTML string: expected tag name `span`, got `DIV`")
  }


  it("SVG: parses SVG tag") {
    expectNode(
      DomApi.unsafeParseSvgString("<svg height=\"200\" width='400'><circle cx='200' cy='15' r='30' fill='red'></circle></svg>"),
      s.svg of (
        s.height is "200",
        s.width is "400",
        s.circle of (
          s.cx is "200",
          s.cy is "15",
          s.r is "30",
          s.fill is "red"
        )
      )
    )
  }

  it("SVG: parses CIRCLE tag") {
    expectNode(
      DomApi.unsafeParseSvgString("<circle cx='200' cy='15' r='30' fill='red'></circle>"),
      s.circle of (
        s.cx is "200",
        s.cy is "15",
        s.r is "30",
        s.fill is "red"
      )
    )
  }

  it("SVG: parses CIRCLE tag [circle]") {
    expectNode(
      DomApi.unsafeParseSvgString(s.circle, "<circle cx='200' cy='15' r='30' fill='red'></circle>"),
      s.circle of (
        s.cx is "200",
        s.cy is "15",
        s.r is "30",
        s.fill is "red"
      )
    )
  }

  it("SVG: fails on multiple elements") {
    val caught = intercept[Exception] {
      DomApi.unsafeParseSvgString(s.svg, "<svg height=\"200\" width='400'></svg><circle cx='200' cy='15' r='30' fill='red'></circle>")
    }
    assert(caught.getMessage == "Error parsing SVG string: expected exactly 1 element, got 2")
  }

  it("SVG: fails on incorrect tag name when expected tag is provided") {
    val caught = intercept[Exception] {
      DomApi.unsafeParseSvgString(s.svg, "<circle cx='200' cy='15' r='30' fill='red'></circle>")
    }
    assert(caught.getMessage == "Error parsing SVG string: expected tag name `svg`, got `circle`")
  }

}
