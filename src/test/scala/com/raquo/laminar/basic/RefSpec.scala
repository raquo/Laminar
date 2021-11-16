package com.raquo.laminar.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

class RefSpec extends UnitSpec {

  it("creates ref right away") {
    val node = div()
    node.ref.isInstanceOf[dom.Element] shouldBe true
    node.ref.parentNode shouldBe null

    mount(node)
    node.ref.parentNode shouldBe containerNode

    unmount()
    node.ref.parentNode shouldBe null
  }
}
