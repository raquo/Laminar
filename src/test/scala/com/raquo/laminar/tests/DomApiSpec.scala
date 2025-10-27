package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.api.L.{svg => s}
import com.raquo.laminar.domapi.DomApi
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

  it("jsTagName: matches case for both HTML and SVG elements") {
    a.jsTagName == a().ref.tagName
    div.jsTagName == div().ref.tagName
    svg.svg.jsTagName == svg.svg().ref.tagName
    svg.circle.jsTagName == svg.circle().ref.tagName
  }

  describe("deletes html props") {

    it("indeterminate") {
      val el = input(
        typ("checkbox"),
        indeterminate(true)
      )

      mount(el)

      expectNode(input of (
        typ is "checkbox",
        indeterminate is true
      ))

      DomApi.setHtmlProperty(el, indeterminate, null)

      expectNode(input of (
        typ is "checkbox",
        indeterminate is false
      ))
    }

    it("checked & defaultChecked") {
      val el = input(
        typ("checkbox"),
        defaultChecked(true),
        checked(true)
      )

      mount(el)

      expectNode(input of (
        typ is "checkbox",
        defaultChecked is true,
        checked is true
      ))

      DomApi.setHtmlProperty(el, checked, null)

      expectNode(input of (
        typ is "checkbox",
        defaultChecked is true,
        checked is false
      ))

      DomApi.setHtmlProperty(el, defaultChecked, null)

      expectNode(input of (
        typ is "checkbox",
        defaultChecked is false,
        checked is false
      ))

      assertEquals(el.ref.hasAttribute("checked"), false)
    }

    it("value") {

      val el = input(
        typ("input"),
        defaultValue("default"),
        value("hello")
      )

      mount(el)

      expectNode(input of (
        typ is "input",
        defaultValue is "default",
        value is "hello"
      ))

      DomApi.setHtmlProperty(el, value, null)

      expectNode(input of (
        typ is "input",
        defaultValue is "default",
        value.isEmpty
      ))

      assertEquals(el.ref.value, "") // just to be sure that we'll read the default empty strnig value
    }

    it("select.value & option.selected") {

      val options = List(
        option(value("v1"), "V1"),
        option(value("v2"), "V2"),
        option(value("v3"), "V3")
      )

      val el = select(
        options,
        value("v2")
      )

      mount(el)

      expectNode(select of (
        option of (value is "v1", selected is false, "V1"),
        option of (value is "v2", selected is true, "V2"),
        option of (value is "v3", selected is false, "V3")
      ))

      DomApi.setHtmlProperty(el, value, null)

      expectNode(select of (
        option of (value is "v1", selected is false, "V1"),
        option of (value is "v2", selected is false, "V2"),
        option of (value is "v3", selected is false, "V3")
      ))

      DomApi.setHtmlProperty(options(2), selected, true)

      expectNode(select of (
        option of (value is "v1", selected is false, "V1"),
        option of (value is "v2", selected is false, "V2"),
        option of (value is "v3", selected is true, "V3")
      ))

      // #TODO[Test] The below appears to work in a real browser, but JSDOM does not simulate it correctly

      // DomApi.unsetHtmlProperty(options(2), selected)
      //
      // expectNode(select of(
      //   option of (value is "v1", selected is false, "V1"),
      //   option of (value is "v2", selected is false, "V2"),
      //   option of (value is "v3", selected is false, "V3")
      // ))
    }
  }
}
