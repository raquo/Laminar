package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class OptionalTextRemountRegressionSpec extends UnitSpec {

  it("clears optional text changed to None while unmounted") {
    val valueVar = Var(Option("old"))
    val host = div("before", text.maybe <-- valueVar.signal, "after")
    mount(host)
    expectNode(div.of("before", "old", "after"))

    unmount()
    valueVar.set(None)
    mount(host)
    expectNode(div.of("before", sentinel, "after"))

    valueVar.set(Some("new"))
    expectNode(div.of("before", "new", "after"))
  }

  it("clears previous onMountInsert content when optional text starts with None") {
    var useText = false
    val host = div(
      "before",
      onMountInsert { _ =>
        if (useText) {
          text.maybe <-- Val(Option.empty[String])
        } else {
          span("old")
        }
      },
      "after"
    )
    mount(host)
    expectNode(div.of("before", sentinel, span of "old", "after"))
    unmount()
    useText = true
    mount(host)
    expectNode(div.of("before", sentinel, "after"))
  }
}
