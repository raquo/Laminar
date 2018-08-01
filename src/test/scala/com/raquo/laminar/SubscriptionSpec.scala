package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class SubscriptionSpec extends UnitSpec {

  it("ReactiveElement.subscribe infers type") {

    val el = div("Hello world")

    el.subscribeO(Var(5).signal)(Observer(num => num * 5))
    el.subscribe(_.events(onClick))(ev => ev.preventDefault())

    el.subscribeO(Var(5).signal)(Observer(num => num * 5))
    el.subscribe(_.events(onClick))(ev => ev.preventDefault())

    mount(el)
  }

}
