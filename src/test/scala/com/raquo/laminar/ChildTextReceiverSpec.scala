package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.modifiers.RenderableText
import com.raquo.laminar.utils.UnitSpec

class ChildTextReceiverSpec extends UnitSpec {

  it("child.text with stream") {

    val bus = new EventBus[String]

    val el = div(
      "Hello",
      child.text <-- bus
    )

    mount(el)

    expectNode(
      div of(
        "Hello",
        sentinel
      )
    )

    // --

    bus.emit("a")

    expectNode(
      div of(
        "Hello",
        "a"
      )
    )

    // --

    bus.emit("b")

    expectNode(
      div of(
        "Hello",
        "b"
      )
    )
  }

  it("multiple child.text with stream") {

    val bus1 = new EventBus[String]
    val bus2 = new EventBus[String]

    val el = div(
      "Hello",
      child.text <-- bus1,
      child.text <-- bus2,
    )

    mount(el)

    expectNode(
      div of(
        "Hello",
        sentinel,
        sentinel
      )
    )

    // --

    bus1.emit("a")

    expectNode(
      div of(
        "Hello",
        "a",
        sentinel
      )
    )

    // --

    bus1.emit("b")

    expectNode(
      div of(
        "Hello",
        "b",
        sentinel
      )
    )

    // --

    bus2.emit("1")

    expectNode(
      div of(
        "Hello",
        "b",
        "1"
      )
    )

    // --

    bus2.emit("2")

    expectNode(
      div of(
        "Hello",
        "b",
        "2"
      )
    )

    // --

    EventBus.emit(
      bus2 -> "3",
      bus1 -> "c"
    )

    expectNode(
      div of(
        "Hello",
        "c",
        "3"
      )
    )
  }

  it("locked to one string") {

    val v = Var(true)

    val el = div(
      text("nope") := false,
      text("yep") := true,
      text("hello") <-- v
    )

    // --

    mount(el)

    expectNode(
      div.of(emptyCommentNode, "yep", "hello")
    )

    // --

    v.set(false)

    expectNode(
      div.of(emptyCommentNode, "yep", "")
    )

    // --

    v.set(true)

    expectNode(
      div.of(emptyCommentNode, "yep", "hello")
    )
  }

  it("locked to one textLike") {

    val v = Var(true)

    class TextLike(val str: String)

    implicit val renderable: RenderableText[TextLike] = RenderableText(_.str)

    val el = div(
      text(new TextLike("nope")) := false,
      text(new TextLike("yep")) := true,
      text(new TextLike("hello")) <-- v
    )

    // --

    mount(el)

    expectNode(
      div.of(emptyCommentNode, "yep", "hello")
    )

    // --

    v.set(false)

    expectNode(
      div.of(emptyCommentNode, "yep", "")
    )

    // --

    v.set(true)

    expectNode(
      div.of(emptyCommentNode, "yep", "hello")
    )
  }
}
