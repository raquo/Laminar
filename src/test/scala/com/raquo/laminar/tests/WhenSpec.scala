package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

class WhenSpec extends UnitSpec {

  it("when evaluates arguments immediately & by-name") {

    var counter1 = 0
    var counter2 = 0
    var counter3 = 0

    // --

    val whenMod1 = when(true) {
      counter1 += 1
      "Hello 1"
    }
    val whenMod2 = when(false) {
      counter2 += 1
      "Hello 2"
    }
    val whenNotMod3 = whenNot(false) {
      counter3 += 1
      "Hello not 3"
    }

    // #TODO[API] Not 100% sure if it's desirable to evaluate these
    //  outside of the element, but I think this is the most predictable
    //  behaviour: when (true) -> evaluate & return.
    // - This matches the behaviour of native `if`, which is what we're
    //   modelling `when` after.
    // - If we thought this wrong, we could wrap the contents of `when`
    //   into another modifier, but then they would get re-evaluated
    //   on every access.
    // - For now I struggle to think of a valid use case where this would
    //   matter – `when` is intended for inlining into elements, where
    //   this distinction is moot.
    // #TODO[API] If we get a signal-based version when(boolSignal),
    //  we should review this decision
    assertEquals(counter1, 1)
    assertEquals(counter2, 0)
    assertEquals(counter3, 1)

    // --

    val el = div(
      whenMod1,
      whenMod2,
      whenNotMod3,
      "Sentinel"
    )

    mount(el)

    expectNode(
      div of (
        "Hello 1",
        "Hello not 3",
        "Sentinel"
      )
    )

    assertEquals(counter1, 1)
    assertEquals(counter2, 0)
    assertEquals(counter3, 1)

    // --

    el.amend(whenMod1)
    el.amend(whenMod2)
    el.amend(whenNotMod3)

    // Sentinel is now first because the same nodes were re-appended
    expectNode(
      div of (
        "Sentinel",
        "Hello 1",
        "Hello not 3",
      )
    )

    // no change – side effects have already executed
    assertEquals(counter1, 1)
    assertEquals(counter2, 0)
    assertEquals(counter3, 1)
  }

}
