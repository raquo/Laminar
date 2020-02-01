package com.raquo.laminar.static

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

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
    expectNode(div like (ExpectedNode.comment() like "yolo"))
    unmount()
  }

  it("renders elements with text Content") {
    mount("span", span(text1))
    expectNode(span like text1)
    unmount()

    mount("article (fancy element from Tags2)", article(text1))
    expectNode(article like text1)
    unmount()
  }

  it("renders two text nodes") {
    mount(article(text1, text2))
    expectNode(article like (text1, text2))
  }

  it("renders nested elements") {
    mount("div > span", div(span(text1)))
    expectNode(div like (span like text1))
    unmount()

    mount(
      "div > span, p",
      div(span(text1), p(text2))
    )
    expectNode(div like(span like text1, p like text2))
    unmount()

    mount(
      "div > span, p, p",
      div(span(text1), p(text2), p(text3))
    )
    expectNode(div like(span like text1, p like text2, p like text3))
    unmount()

    mount(
      "div > span, (p > #text, span, span), hr",
      div(
        span(text1),
        p(text2, span(text2), span(text3)),
        hr()
      )
    )

    expectNode(div like(
      span like text1,
      p like (text2, span like text2, span like text3),
      hr
    ))
    unmount()
  }
}
