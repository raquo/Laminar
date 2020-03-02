package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js

class BetterSyntaxSpec extends UnitSpec {

  // Scala 2.12 is not as good at type inference so we have similar but weaker tests for it in test/scala.

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
      onMountUnmountCallback(
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
      onMountInsert(_ => div())
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
      onMountBind(_.thisNode.events(onClick).map(_ => 1) --> (num => num * 5))
    )

    el.amend(
      onMountBind(_ => stream --> bus.writer)
    )

    mount(el)
  }
}
