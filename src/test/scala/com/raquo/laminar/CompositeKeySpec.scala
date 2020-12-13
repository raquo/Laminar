package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class CompositeKeySpec extends UnitSpec {

  it("cls - simple static modifiers") {
    val el = div(cls := "foo")
    mount(el)
    expectNode(div like (cls is "foo"))

    // Append, not replace
    (cls := "bar")(el)
    expectNode(div like (cls is "foo bar"))

    // Append, not replace
    (cls := "baz")(el)
    expectNode(div like (cls is "foo bar baz"))

    // No duplicates
    (cls := "bar")(el)
    expectNode(div like (cls is "foo bar baz"))

    // Adding empty class does nothing
    (cls := "")(el)
    expectNode(div like (cls is "foo bar baz"))

    // Adding empty class does nothing
    (cls := " ")(el)
    expectNode(div like (cls is "foo bar baz"))

    // Adding two space separated classes works, even with an extra space
    (cls := "fox  box")(el)
    expectNode(div like (cls is "foo bar baz fox box"))

    // Adding again in different order does not produce duplicates
    (cls := "box fox")(el)
    expectNode(div like (cls is "foo bar baz fox box"))
  }

  it("cls - fancy static modifiers") {
    val el = div(cls := ("foo", "bar"))
    mount(el)
    expectNode(div like (cls is "foo bar"))

    // Note: We need locally() wrapper because otherwise scala will incorrectly
    // interpret this as passing el in place of an implicit parameter.
    locally(cls := ("baz foo", "qux fox"))(el)
    expectNode(div like (cls is "foo bar baz qux fox"))

    (cls := Map("bar xxx" -> false, "" -> false, " " -> false, "baz" -> true, "box dox" -> true, "foo" -> false))(el)
    expectNode(div like (cls is "baz qux fox box dox"))

    locally(cls := ("bar" -> true, "" -> true, " " -> false, "baz box" -> false, "dox" -> true, "foo" -> true))(el)
    expectNode(div like (cls is "qux fox dox bar foo"))

    val classes1 = List("qux" -> false, "baz" -> true)
    locally(cls := classes1)(el)
    expectNode(div like (cls is "fox dox bar foo baz"))

    val classes2 = List("qux", "baz")
    locally(cls := classes2)(el)
    expectNode(div like (cls is "fox dox bar foo baz qux"))
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
    expectNode(div like (cls is "foo"))

    // Add classes without removing `foo`
    classesBus.writer.onNext("bar baz")
    expectNode(div like (cls is "foo bar baz"))

    // Remove `bar` because this stream's new value does not include it
    classesBus.writer.onNext("bax baz")
    expectNode(div like (cls is "foo baz bax"))

    classesSeqBus.writer.onNext(List("ya yo"))
    expectNode(div like (cls is "foo baz bax ya yo"))

    classesSeqBus.writer.onNext(List("yo", "ye"))
    expectNode(div like (cls is "foo baz bax yo ye"))

    // `classesSeqBus` claims `baz`, in conflict with `classesBus`
    classesSeqBus.writer.onNext(List("yo", "ye baz"))
    expectNode(div like (cls is "foo baz bax yo ye"))

    // Known interference case - because `classesSeqBus` emitted `baz`, removing it from the next value removes it from the list
    classesSeqBus.writer.onNext(List("yo"))
    expectNode(div like (cls is "foo bax yo"))

    // Restore baz
    classesBus.writer.onNext("bar bax baz")
    expectNode(div like (cls is "foo bax yo bar baz"))
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
    expectNode(div like (cls is "foo"))

    // Add classes without removing `foo`
    classesBus.writer.onNext(List("bar baz" -> true))
    expectNode(div like (cls is "foo bar baz"))

    // Remove `bar` because this stream's new value does not include it
    classesBus.writer.onNext(List("bax" -> true, "baz" -> true))
    expectNode(div like (cls is "foo baz bax"))

    // Remove `foo`
    classesBus.writer.onNext(List("bax" -> true, "baz" -> true, "foo" -> false))
    expectNode(div like (cls is "baz bax"))

    classesMapBus.writer.onNext(Map("ya" -> true, "yo" -> true))
    expectNode(div like (cls is "baz bax ya yo"))

    classesMapBus.writer.onNext(Map("yo  ye foo" -> true))
    expectNode(div like (cls is "baz bax yo ye foo"))

    classesMapBus.writer.onNext(Map("ye" -> true, "foo" -> false))
    expectNode(div like (cls is "baz bax ye"))
  }

  it("cls - set") {
    val el = div(cls := "foo")
    mount(el)
    expectNode(div like (cls is "foo"))

    // Append, not replace
    (cls := "bar")(el)
    expectNode(div like (cls is "foo bar"))

    // Replace
    cls.set("foo", "faa")(el)
    expectNode(div like (cls is "foo faa"))

    // Replace again
    cls.set("baz bax")(el)
    expectNode(div like (cls is "baz bax"))

    // Append again
    (cls := "bar")(el)
    expectNode(div like (cls is "baz bax bar"))
  }

  it("cls - remove") {
    val el = div(cls := "foo bar baz qux")
    mount(el)
    expectNode(div like (cls is "foo bar baz qux"))

    // Remove some
    el.amend(cls.remove("foo baz"))
    expectNode(div like (cls is "bar qux"))

    // Remove not found
    el.amend(cls.remove("baz", "bax"))
    expectNode(div like (cls is "bar qux"))

    // Remove one more
    el.amend(cls.remove("bar", "bax"))
    expectNode(div like (cls is "qux"))
  }

  it("cls - toggle - eventbus") {
    val bus = new EventBus[Boolean]
    val el = div(
      cls := "foo faa",
      cls.toggle("bar bax") := true,
      cls.toggle("foo bar") <-- bus.events,
      cls.toggle("qux") <-- bus.events
    )
    mount(el)
    expectNode(div like (cls is "foo faa bar bax"))

    bus.writer.onNext(false)
    expectNode(div like (cls is "faa bax"))

    bus.writer.onNext(true)
    expectNode(div like (cls is "faa bax foo bar qux"))

    el.amend(cls.toggle("foo faa") := false)
    expectNode(div like (cls is "bax bar qux"))
  }
}
