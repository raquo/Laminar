package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

class WhenVarArgSpec extends UnitSpec {

  // Scala 2 does not support by-name varargs, so this sub-feature is Scala 3 only.

  it("when evaluates varargs immediately & by-name") {
    var counter1 = 0
    var counter2 = 0
    var counter3 = 0

    // --

    val whenVarArgMod1 = when(true)(
      Modifier(_ => counter1 += 1),
      "Hello 1",
      Modifier(_ => counter2 += 10),
      span("Hello 10"),
      Modifier(_ => counter3 += 100),
      "Hello 100",
    )

    val whenVarArgMod2 = when(false)(
      Modifier(_ => counter1 += 1),
      "x",
      Modifier(_ => counter2 += 1),
      span("xx"),
      Modifier(_ => counter3 += 1),
      "xxx",
    )

    val whenNotVarArgMod3 = whenNot(false)(
      Modifier(_ => counter1 += 1000),
      "y",
      Modifier(_ => counter2 += 1000),
      span("yy"),
      Modifier(_ => counter3 += 1000),
      "yyy",
    )

    val whenNotVarArgMod4 = whenNot(true)(
      Modifier(_ => counter1 += 1000),
      "z",
      Modifier(_ => counter2 += 1000),
      span("zz"),
      Modifier(_ => counter3 += 1000),
      "zzz",
    )

    // Unlike the other test, the side effects are INSIDE these modifiers,
    // so they are only evaluated once they are applied to the element.
    assertEquals(counter1, 0)
    assertEquals(counter2, 0)
    assertEquals(counter3, 0)

    // --

    val el = div(
      whenVarArgMod1,
      whenVarArgMod2,
      whenNotVarArgMod3,
      whenNotVarArgMod4,
      "Sentinel"
    )

    assertEquals(counter1, 1001)
    assertEquals(counter2, 1010)
    assertEquals(counter3, 1100)


    mount(el)

    expectNode(
      div of (
        "Hello 1",
        span of "Hello 10",
        "Hello 100",
        "y",
        span of "yy",
        "yyy",
        "Sentinel",
      )
    )

    assertEquals(counter1, 1001)
    assertEquals(counter2, 1010)
    assertEquals(counter3, 1100)

    // --

    el.amend(
      whenVarArgMod1,
      whenVarArgMod2,
      whenNotVarArgMod3,
      whenNotVarArgMod4,
    )

    // sentinel is first because nodes were re-added
    expectNode(
      div of (
        "Sentinel",
        "Hello 1",
        span of "Hello 10",
        "Hello 100",
        "y",
        span of "yy",
        "yyy",
      )
    )

    // unlike the other test, side effects are re-applied every time the modifiers are re-applied
    assertEquals(counter1, 2002)
    assertEquals(counter2, 2020)
    assertEquals(counter3, 2200)
  }


}
