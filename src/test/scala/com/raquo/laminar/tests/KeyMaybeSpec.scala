package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.utils.UnitSpec

class KeyMaybeSpec extends UnitSpec {

  it("maybe :=") {

    val fooProp = htmlProp("foo", None, Codec.intAsIs)

    val el = input(
      fooProp.maybe := Some(20), // non-reflected prop
      title.maybe := Some("Hello"), // reflected prop
      widthAttr.maybe := Some(100), // html attribute
      background.maybe := Some("yellow"), // style prop
      value.maybe := Some("yo"), // value prop
    )

    mount(el)

    expectNode(
      input of (
        fooProp is 20,
        title is "Hello",
        widthAttr is 100,
        background is "yellow",
        value is "yo",
        // defaultValue is "",
      )
    )

    el.amend(
      fooProp.maybe := None,
      title.maybe := None,
      widthAttr.maybe := None,
      background.maybe := None,
      value.maybe := None,
    )

    expectNode(
      input of (
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr.isEmpty,
        background is "",
        value.isEmpty
      )
    )
  }

  it("maybe apply") {

    val fooProp = htmlProp("foo", None, Codec.intAsIs)

    val el = input(
      fooProp.maybe(Some(20)), // non-reflected prop
      title.maybe(Some("Hello")), // reflected prop
      widthAttr.maybe(Some(100)), // html attribute
      background.maybe(Some("yellow")), // style prop
      value.maybe(Some("yo")), // value prop
    )

    mount(el)

    expectNode(
      input of (
        fooProp is 20,
        title is "Hello",
        widthAttr is 100,
        background is "yellow",
        value is "yo",
        // defaultValue is "",
      )
    )

    el.amend(
      fooProp.maybe(None),
      title.maybe(None),
      widthAttr.maybe(None),
      background.maybe(None),
      value.maybe(None),
    )

    expectNode(
      input of (
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr.isEmpty,
        background is "",
        value.isEmpty
      )
    )
  }

  it("maybe <--") {

    val fooProp = htmlProp("foo", None, Codec.intAsIs)

    val fooVar = Var(Option(20))
    val titleVar = Var(Option("Hello"))
    val (widthAttrS, setWidthAttr) = EventStream.withCallback[Option[Int]]
    val (backgroundS, setBackground) = EventStream.withCallback[Option[String]]
    val (valueS, setValue) = EventStream.withCallback[Option[String]]
    val el = input(
      fooProp.maybe <-- fooVar.signal, // non-reflected prop
      title.maybe <-- titleVar.signal.map(identity), // reflected prop
      widthAttr.maybe <-- widthAttrS, // html attribute
      background.maybe <-- backgroundS, // style prop
      value.maybe <-- (valueS), // value prop
    )

    expectNode(
      el.ref,
      input of (
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr.isEmpty,
        background is "",
        value.isEmpty,
        defaultValue.isEmpty,
      )
    )

    // --

    mount(el)

    expectNode(
      input of (
        fooProp is 20,
        title is "Hello",
        widthAttr.isEmpty,
        background is "",
        value.isEmpty,
        defaultValue.isEmpty,
      )
    )

    // --

    setWidthAttr(Some(100))
    setBackground(Some("yellow"))
    setValue(Some("yo"))

    expectNode(
      input of (
        fooProp is 20,
        title is "Hello",
        widthAttr is 100,
        background is "yellow",
        value is "yo",
        defaultValue.isEmpty,
      )
    )

    // --

    fooVar.set(Some(21))
    titleVar.set(Some("Hello there"))
    setWidthAttr(Some(101))
    setBackground(Some("red"))
    setValue(Some("yooo"))

    expectNode(
      input of (
        fooProp is 21,
        title is "Hello there",
        widthAttr is 101,
        background is "red",
        value is "yooo",
        defaultValue.isEmpty,
      )
    )

    // --

    fooVar.set(None)
    titleVar.set(None)
    setWidthAttr(None)

    expectNode(
      input of (
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr.isEmpty,
        background is "red",
        value is "yooo",
        defaultValue.isEmpty,
      )
    )

    // -- external disruption

    el.amend(
      fooProp.maybe(None),
      title.maybe(None),
      widthAttr.maybe(None),
      background.maybe(None),
      value.maybe(None),
    )

    expectNode(
      input of (
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr.isEmpty,
        background is "",
        value.isEmpty,
        defaultValue.isEmpty,
      )
    )

    // -- re-build

    fooVar.set(Some(22))
    titleVar.set(Some("Hello world"))
    setWidthAttr(Some(102))
    setBackground(Some("pink"))
    setValue(Some("yooo hooo"))

    expectNode(
      input of (
        fooProp is 22,
        title is "Hello world",
        widthAttr is 102,
        background is "pink",
        value is "yooo hooo",
        defaultValue.isEmpty,
      )
    )
  }

  it("key(value) :=") {

    val fooProp = htmlProp("foo", None, Codec.intAsIs)

    val el = input(
      fooProp(20) := true, // non-reflected prop
      title("Hello") := true, // reflected prop
      widthAttr(100) := true, // html attribute
      background("yellow") := true, // style prop
      value("yo") := true, // value prop
      alt("foo")(true), // apply syntax
    )

    mount(el)

    expectNode(
      input of (
        fooProp is 20,
        title is "Hello",
        widthAttr is 100,
        background is "yellow",
        value is "yo",
        alt is "foo"
      )
    )

    el.amend(
      fooProp(20) := false,
      title("Hello") := false,
      widthAttr(100) := false,
      background("yellow") := false,
      value("yo") := false,
      alt("foo")(false),
    )

    expectNode(
      input of (
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr.isEmpty,
        background is "",
        value.isEmpty,
        alt.isEmpty
      )
    )
  }

  it("key.value <--") {

    val fooProp = htmlProp("foo", None, Codec.intAsIs)

    val fooVar = Var(true)
    val titleVar = Var(true)
    val widthBus = new EventBus[Boolean]
    val heightBus = new EventBus[Boolean]
    val backgroundBus = new EventBus[Boolean]
    val zIndexBus = new EventBus[Boolean]
    val valueBus = new EventBus[Boolean]
    val el = input(
      fooProp(20) <-- fooVar, // non-reflected prop
      title("Hello") <-- titleVar.signal.map(identity), // reflected prop
      widthAttr(100) <-- widthBus, // html attribute
      heightAttr(200) <-- heightBus, // html attribute
      background("cyan") <-- backgroundBus.events, // style prop
      zIndex(50) <-- zIndexBus,
      value("txt") <-- valueBus, // value prop
    )

    expectNode(
      el.ref,
      input of(
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr.isEmpty,
        heightAttr.isEmpty,
        background is "",
        zIndex.isEmpty,
        value.isEmpty,
        defaultValue.isEmpty,
      )
    )

    // --

    mount(el)

    expectNode(
      input of(
        fooProp is 20,
        title is "Hello",
        widthAttr.isEmpty,
        heightAttr.isEmpty,
        background is "",
        zIndex.isEmpty,
        value.isEmpty,
        defaultValue.isEmpty,
      )
    )

    // --

    widthBus.emit(false)
    zIndexBus.emit(false)

    expectNode(
      input of(
        fooProp is 20,
        title is "Hello",
        widthAttr.isEmpty,
        heightAttr.isEmpty,
        background is "",
        zIndex.isEmpty,
        value.isEmpty,
        defaultValue.isEmpty,
      )
    )

    // --

    fooVar.set(false)
    titleVar.set(false)
    widthBus.emit(true)
    heightBus.emit(true)
    backgroundBus.emit(true)
    zIndexBus.emit(true)
    valueBus.emit(true)

    expectNode(
      input of(
        fooProp.isEmpty,
        title.isEmpty,
        widthAttr is 100,
        heightAttr is 200,
        background is "cyan",
        zIndex is 50,
        value is "txt",
        defaultValue.isEmpty,
      )
    )

    // --

    fooVar.set(true)
    titleVar.set(true)

    expectNode(
      input of(
        fooProp is 20,
        title is "Hello",
        widthAttr is 100,
        heightAttr is 200,
        background is "cyan",
        zIndex is 50,
        value is "txt",
        defaultValue.isEmpty,
      )
    )

    // --

    unmount()

    // external disruption

    el.amend(
      fooProp.maybe(None),
      title.maybe(None),
      widthAttr.maybe(None),
      heightAttr.maybe(None),
      background.maybe(None),
      zIndex.maybe(None),
      value.maybe(None),
      defaultValue.maybe(None),
    )

    mount(el)

    expectNode(
      input of(
        fooProp is 20,
        title is "Hello",
        widthAttr.isEmpty,
        heightAttr.isEmpty,
        background is "",
        zIndex.isEmpty,
        value.isEmpty,
        defaultValue.isEmpty,
      )
    )

    // -- rebuild

    fooVar.set(true)
    titleVar.set(true)
    widthBus.emit(true)
    heightBus.emit(true)
    backgroundBus.emit(true)
    zIndexBus.emit(true)
    valueBus.emit(true)

    expectNode(
      input of(
        fooProp is 20,
        title is "Hello",
        widthAttr is 100,
        heightAttr is 200,
        background is "cyan",
        zIndex is 50,
        value is "txt",
        defaultValue.isEmpty,
      )
    )

  }
}
