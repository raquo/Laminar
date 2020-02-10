package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js

class SyntaxSpec extends UnitSpec {

  it("ModifierSeq implicit conversion works for both strings and nodes") {

    val strings = List("a", "b")
    val nodes = Vector(span("ya"), article("yo"))
    val jsNodes: scala.collection.Seq[Mod[Element]] = js.Array(span("js")) // JS arrays need some help for now. We could optimize that if needed
    val mixed: Seq[Mod[HtmlElement]] = Vector("c", input())

    mount(div(strings, nodes, jsNodes, mixed))

    expectNode(div like (
      "a", "b",
      span like "ya", article like "yo",
      span like "js",
      "c", input
    ))
  }

  it("inContext modifier infers precise type") {

    val testOwner = new TestableOwner

    val checkedBus = new EventBus[Boolean]

    val events = mutable.Buffer[Boolean]()
    checkedBus.events.foreach(events += _)(testOwner)

    val checkbox = input(
      typ := "checkbox",
      checked := true,
      inContext(thisNode => onClick.mapTo(thisNode.ref.checked) --> checkedBus)
    )

    mount(checkbox)

    events shouldEqual mutable.Buffer()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(false)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(true)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(false)
    events.clear()
  }

//  it ("onMount infers precise type and provides implicit owner") {
//
//    var value = 0
//
//    val bus = new EventBus[Int]
//    val observer = Observer[Int](ev => value = ev)
//
//    val el = div("Hello", onMountCallback(ctx => {
//      import ctx.owner
//      val x: Div = ctx.thisNode
//      bus.events.addObserver(observer) // using owner implicitly
//    }))
//
//    // --
//
//    bus.writer.onNext(1)
//
//    assert(value == 0) // not subscribed yet
//
//    mount(el)
//
//    bus.writer.onNext(2)
//
//    assert(value == 2)
//  }
//
//  it ("onMount and onUnmount work for repeated mounting") {
//
//    var numMounts = 0
//    var numUnmounts = 0
//
//    val el = div("Hello", onMountCallback(_ => numMounts += 1), onUnmount(_ => numUnmounts += 1))
//
//    assert(numMounts == 0)
//    assert(numUnmounts == 0)
//
//    // --
//
//    mount(el)
//
//    assert(numMounts == 1)
//    assert(numUnmounts == 0)
//
//    numMounts = 0
//
//    // --
//
//    unmount()
//
//    assert(numMounts == 0)
//    assert(numUnmounts == 1)
//
//    numUnmounts = 0
//
//    // --
//
//    mount(el)
//
//    assert(numMounts == 1)
//    assert(numUnmounts == 0)
//
//    numMounts = 0
//
//    // --
//
//    unmount()
//
//    assert(numMounts == 0)
//    assert(numUnmounts == 1)
//
//    numUnmounts = 0
//  }
//
//  it("bind / bindObserver / bindBus / etc. syntax") {
//
//    val el = div("Hello world")
//
//    val bus = new EventBus[Int]
//    val state = Var(5)
//    val signal = state.signal
//    val stream = signal.changes
//    val observable: Observable[Int] = stream
//
//    // @TODO[API] Can we have type inference for this [Int]?
//    el.bindObserver(observable)(Observer[Int](num => num * 5))
//    el.bindObserver(signal)(Observer[Int](num => num * 5))
//    el.bindObserver(stream)(Observer[Int](num => num * 5))
//
//    el.bindFn(observable)(num => num * 5)
//    el.bindFn(signal)(num => num * 5)
//    el.bindFn(stream)(num => num * 5)
//
//    el.bind(observable --> Observer[Int](num => num * 5))
//    el.bind(signal --> Observer[Int](num => num * 5))
//    el.bind(stream --> Observer[Int](num => num * 5))
//
//    el.bind(onClick --> Observer[dom.MouseEvent](ev => ()))
//    el.bind(onClick.mapTo(1) --> Observer[Int](num => num * 5))
//
//    el.bind(observable --> (num => num * 5))
//    el.bind(signal --> (num => num * 5))
//    el.bind(stream --> (num => num * 5))
//
//    el.bind(observable --> ((num: Int) => num * 5))
//    el.bind(signal --> ((num: Int) => num * 5))
//    el.bind(stream --> ((num: Int) => num * 5))
//
//    el.bindThis(_.events(onClick).map(_ => 1) --> (num => num * 5))
//    el.bindThis(
//      _.events(onClick).map(_ => 1) --> (num => num * 5),
//      _.events(onDblClick).map(_ => "x") --> Observer[String](num => "a" + num),
//    )
//
//    el.bind(stream --> bus.writer)
//    el.bindBus(stream)(bus.writer)
//
//    // --
//
//    mount(el)
//  }

  it("onFocusMount") {

    val el = div(
      onMountFocus, // I'd love to avoid this line compiling, but I can't.
      input(defaultValue("Your name "), onMountFocus),
      textArea(defaultValue("Your name "), onMountFocus)
    )

    mount(el)
  }

}
