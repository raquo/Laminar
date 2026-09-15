package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class SharedClassOwnershipRegressionSpec extends UnitSpec {

  List(false, true).foreach { reverse =>
    it(s"removes a shared class after both bindings release it (reverse=$reverse)") {
      val first = Var(true)
      val second = Var(true)
      val host = div(cls("active") <-- first.signal, cls("active") <-- second.signal)
      mount(host)
      expectNode(div.of(cls is "active"))

      val (releaseFirst, releaseLast) = if (reverse) {
        (second, first)
      } else {
        (first, second)
      }
      releaseFirst.set(false)
      expectNode(div.of(cls is "active"))
      releaseLast.set(false)
      expectNode(div.of(cls is ""))
    }
  }
}
