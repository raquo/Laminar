package com.raquo.laminar.basic

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.{svg => s}
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

class ElementSpec extends UnitSpec {

  val text1 = randomString("text1_")
  val text2 = randomString("text2_")
  val text3 = randomString("text3_")

  it("renders empty elements") {
    mount("empty <div>", div())
    expectNode(div)
    unmount()

    mount("empty <span>", span())
    expectNode(span)
    unmount()

    mount("empty <p>", p())
    expectNode(p)
    unmount()

    mount("empty <hr>", hr())
    expectNode(hr)
    unmount()
  }

  it("renders a comment") {
    mount(div(commentNode("yolo")), "")
    expectNode(div.of(ExpectedNode.comment of "yolo"))
    unmount()
  }

  it("renders elements with text Content") {
    mount("span", span(text1))
    expectNode(span of text1)
    unmount()

    mount("article (fancy element from Tags2)", articleTag(text1))
    expectNode(articleTag of text1)
    unmount()
  }

  it("renders two text nodes") {
    mount(articleTag(text1, text2))
    expectNode(articleTag.of(text1, text2))
  }

  it("renders nested elements") {
    mount("div > span", div(span(text1)))
    expectNode(div.of(span of text1))
    unmount()

    mount(
      "div > span, p",
      div(span(text1), p(text2))
    )
    expectNode(div.of(span of text1, p of text2))
    unmount()

    mount(
      "div > span, p, p",
      div(span(text1), p(text2), p(text3))
    )
    expectNode(div.of(span of text1, p of text2, p of text3))
    unmount()

    mount(
      "div > span, (p > #text, span, span), hr",
      div(
        span(text1),
        p(text2, span(text2), span(text3)),
        hr()
      )
    )

    expectNode(div.of(
      span of text1,
      p.of(text2, span of text2, span of text3),
      hr
    ))
    unmount()
  }

  it("renders foreign HTML elements") {
    mount(
      div(
        b("Hello"),
        foreignHtmlElement(span, DomApi.unsafeParseHtmlString(span, "<span class='foo'>world</span>")),
        foreignHtmlElement(DomApi.unsafeParseHtmlString("<span class='bar'>sun</span>")),
        " Eh"
      )
    )

    expectNode(
      div of (
        b of "Hello",
        span of (
          cls is "foo",
          "world"
        ),
        span of(
          cls is "bar",
          "sun"
        ),
        " Eh"
      )
    )
  }

  it("renders foreign SVG root elements") {
    mount(
      div(
        b("Hello"),
        foreignSvgElement(DomApi.unsafeParseSvgString("<svg height=\"200\" width='400'><circle cx='200' cy='15' r='30' fill='red'></circle></svg>")),
        " Eh"
      )
    )

    expectNode(
      div of (
        b of "Hello",
        s.svg of (
          s.height is "200",
          s.width is "400",
          s.circle of (
            s.cx is "200",
            s.cy is "15",
            s.r is "30",
            s.fill is "red"
          )
        ),
        " Eh"
      )
    )
  }

  it("renders foreign SVG sub-elements") {
    mount(
      div(
        b("Hello"),
        s.svg(
          s.height("200"),
          s.width("400"),
          foreignSvgElement(svg.circle, DomApi.unsafeParseSvgString(svg.circle, "<circle cx='200' cy='15' r='30' fill='red'></circle>")),
          foreignSvgElement(DomApi.unsafeParseSvgString("<circle cx='2000' cy='150' r='300' fill='blue'></circle>"))
        ),
        " Eh"
      )
    )

    expectNode(
      div of (
        b of "Hello",
        s.svg of (
          s.height is "200",
          s.width is "400",
          s.circle of (
            s.cx is "200",
            s.cy is "15",
            s.r is "30",
            s.fill is "red"
          ),
          s.circle of(
            s.cx is "2000",
            s.cy is "150",
            s.r is "300",
            s.fill is "blue"
          )
        ),
        " Eh"
      )
    )
  }
}
