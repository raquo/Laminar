package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class DetachedRootSpec extends UnitSpec {

  it("node lifecycle - activateNow = false") {

    val nameVar = Var("world")

    val root = renderDetached(
      div("hello", child.text <-- nameVar),
      activateNow = false
    )

    expectNode(
      root.node.ref,
      div of ("hello", sentinel)
    )

    // --

    assertEquals(root.isActive, false)

    root.activate()

    expectNode(
      root.node.ref,
      div of ("hello", "world")
    )

    assertEquals(root.isActive, true)

    // --

    root.deactivate()

    nameVar.set("ignored-name")

    expectNode(
      root.node.ref,
      div of("hello", "world")
    )

    assertEquals(root.isActive, false)
  }

  it("node lifecycle - activateNow = true") {

    val nameVar = Var("world")

    val root = renderDetached(
      div("hello", child.text <-- nameVar),
      activateNow = true
    )

    expectNode(
      root.node.ref,
      div of ("hello", "world")
    )

    assertEquals(root.isActive, true)

    // --

    nameVar.set("you")

    expectNode(
      root.node.ref,
      div of("hello", "you")
    )

    // --

    root.deactivate()

    nameVar.set("ignored-name")

    expectNode(
      root.node.ref,
      div of("hello", "you")
    )
  }
}
