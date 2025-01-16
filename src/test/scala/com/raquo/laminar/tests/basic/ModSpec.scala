package com.raquo.laminar.tests.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

import scala.scalajs.js

class ModSpec extends UnitSpec {

  it("nodeSeq type inference") {

    val nodes = nodeSeq("text", span("element"))

    val el = div(
      nodes
    )

    mount(el)

    expectNode(
      div.of("text", span.of("element"))
    )
  }

  it("modSeq type inference") {

    val mods = modSeq(title("world"), "text", span("element"))

    val el = div(
      "hello",
      mods
    )

    mount(el)

    expectNode(
      div.of("hello", title is "world", "text", span.of("element"))
    )
  }

  case class Coordinates(x: Int, y: Int, z: Int)

  it("modSeqWith") {
    var lastZ: js.UndefOr[Int] = js.undefined
    val zObserver = Observer[Int](lastZ = _)

    val el = div(
      modSeqWith(Val(Coordinates(1, 2, 3)))(
        _ => "hello", // testing type inference that depends on implicits
        left.px <-- _.map(_.x),
        top.px <-- _.map(_.y),
        _.map(_.z) --> zObserver,
        child.text <-- _.map(_.toString)
      )
    )

    assertEquals(lastZ, js.undefined)

    // --

    mount(el)

    expectNode(
      div.of(
        "hello",
        left is "1px",
        top is "2px",
        "Coordinates(1,2,3)"
      )
    )

    assertEquals(lastZ, 3)
  }

}
