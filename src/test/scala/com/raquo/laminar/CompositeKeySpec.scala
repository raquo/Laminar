package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class CompositeKeySpec extends UnitSpec {

  it("cls - simple static modifiers") {
    val el = div(className := "foo")
    mount(el)
    expectNode(div.of(cls is "foo"))

    withClue("Append, not replace (1)") {
      (cls := "bar") (el)
      expectNode(div.of(cls is "foo bar"))
    }

    withClue("Append, not replace (2):") {
      (cls := "baz") (el)
      expectNode(div.of(cls is "foo bar baz"))
    }

    withClue("No duplicates:") {
      (cls := "bar") (el)
      expectNode(div.of(cls is "foo bar baz"))
    }

    withClue("Adding empty class does nothing:") {
      (cls := "") (el)
      expectNode(div.of(cls is "foo bar baz"))
    }

    withClue("Adding empty class does nothing:") {
      (cls := " ") (el)
      expectNode(div.of(cls is "foo bar baz"))
    }

    withClue("Adding two space separated classes works, even with an extra space:") {
      (cls := "fox  box") (el)
      expectNode(div.of(cls is "foo bar baz fox box"))
    }

    withClue("Adding again in different order does not produce duplicates:") {
      (cls := "box fox") (el)
      expectNode(div.of(cls is "foo bar baz fox box"))
    }
  }

  it("cls - fancy static modifiers") {
    val el = div(cls("foo", "bar"))
    mount(el)
    expectNode(div.of(cls is "foo bar"))

    // Note: We need locally() wrapper because otherwise scala will incorrectly
    // interpret this as passing el in place of an implicit parameter.
    locally(cls("baz foo", "qux fox"))(el)
    expectNode(div.of(cls is "foo bar baz qux fox"))

    // As of Laminar v0.12.0, you can't remove classes that you previously added with a different modifier of this.
    (cls := Map("bar xxx" -> false, "" -> false, " " -> false, "baz" -> true, "box dox" -> true, "foo" -> false))(el)
    expectNode(div.of(cls is "foo bar baz qux fox box dox"))

    // As of Laminar v0.12.0, you can't remove classes that you previously added with a different modifier of this.
    locally(cls("bar" -> true, "" -> true, " " -> false, "baz box" -> false, "dox" -> true, "foo" -> true))(el)
    expectNode(div.of(cls is "foo bar baz qux fox box dox"))

    // As of Laminar v0.12.0, you can't remove classes that you previously added with a different modifier of this.
    val classes1 = List("qux" -> false, "baz" -> true)
    locally(cls := classes1)(el)
    expectNode(div.of(cls is "foo bar baz qux fox box dox"))

    val classes2 = List("qux", "baz")
    locally(cls := classes2)(el)
    expectNode(div.of(cls is "foo bar baz qux fox box dox"))
  }

  it("cls - simple reactive modifiers") {
    val classesBus = new EventBus[String]
    val classesSeqBus = new EventBus[Seq[String]]

    val el = div(
      cls := "foo",
      cls <-- classesBus.events,
      cls <-- classesSeqBus.events
    )
    mount(el)
    expectNode(div.of(cls is "foo"))

    // Add classes without removing `foo`
    classesBus.writer.onNext("bar baz")
    expectNode(div.of(cls is "foo bar baz"))

    // Remove `bar` because this stream's new value does not include it
    classesBus.writer.onNext("bax baz")
    expectNode(div.of(cls is "foo baz bax"))

    classesSeqBus.writer.onNext(List("ya yo"))
    expectNode(div.of(cls is "foo baz bax ya yo"))

    classesSeqBus.writer.onNext(List("yo", "ye"))
    expectNode(div.of(cls is "foo baz bax yo ye"))

    // `classesSeqBus` claims `baz`, in conflict with `classesBus`
    classesSeqBus.writer.onNext(List("yo", "ye baz"))
    expectNode(div.of(cls is "foo baz bax yo ye"))

    // Solved interference case as of Laminar v0.12.0:
    // - Previously, because `classesSeqBus` emitted `baz`, removing it from the next value removed it from the list
    // - But now its not removed anymore because we keep track of which modifiers want which classes
    classesSeqBus.writer.onNext(List("yo"))
    expectNode(div.of(cls is "foo baz bax yo"))

    // Restore bar
    classesBus.writer.onNext("bar baz bax")
    expectNode(div.of(cls is "foo baz bax yo bar"))
  }

  it("cls - fancy reactive modifiers") {
    val classesBus = new EventBus[Seq[(String, Boolean)]]
    val classesMapBus = new EventBus[Map[String, Boolean]]

    val el = div(
      cls := "foo",
      cls <-- classesBus.events,
      cls <-- classesMapBus.events
    )
    mount(el)
    expectNode(div.of(cls is "foo"))

    // Add classes without removing `foo`
    classesBus.writer.onNext(List("bar baz" -> true))
    expectNode(div.of(cls is "foo bar baz"))

    // Remove `bar` because this stream's new value does not include it
    classesBus.writer.onNext(List("bax" -> true, "baz" -> true))
    expectNode(div.of(cls is "foo baz bax"))

    // As of Laminar v0.12.0, we can't remove `foo` this way, as it is still required by another modifier
    classesBus.writer.onNext(List("bax" -> true, "baz" -> true, "foo" -> false))
    expectNode(div.of(cls is "foo baz bax"))

    classesMapBus.writer.onNext(Map("ya" -> true, "yo" -> true))
    expectNode(div.of(cls is "foo baz bax ya yo"))

    classesMapBus.writer.onNext(Map("yo  ye foo" -> true))
    expectNode(div.of(cls is "foo baz bax yo ye"))

    // As of Laminar v0.12.0, we can't remove `foo` this way, as it is still required by another modifier
    classesMapBus.writer.onNext(Map("ye" -> true, "foo" -> false))
    expectNode(div.of(cls is "foo baz bax ye"))
  }

  it("cls - toggle - eventbus") {
    val bus = new EventBus[Boolean]
    val el = div(
      cls := "foo faa",
      cls("bar bax") := true,
      cls("bar nope") := false,
      cls("bar baz") <-- bus,
      cls("qux") <-- bus.events
    )
    mount(el)
    expectNode(div.of(cls is "foo faa bar bax"))

    bus.writer.onNext(false)
    expectNode(div.of(cls is "foo faa bar bax"))

    bus.writer.onNext(true)
    expectNode(div.of(cls is "foo faa bar bax baz qux"))

    // This does not actually do anything since Laminar v0.12.0
    el.amend(cls("foo faa") := false)
    expectNode(div.of(cls is "foo faa bar bax baz qux"))
  }

  it("cls - toggle - var") {
    val bus = Var(false)
    val el = div(
      cls := "foo faa",
      cls("bar bax") := true,
      cls("bar nope") := false,
      cls("foo baz") <-- bus
    )
    mount(el)
    expectNode(div.of(cls is "foo faa bar bax")) // Var starts with false

    bus.writer.onNext(true)
    expectNode(div.of(cls is "foo faa bar bax baz"))

    bus.writer.onNext(false)
    expectNode(div.of(cls is "foo faa bar bax"))
  }

  it("cls - no interference") {

    val bus = new EventBus[Int]

    mount(
      div(
        "hello",
        cls <-- bus.events.map { num =>
          if (num % 2 == 0) {
            "always even"
          } else {
            ""
          }
        },
        cls <-- bus.events.map { num =>
          if (num % 2 == 1) {
            "always odd"
          } else {
            ""
          }
        }
      )
    )

    expectNode(div.of("hello"))

    bus.writer.onNext(1)

    expectNode(
      div.of(
        "hello",
        cls is ("always odd")
      )
    )

    bus.writer.onNext(2)

    expectNode(
      div.of(
        "hello",
        cls is ("always even")
      )
    )

  }

  it("cls - third party interference") {

    val clsBus = new EventBus[String]

    val el = div(
      cls := "always",
      cls <-- clsBus.events
    )

    mount(el)

    expectNode(
      div.of(
        cls is ("always")
      )
    )

    // -- Laminar cls should not interfere with externally added class

    el.ref.className = el.ref.className + " external"

    expectNode(
      div.of(
        cls is ("always external")
      )
    )

    // --

    clsBus.writer.onNext("foo bar")

    expectNode(
      div.of(
        cls is ("always external foo bar")
      )
    )

    // --

    clsBus.writer.onNext("foo baz")

    expectNode(
      div.of(
        cls is ("always external foo baz")
      )
    )

    // --

    clsBus.writer.onNext("foo external")

    expectNode(
      div.of(
        cls is ("always external foo external") // kinda weird but ok
      )
    )

    // -- Interference: the "external" class is removed even though it was added manually from outside Laminar.
    //     This is expected. It's hard to work around. Clean up your class logic if you run into this...

    clsBus.writer.onNext("foo")

    expectNode(
      div.of(
        cls is ("always foo")
      )
    )

    // --

    el.ref.classList.add("external2")

    expectNode(
      div.of(
        cls is ("always foo external2")
      )
    )

    // --

    clsBus.writer.onNext("foo bar")

    expectNode(
      div.of(
        cls is ("always foo external2 bar")
      )
    )

    // --

    el.ref.classList.add("foo")

    expectNode(
      div.of(
        cls is ("always foo external2 bar")
        )
    )
  }

  it("svg cls - simple static modifiers") {
    // We test the basics differently because SVG complex keys use DOM attributes whereas HTML uses props

    val poly = svg.polyline(svg.cls := "foo")
    val el = svg.svg(poly)
    mount(el)
    expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo")))

    withClue("Append, not replace (1)") {
      poly.amend(svg.cls := "bar")
      expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo bar")))
    }

    withClue("Append, not replace (2):") {
      poly.amend(svg.cls := "baz") (el)
      expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo bar baz")))
    }

    withClue("No duplicates:") {
      poly.amend(svg.cls := "bar")
      expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo bar baz")))
    }

    withClue("Adding empty class does nothing:") {
      poly.amend(svg.cls := "")
      expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo bar baz")))
    }

    withClue("Adding empty class does nothing:") {
      poly.amend(svg.cls := " ")
      expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo bar baz")))
    }

    withClue("Adding two space separated classes works, even with an extra space:") {
      poly.amend(svg.cls := "fox  box")
      expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo bar baz fox box")))
    }

    withClue("Adding again in different order does not produce duplicates:") {
      poly.amend(svg.cls := "box fox")
      expectNode(svg.svg.of(svg.polyline.of(svg.cls is "foo bar baz fox box")))
    }
  }
}
