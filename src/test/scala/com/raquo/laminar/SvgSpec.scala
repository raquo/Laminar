package com.raquo.laminar

import com.raquo.laminar.api.A._
import com.raquo.laminar.api.L.svg._
import com.raquo.laminar.api._
import com.raquo.laminar.keys.SvgAttr
import com.raquo.laminar.utils.UnitSpec

class SvgSpec extends UnitSpec {

  it("renders sample svg, sets attrs and responds to events") {

    val strokeWidthVar = Var("3")

    var clickCount = 0

    val clickMod = L.onClick --> (_ => clickCount += 1)

    val polylineEl = polyline(
      points := "20,20 40,25 60,40 80,120 120,140 200,180",
      fill := "none",
      stroke := "black",
      className := "classy",
      strokeWidth <-- strokeWidthVar.signal,
      clickMod
    )

    val el = svg(
      xmlns := SvgAttr.svgNamespaceUri, // kinda tests https://github.com/raquo/Laminar/issues/143
      height := "800",
      width := "500",
      polylineEl
    )

    mount(L.div(el))

    expectNode(L.div.of(svg.of(
      height is "800",
      width is "500",
      polyline.of(
        points is "20,20 40,25 60,40 80,120 120,140 200,180",
        fill is "none",
        stroke is "black",
        className is "classy",
        strokeWidth is "3"
      )
    )))

    // --

    (stroke := "red").apply(polylineEl)

    expectNode(L.div.of(svg.of(
      height is "800",
      width is "500",
      polyline.of(
        points is "20,20 40,25 60,40 80,120 120,140 200,180",
        fill is "none",
        stroke is "red", // <-- the change
        className is "classy",
        strokeWidth is "3"
      )
    )))

    // --

    strokeWidthVar.writer.onNext("4")

    expectNode(L.div.of(svg.of(
      height is "800",
      width is "500",
      polyline.of(
        points is "20,20 40,25 60,40 80,120 120,140 200,180",
        fill is "none",
        stroke is "red", // <-- the change
        strokeWidth is "4",
        className is "classy"
      )
    )))

    polylineEl.eventListeners shouldBe List(clickMod)
    clickCount shouldBe 0

    // One event listener added
    simulateClick(polylineEl.ref)
    clickCount shouldBe 1

    unmount()
  }

  it("renders el with class and svg with text") {

    val el = svg(
      xmlns := SvgAttr.svgNamespaceUri,
      className := "svgClass",
      height := "800",
      width := "500",
      text("Hello")
    )

    mount(
      L.div(
        L.className := "htmlClass",
        el
      )
    )

    expectNode(L.div.of(
      L.className is "htmlClass",
      svg.of(
        className is "svgClass",
        height is "800",
        width is "500",
        text of "Hello"
      )
    ))
  }

  it("correctly sets namespaces") {

    val el = svg(
      xmlns := SvgAttr.svgNamespaceUri,
      xmlnsXlink := SvgAttr.xlinkNamespaceUri,
      a(
        xlinkHref := "https://example.com"
      )
    )

    mount(
      L.div(
        L.className := "htmlClass",
        el
      )
    )

    expectNode(L.div.of(
      L.className is "htmlClass",
      svg.of(
        xmlns is SvgAttr.svgNamespaceUri,
        xmlnsXlink is SvgAttr.xlinkNamespaceUri,
        a of(
          xlinkHref is "https://example.com"
        )
      )
    ))
  }
}
