package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

class MountHooksSpec extends UnitSpec {

  it ("onMountCallback infers precise type and provides implicit owner") {

    var value = 0

    val bus = new EventBus[Int]
    val observer = Observer[Int](ev => value = ev)

    val el = div("Hello", onMountCallback(ctx => {
      import ctx.owner
      val x: Div = ctx.thisNode
      bus.events.addObserver(observer) // using owner implicitly
    }))

    // --

    bus.writer.onNext(1)

    assert(value == 0) // not subscribed yet

    mount(el)

    bus.writer.onNext(2)

    assert(value == 2)
  }

  it ("onMountBind works and infers precise type") {

    var value = 0
    var numMountCalls = 0

    val bus = new EventBus[Int]
    val observer = Observer[Int](ev => value = ev)

    val el = div("Hello", onMountBind(ctx => {
      val x: Div = ctx.thisNode
      numMountCalls += 1
      bus.events --> observer
    }))

    // --

    bus.writer.onNext(1)

    assert(value == 0) // not subscribed yet
    assert(numMountCalls == 0)

    // --

    mount(el)

    assert(value == 0)
    assert(numMountCalls == 1)

    numMountCalls = 0

    // --

    bus.writer.onNext(2)

    assert(value == 2)
    assert(numMountCalls == 0)

    // --

    unmount()

    assert(value == 2)
    assert(numMountCalls == 0)

    // --

    bus.writer.onNext(3) // unmounted

    assert(value == 2)
    assert(numMountCalls == 0)

    // --

    mount(el)

    assert(value == 2)
    assert(numMountCalls == 1)

    numMountCalls = 0

    // --

    bus.writer.onNext(4)

    assert(value == 4)

    assert(numMountCalls == 0)
  }

  it ("onMountCallback and onUnmountCallback work for repeated mounting") {

    var numMounts = 0
    var numUnmounts = 0

    val el = div(
      "Hello",
      onMountCallback(_ => numMounts += 1),
      onUnmountCallback(_ => numUnmounts += 1)
    )

    assert(numMounts == 0)
    assert(numUnmounts == 0)

    // --

    mount(el)

    assert(numMounts == 1)
    assert(numUnmounts == 0)

    numMounts = 0

    // --

    unmount()

    assert(numMounts == 0)
    assert(numUnmounts == 1)

    numUnmounts = 0

    // --

    mount(el)

    assert(numMounts == 1)
    assert(numUnmounts == 0)

    numMounts = 0

    // --

    unmount()

    assert(numMounts == 0)
    assert(numUnmounts == 1)

    numUnmounts = 0
  }

  it("onMountInsert child") {

    val nameVar = Var[String]("Nikita")
    var numMountCalls = 0

    val signal = nameVar.signal
    val stream = signal.changes

    val el = div(
      "Hello ",
      onMountInsert { c =>
        numMountCalls += 1
        child <-- signal.map(name => span(name))
      },
      onMountInsert { c =>
        child <-- stream.map(name => div(name))
      },
      " world"
    )

    withClue("before first mount:") {
      expectNode(el.ref, div like("Hello ", ExpectedNode.comment(), ExpectedNode.comment(), " world"))

      assert(numMountCalls == 0) // important part of API. We reserve the spot but don't call the hook yet.
    }

    withClue("first mount:") {

      mount(el)

      expectNode(div like("Hello ", span like ("Nikita"), ExpectedNode.comment(), " world"))
      assert(numMountCalls == 1)

      numMountCalls = 0
    }

    withClue("first mounted event:") {
      nameVar.writer.onNext("Yan")

      expectNode(div like("Hello ", span like ("Yan"), div like ("Yan"), " world"))
      assert(numMountCalls == 0)
    }

    withClue("first unmounted event:") {

      unmount()

      nameVar.writer.onNext("Igor") // Var updates, but element won't be populated until re-mounted

      expectNode(el.ref, div like("Hello ", span like ("Yan"), div like ("Yan"), " world"), "unmounted")
      assert(numMountCalls == 0)
    }


    withClue("unmounted event with other obs:") {

      val owner = new TestableOwner
      signal.foreach(_ => ())(owner)
      stream.foreach(_ => ())(owner)
      nameVar.writer.onNext("Igor2") // this value will be resurrected when remounting, and DOM nodes will be fine
      owner.killSubscriptions()

      expectNode(el.ref, div like("Hello ", span like ("Yan"), div like ("Yan"), " world"), "unmounted")
      assert(numMountCalls == 0)
      assert(signal.now() == "Igor2")
    }

    withClue("re-mount:") {

      mount(el)

      expectNode(div like("Hello ", span like ("Igor2"), div like ("Yan"), " world"))
      assert(numMountCalls == 1)

      numMountCalls = 0
    }

    withClue("first event after re-mounting:") {
      nameVar.writer.onNext("Elan")

      expectNode(div like("Hello ", span like ("Elan"), div like ("Elan"), " world"))
      assert(numMountCalls == 0)
    }
  }

  it("onMountBind cancels previous dynamic subscriptions on unmount") {

    val num = Var(1)

    var numBindCalls = 0
    var numSignalCalls = 0

    val signal = num.signal.map(n => {
      numSignalCalls += 1
      "x" + n
    })

    val el = div(
      "Hello ",
      onMountBind { _ =>
        numBindCalls += 1
        title <-- signal
      },
      " world"
    )

    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)

    assert(numBindCalls == 10)
    assert(numSignalCalls == 1)

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 2 // one is onMountBind itself, the other is its payload
  }

  it("onMountInsert child cancels previous dynamic subscriptions on unmount") {

    val num = Var(1)

    var numBindCalls = 0
    var numSignalCalls = 0

    val signal = num.signal.map(n => {
      numSignalCalls += 1
      span("x" + n)
    })

    val el = div(
      "Hello ",
      onMountInsert { _ =>
        numBindCalls += 1
        child <-- signal
      },
      " world"
    )

    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)

    assert(numBindCalls == 10)
    assert(numSignalCalls == 1)

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3 // one is onMountBind itself, the other is its payload, and one more is the child's pilot subscription
  }

  it("onMountInsert children cancels previous dynamic subscriptions on unmount") {

    val num = Var(1)

    var numBindCalls = 0
    var numSignalCalls = 0

    val signal = num.signal.map(n => {
      numSignalCalls += 1
      span("a" + n) :: span("b" + n) :: span("c" + n) :: Nil
    })

    val el = div(
      "Hello ",
      onMountInsert { _ =>
        numBindCalls += 1
        children <-- signal
      },
      " world"
    )

    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)

    assert(numBindCalls == 10)
    assert(numSignalCalls == 1)

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 5 // one is onMountBind itself, the other is its payload, and three more for each child's pilot subscription
  }

  it("onFocusMount") {

    val el = div(
      onMountFocus, // I'd love to avoid this line compiling, but I can't.
      input(defaultValue("Your name "), onMountFocus),
      textArea(defaultValue("Your name "), onMountFocus)
    )

    mount(el)
  }
}
