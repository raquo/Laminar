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
    val nodes = Vector(span("ya"), article("yo")) // seq of elements.
    val nodesBuffer = mutable.Buffer(span("boo")) // mutable.Buffer[Span] is covariant as collection.Seq
    val jsNodes = js.Array(span("js")) // JS arrays are invariant
    val mixed: Seq[Mod[HtmlElement]] = Vector("c", input())

    // @Note we make sure that none of the above go through expensive `nodesSeqToInserter` by absence of a sentinel comment node

    mount(div(strings, nodes, nodesBuffer, jsNodes, mixed))

    expectNode(div.of(
      "a", "b",
      span of "ya", article of "yo",
      span of "boo",
      span of "js",
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

  it("onMountUnmountCallback infers precise type") {

    val el = div("Hello world", onMountUnmountCallback(
      mount = c => {
        val proof: MountContext[Div] = c
        ()
      },
      unmount = n => {
        val proof: Div = n
        ()
      }
    ))

    el.amend(
      onMountUnmountCallback[Div]( // @Note "Div" type param is required only in scala 2.12
        mount = c => {
          val proof: MountContext[Div] = c
          ()
        },
        unmount = n => {
          val proof: Div = n
          ()
        }
      )
    )

    mount(el)
  }

  it("onMountBind with implicit setters syntax") {

    val el = div("Hello world")

    val bus = new EventBus[Int]
    val state = Var(5)
    val signal = state.signal
    val stream = signal.changes
    val observable: Observable[Int] = stream

    // @TODO[API] Can we have type inference for this [Int]?
    el.amend(
      onMountBind(_ => observable --> Observer[Int](num => num * 5)),
      onMountBind(_ => signal --> Observer[Int](num => num * 5)),
      onMountBind(_ => stream --> Observer[Int](num => num * 5))
    )

    el.amend(
      onMountInsert[Div](_ => div()) // @Note "Div" type param is required only in scala 2.12
    )

    el.amend(
      onMountBind(_ => observable --> (num => num * 5)),
      onMountBind(_ => signal --> (num => num * 5)),
      onMountBind(_ => stream --> (num => num * 5))
    )

    el.amend(
      onMountBind(_ => onClick --> Observer[dom.MouseEvent](ev => ())),
      onMountBind(_ => onClick.mapTo(1) --> Observer[Int](num => num * 5))
    )

    el.amend(
      onMountBind(_ => observable --> ((num: Int) => num * 5)),
      onMountBind(_ => signal --> ((num: Int) => num * 5)),
      onMountBind(_ => stream --> ((num: Int) => num * 5))
    )

    el.amend(
      onMountBind[Div](_.thisNode.events(onClick).map(_ => 1) --> (num => num * 5)) // @Note "Div" type param is required only in scala 2.12
    )

    el.amend(
      onMountBind[Div](_ => stream --> bus.writer) // @Note "Div" type param is required only in scala 2.12
    )

    mount(el)
  }

  it("amend with implicit setters syntax") {

    val el = div("Hello world")

    val bus = new EventBus[Int]
    val state = Var(5)
    val signal = state.signal
    val stream = signal.changes
    val observable: Observable[Int] = stream

    // @TODO[API] Can we have type inference for this [Int]?
    el.amend(observable --> Observer[Int](num => num * 5))
    el.amend(signal --> Observer[Int](num => num * 5))
    el.amend(stream --> Observer[Int](num => num * 5))

    el.amend(observable --> (num => num * 5))
    el.amend(signal --> (num => num * 5))
    el.amend(stream --> (num => num * 5))

    el.amend(onClick --> Observer[dom.MouseEvent](ev => ()))
    el.amend(onClick.mapTo(1) --> Observer[Int](num => num * 5))

    el.amend(observable --> ((num: Int) => num * 5))
    el.amend(signal --> ((num: Int) => num * 5))
    el.amend(stream --> ((num: Int) => num * 5))

    el.amendThis(_ => stream --> bus.writer)

    mount(el)
  }

  it("amend and amendThis on inlined element") {
    // https://github.com/raquo/Laminar/issues/54

    div().amend(
      cls := "foo"
    )

    div().amend(
      cls := "foo",
      cls := "bar"
    )

    div().amendThis { thisNode =>
      val node = thisNode: Div // assert
      cls := "foo"
    }
  }

}
