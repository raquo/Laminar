package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.ButtonElement
import com.raquo.laminar.utils.UnitSpec

class InsertHookSpec extends UnitSpec {

  it("Slot syntax compilation") {

    // #TODO[Test]: Actually test this with HTML custom elements.
    //  I don't think we need JS web components to test this, just
    //  need a <template> with slots in it.

    val staticElements = List(div("list-el1"), div("list-el2"))

    val dynamicElements = List(div("children <-- val (el1)"), div("children <-- val (el2)"))

    // Mark as used (it's used in compilable code strings below)
    val _ = ButtonElement

    assertCompiles(
      """
      div(
        ButtonElement.of(
          _.slots.prefix(
            div("hello"),
            span("foo"),
            staticElements,
            child <-- Val(span("child <-- val")),
            child.maybe <-- Val(Some(span("child.maybe <-- val"))),
            text <-- Val("text <-- val"),
            children <-- Val(dynamicElements)
          )
        )
      )
      """
    )

    assertCompiles(
      """
      ButtonElement.of(_.slots.prefix(span("wrapped text")))
      """
    )

    assertCompiles(
      """
      ButtonElement.of(_.slots.prefix(child <-- Val(span("child inserter"))))
      """
    )

    assertCompiles(
      """
      ButtonElement.of(_.slots.prefix(children <-- Val(span("children inserter") :: Nil)))
      """
    )

    assertTypeError(
      """
      ButtonElement.of(_.slots.prefix("text can not be slotted"))
      """
    )

  }

}
