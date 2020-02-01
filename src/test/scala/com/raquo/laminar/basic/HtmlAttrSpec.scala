package com.raquo.laminar.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

import scala.util.Random

class HtmlAttrSpec extends UnitSpec {

  it("sets attrs") {
    val expectedTitle = randomString("title_")
    val expectedColSpan = Random.nextInt(15)
    val expectedRowSpan = 15 + Random.nextInt(7)

    mount("div", div(title := expectedTitle))
    expectNode(div like (title is expectedTitle))
    unmount()

    mount("td [colSpan, rowSpan]", td(
      colSpan := expectedColSpan,
      rowSpan := expectedRowSpan
    ))
    expectNode(
      td like(
        colSpan is expectedColSpan,
        rowSpan is expectedRowSpan,
        title.isEmpty
      )
    )
    unmount()
  }

  it("sets boolean attrs") {
    mount("[contentEditable=false]", div(contentEditable := false))
    expectNode(div like(contentEditable is false, colSpan.isEmpty))
    unmount()

    mount("[contentEditable=true]", div(contentEditable := true))
    expectNode(div like(contentEditable is true, colSpan.isEmpty))
    unmount()
  }

  it("sets integer attrs") {
    val expectedHeight = Random.nextInt(10)
    mount("expectedHeight", td(heightAttr := expectedHeight))
    expectNode(td like(heightAttr is expectedHeight, selected.isEmpty))
    unmount()
  }
}
