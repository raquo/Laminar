package com.raquo.laminar.tests.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

import scala.util.Random

class ReflectedAttrSpec extends UnitSpec {

  it("sets reflected attrs") {
    val expectedRel = randomString("rel_")
    val expectedHref = randomString("href_")
    val expectedAlt = randomString("alt_")

    mount("div", div(rel := expectedRel))
    expectNode(div.of(rel is expectedRel))
    unmount()

    mount(
      "td [colSpan, rowSpan]",
      td(
        href := expectedHref,
        alt := expectedAlt
      )
    )
    expectNode(
      td.of(
        href is expectedHref,
        alt is expectedAlt,
        rel.isEmpty
      )
    )
    unmount()
  }

  it("sets non-string reflected attrs") {
    mount("input [disabled=false]", input(disabled := false))
    expectNode(input.of(disabled is false, colSpan.isEmpty))
    unmount()

    mount("input [disabled=true]", input(disabled := true))
    expectNode(input.of(disabled is true, colSpan.isEmpty))
    unmount()

    val expectedColSpan = 1 + Random.nextInt(10)
    mount("td [colSpan]", td(colSpan := expectedColSpan))
    expectNode(td.of(colSpan is expectedColSpan, disabled.isEmpty))
    unmount()
  }

  it("order of modifiers does not matter") {
    val expectedHref = randomString("href_")
    val expectedTitle = randomString("title_")
    val expectedText = randomString("text_")
    val expected = div.of(
      href is expectedHref,
      title is expectedTitle,
      span of expectedText
    )

    val setHref = href := expectedHref
    val setTitle = title := expectedTitle
    val addChild = span(expectedText)

    mount("[href], [title], span", div(setHref, setTitle, addChild))
    expectNode(expected)
    unmount()

    mount("[title], [href], span", div(setTitle, setHref, addChild))
    expectNode(expected)
    unmount()

    mount("[title], span, [href]", div(setTitle, addChild, setHref))
    expectNode(expected)
    unmount()

    mount("span, [title], [href]", div(addChild, setTitle, setHref))
    expectNode(expected)
    unmount()
  }

  it("sets reflected attrs in nested elements") {
    val expectedRel1 = randomString("rel1_")
    val expectedRel2 = randomString("rel2_")
    val expectedText1 = randomString("text1_")
    val expectedText2 = randomString("text2_")
    val expectedColSpan = 1 + Random.nextInt(15)
    val expectedRowSpan = 15 + Random.nextInt(7)

    mount(
      td(
        colSpan := expectedColSpan,
        rowSpan := expectedRowSpan,
        span(
          rel := expectedRel1,
          expectedText1,
          span(
            expectedText2,
            rel := expectedRel2
          )
        ),
        span()
      )
    )

    expectNode(
      td.of(
        colSpan is expectedColSpan,
        rowSpan is expectedRowSpan,
        rel.isEmpty,
        span.of(
          rel is expectedRel1,
          colSpan.isEmpty,
          rowSpan.isEmpty,
          expectedText1,
          span.of(
            rel is expectedRel2,
            colSpan.isEmpty,
            rowSpan.isEmpty,
            expectedText2
          )
        ),
        span
      )
    )
  }
}
