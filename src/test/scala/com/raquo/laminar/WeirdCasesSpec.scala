package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.utils.UnitSpec
import com.raquo.laminar.api.L._

/** These tests verify Laminar's behaviour in weird cases.
  *
  * DO NOT look into these test for inspiration for how to do things in Laminar.
  *
  * Every test will have a quick note on how to do achieve the same result better.
  */
class WeirdCasesSpec extends UnitSpec {

  /** See https://github.com/raquo/Laminar/issues/11 for a better approach
    * (basically, use child.text instead of nested observables)
    */
  it("nested, synchronously dependent observables work as expected for some reason") {

    val bus = new EventBus[Int]

    mount(
      div(
        "hello",
        child <-- bus.events.map(num =>
          span(
            s"num: $num",
            child <-- bus.events.map(innerNum => article(s"innerNum: $innerNum"))
          )
        )
      )
    )

    // @TODO[Test] we should not need to specify this sentinel comment node here. Gotta find a way to ignore those.
    expectNode(div like ("hello", ExpectedNode.comment()))

    bus.writer.onNext(1)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 1",
          article like "innerNum: 1"
        )
      )
    )

    bus.writer.onNext(2)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 2",
          article like "innerNum: 2"
        )
      )
    )
  }
}
