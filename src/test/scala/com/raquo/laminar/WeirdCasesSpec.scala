package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

/** These tests verify Laminar's behaviour in weird cases.
  *
  * DO NOT look into these test for inspiration for how to do things in Laminar.
  *
  * Every test will have a quick note on how to do achieve the same result better.
  */
class WeirdCasesSpec extends UnitSpec {

  // @nc I don't think we can get this to work. Can we maybe write a workaround to detect this condition?
  // - Similar problem is described in https://github.com/raquo/Laminar/issues/47
  // - But now in Laminar v0.8 subscriptions are not activated until mount event fires, and that happens in a new transaction,
  //   but by then the original event that the synchronously dependent observable relies upon is long gone, so it doesn't get that event
  // - Generally I think this is an acceptable tradeoff, as long as we document this.
  // - Ideally we should offer a workaround.
  // - I would assume most encounters of this would be a misuse of streams vs signals...
  // @nc write a similar test for signals that works

  /** See https://github.com/raquo/Laminar/issues/11 for a better approach
    * (basically, use child.text instead of nested observables)
    */
  ignore("nested, synchronously dependent observables work as expected for some reason") {

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
